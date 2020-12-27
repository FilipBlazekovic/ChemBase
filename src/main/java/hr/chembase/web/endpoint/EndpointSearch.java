package hr.chembase.web.endpoint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import hr.chembase.web.db.DBConnection;
import hr.chembase.web.db.SQLStatements;
import hr.chembase.web.model.JSONErrorResponse;
import hr.chembase.web.model.JSONSearchRequest;
import hr.chembase.web.model.JSONSearchResponse;
import hr.chembase.web.model.ObjectChemical;
import hr.chembase.web.utils.CryptoUtils;
import hr.chembase.web.utils.HTTPResponseMessages;
import hr.chembase.web.utils.Utils;

@Path("/chemicals/search")
public class EndpointSearch extends ResourceConfig {

    private Logger logger = LoggerFactory.getLogger("chembase-logger");
    
    /* ___________________________________________________________________________________________________________________________ */

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response processSearchRequest(@CookieParam("sessionID") Cookie sessionCookie, JSONSearchRequest request)
    {
        long startTime                  = System.nanoTime();    
        boolean VALIDATION_STATUS       = true;
        boolean RETURN_STATUS           = false;
        String RETURN_MESSAGE           = HTTPResponseMessages.HTTP_ERROR;
        String sessionID                = null;
        String chemicalName             = null;
        String manufacturer             = null;
        String supplier                 = null;
        Integer storageLocation         = null;
        List<ObjectChemical> chemicals  = null;
        
        if (sessionCookie != null)
            sessionID = sessionCookie.getValue(); 
        
        if (request != null)
        {
            chemicalName    = request.getChemicalName();
            manufacturer    = request.getManufacturer();
            supplier        = request.getSupplier();
            storageLocation = request.getStorageLocation();
            
            if (chemicalName != null) chemicalName = chemicalName.trim();
            if (manufacturer != null) manufacturer = manufacturer.trim();
            if (supplier != null)     supplier = supplier.trim();
        }

        MDC.put("sessionID", (sessionID != null ? sessionID.substring(0,10) : ""));
        logger.info("ENDPOINT: /chemicals/search");
        logger.info("Retrieved params:");
        logger.info("sessionID: "       + (sessionID != null ? sessionID : ""));
        logger.info("chemicalName: "    + (chemicalName != null ? chemicalName : ""));
        logger.info("manufacturer: "    + (manufacturer != null ? manufacturer : ""));
        logger.info("supplier: "        + (supplier != null ? supplier : ""));
        logger.info("storageLocation: " + (storageLocation != null ? storageLocation : ""));

        logger.info("Performing validation...");
        if (sessionID == null || sessionID.length() == 0)
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.HTTP_SESSION_ID_INVALID;
            logger.info("Validation fail => missing sessionID!");
        }

        /* _____________________________________________________________________________ */
        
        if (VALIDATION_STATUS)
        {
            logger.info("Validation success!");
            logger.info("Establishing database connection...");
            
            Connection connection       = null;
            PreparedStatement statement = null;
            ResultSet resultSet         = null;
            try
            {
                connection = DBConnection.openDBConnection();
                if (connection == null)
                {
                    RETURN_MESSAGE = HTTPResponseMessages.HTTP_DATABASE_UNAVAILABLE;
                    logger.info("Database unavailable!");
                }
                else
                {
                    logger.info("Database connection established!");

                    int sessionValidationStatus = CryptoUtils.validateSessionID(connection, sessionID);
                    if (sessionValidationStatus == CryptoUtils.SESSION_ID_INVALID)
                    {
                        RETURN_MESSAGE = HTTPResponseMessages.HTTP_SESSION_ID_INVALID;
                        logger.info("SessionID invalid!");
                    }
                    else
                    {
                        logger.info("SessionID valid!");
                        logger.info("Retrieving chemicals...");
                        chemicals = new LinkedList<ObjectChemical>();
                        
                        boolean hasChemicalNameFilter    = false;
                        boolean hasManufacturerFilter    = false;
                        boolean hasSupplierFilter        = false;
                        boolean hasStorageLocationFilter = false;
                        
                        String searchSQL = SQLStatements.SEARCH_CHEMICALS_CORE_SQL;
                        
                        if (chemicalName != null && chemicalName.length() > 0)
                        {
                            hasChemicalNameFilter = true;
                            searchSQL += " AND x.chemname = ?";
                        }
                        if (manufacturer != null && manufacturer.length() > 0)
                        {
                            hasManufacturerFilter = true;
                            searchSQL += " AND x.chemmanufacturer = ?";
                        }
                        if (supplier != null && supplier.length() > 0)
                        {
                            hasSupplierFilter = true;
                            searchSQL += " AND x.chemsupplier = ?";
                        }
                        if (storageLocation != null && storageLocation.intValue() > 0)
                        {
                            hasStorageLocationFilter = true;
                            searchSQL += " AND x.chemstoragelocation = ?";
                        }

                        statement = connection.prepareStatement(searchSQL + " ORDER BY x.chemname LIMIT 1000");

                        int i = 1;
                        if (hasChemicalNameFilter)      statement.setString(i++, chemicalName);
                        if (hasManufacturerFilter)      statement.setString(i++, manufacturer);
                        if (hasSupplierFilter)          statement.setString(i++, supplier);
                        if (hasStorageLocationFilter)   statement.setInt(i++, storageLocation);
                        
                        resultSet = statement.executeQuery();
                        while (resultSet.next())
                        {
                            final ObjectChemical currentChemical = new ObjectChemical();
                            currentChemical.setId(resultSet.getInt(1));
                            currentChemical.setChemicalName(resultSet.getString(2));
                            currentChemical.setBruttoFormula(resultSet.getString(3));
                            currentChemical.setMoralMass(resultSet.getString(4));
                            currentChemical.setQuantity(resultSet.getBigDecimal(5));
                            currentChemical.setUnit(resultSet.getString(6));
                            currentChemical.setStorageLocation(resultSet.getString(7));
                            currentChemical.setManufacturer(resultSet.getString(8));
                            currentChemical.setSupplier(resultSet.getString(9));
                            currentChemical.setDateOfEntry(resultSet.getString(10));
                            currentChemical.setAdditionalInfo(resultSet.getString(11));
                            chemicals.add(currentChemical);
                        }
                        
                        try
                        {
                            if (resultSet != null) resultSet.close();
                            if (statement != null) statement.close();
                        }
                        catch (Exception ex) {}
                        
                        RETURN_MESSAGE = HTTPResponseMessages.HTTP_OK;
                        RETURN_STATUS = true;
                    }
                }
            }
            catch (Exception ex)
            {
                logger.info(Utils.generateStackTrace(ex));
                RETURN_MESSAGE = HTTPResponseMessages.HTTP_SQL_EXCEPTION;
            }
            finally
            {
                try
                {
                    if (resultSet != null) resultSet.close();
                    if (statement != null) statement.close();
                }
                catch (Exception ex) {}
                
                try { DBConnection.closeDBConnection(connection); }
                catch (Exception ex) {}
            }
        }
        
        /* _____________________________________________________________________________ */
        
        long endTime = System.nanoTime();
        logger.info("PROCESSING_TIME: " + ((endTime - startTime)/1000000) + " ms");
        
        
        // Returning a response in case of success
        // ---------------------------------------
        if (RETURN_STATUS)
        {
            final JSONSearchResponse response = new JSONSearchResponse();
            response.setChemicals(chemicals);
            logger.info("Returning HTTP 200");
            return Response.ok(response).build();
        }

        // Returning a response in case of error
        // -------------------------------------
        else if (RETURN_MESSAGE.equals(HTTPResponseMessages.HTTP_DATABASE_UNAVAILABLE) ||
                 RETURN_MESSAGE.equals(HTTPResponseMessages.HTTP_SQL_EXCEPTION) ||
                 RETURN_MESSAGE.equals(HTTPResponseMessages.HTTP_ERROR))
        {
            final JSONErrorResponse response = new JSONErrorResponse();
            response.setError(RETURN_MESSAGE);
            logger.info("Returning HTTP 500 => " + RETURN_MESSAGE);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }   
        else
        {
            final JSONErrorResponse response = new JSONErrorResponse();
            response.setError(RETURN_MESSAGE);          
            logger.info("Returning HTTP 400 => " + RETURN_MESSAGE);
            return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
        }
    }

    /* ___________________________________________________________________________________________________________________________ */

}
