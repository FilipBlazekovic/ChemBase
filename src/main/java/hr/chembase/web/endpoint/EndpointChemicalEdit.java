package hr.chembase.web.endpoint;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

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
import hr.chembase.web.model.JSONChemicalManagementRequest;
import hr.chembase.web.model.JSONChemicalManagementResponse;
import hr.chembase.web.model.JSONErrorResponse;
import hr.chembase.web.model.ObjectChemical;
import hr.chembase.web.utils.CryptoUtils;
import hr.chembase.web.utils.HTTPResponseMessages;
import hr.chembase.web.utils.Utils;

@Path("/chemicals/edit")
public class EndpointChemicalEdit extends ResourceConfig {

    private Logger logger = LoggerFactory.getLogger("chembase-logger");

    private static final String[] unitsString = { "g","kg","ml","l","cm3","dm3", "mol", "mmol" };

    /* ___________________________________________________________________________________________________________________________ */

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editChemical(@CookieParam("sessionID") Cookie sessionCookie, JSONChemicalManagementRequest request)
    {
        long startTime                = System.nanoTime();
        ObjectChemical editedChemical = null;
        boolean RETURN_STATUS         = false;
        boolean VALIDATION_STATUS     = true;
        String RETURN_MESSAGE         = HTTPResponseMessages.HTTP_ERROR;
        String sessionID              = null;
        String chemicalName           = null;
        String bruttoFormula          = null;
        String molarMass              = null;       
        String unit                   = null;
        String manufacturer           = null;
        String supplier               = null;
        String additionalInfo         = null;
        Integer storageLocation       = null;
        Integer id                    = null;
        BigDecimal amount             = null;

        if (sessionCookie != null)
            sessionID = sessionCookie.getValue();

        if (request != null)
        {
            id              = request.getId();
            chemicalName    = request.getChemicalName();
            bruttoFormula   = request.getBruttoFormula();
            molarMass       = request.getMolarMass();
            amount          = request.getAmount();
            unit            = request.getUnit();
            storageLocation = request.getStorageLocation();
            manufacturer    = request.getManufacturer();
            supplier        = request.getSupplier();
            additionalInfo  = request.getAdditionalInfo();
            
            if (chemicalName != null)   chemicalName = chemicalName.trim();
            if (bruttoFormula != null)  bruttoFormula = bruttoFormula.trim().toUpperCase();
            if (molarMass != null)      molarMass = molarMass.trim();
            if (manufacturer != null)   manufacturer = manufacturer.trim();
            if (supplier != null)       supplier = supplier.trim();
            if (additionalInfo != null) additionalInfo = additionalInfo.trim();
        }
        
        MDC.put("sessionID", (sessionID != null ? sessionID.substring(0,10) : ""));
        logger.info("ENDPOINT: /chemicals/edit");
        logger.info("Retrieved params:");
        logger.info("sessionID: "       + (sessionID != null ? sessionID : ""));
        logger.info("id: "              + (id != null ? id : ""));
        logger.info("chemicalName: "    + (chemicalName != null ? chemicalName : ""));
        logger.info("bruttoFormula: "   + (bruttoFormula != null ? bruttoFormula : ""));
        logger.info("molarMass: "       + (molarMass != null ? molarMass : ""));        
        logger.info("amount: "          + (amount != null ? amount : ""));
        logger.info("unit: "            + (unit != null ? unit : ""));
        logger.info("storageLocation: " + (storageLocation != null ? storageLocation : ""));
        logger.info("manufacturer: "    + (manufacturer != null ? manufacturer : ""));
        logger.info("supplier: "        + (supplier != null ? supplier : ""));
        logger.info("additionalInfo: "  + (additionalInfo != null ? additionalInfo : ""));

        /* _____________________________________________________________________________ */
        
        logger.info("Performing validation...");
        if (sessionID == null || sessionID.length() == 0)
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.HTTP_SESSION_ID_INVALID;
            logger.info("Validation fail => missing sessionID!");
        }
        else if (id == null)
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.HTTP_MISSING_PARAMS_IN_REQUEST;
            logger.info("Validation fail => missing id!");
        }       
        else if (chemicalName == null || chemicalName.length() == 0)
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.HTTP_MISSING_PARAMS_IN_REQUEST;
            logger.info("Validation fail => missing chemicalName!");
        }
        else if (bruttoFormula == null || bruttoFormula.length() == 0)
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.HTTP_MISSING_PARAMS_IN_REQUEST;
            logger.info("Validation fail => missing bruttoFormula!");
        }
        else if (bruttoFormula != null && bruttoFormula.length() > 0 && (!bruttoFormula.matches("[A-Z0-9]+")))
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.INVALID_PARAMS_IN_REQUEST;
            logger.info("Validation fail => Invalid bruttoFormula!");
        }
        else if (molarMass == null || molarMass.length() == 0)
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.HTTP_MISSING_PARAMS_IN_REQUEST;
            logger.info("Validation fail => missing molarMass!");
        }
        else if (molarMass != null && molarMass.length() > 0 && (!molarMass.matches("\\d+(\\.\\d{1,8})?")))
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.INVALID_PARAMS_IN_REQUEST;
            logger.info("Validation fail => Invalid molarMass!");
        }
        else if (amount == null)
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.HTTP_MISSING_PARAMS_IN_REQUEST;
            logger.info("Validation fail => missing amount!");
        }
        else if (unit == null || unit.length() == 0)
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.HTTP_MISSING_PARAMS_IN_REQUEST;
            logger.info("Validation fail => missing unit!");
        }
        else if (!Arrays.asList(unitsString).contains(unit))
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.INVALID_PARAMS_IN_REQUEST;
            logger.info("Validation fail => Invalid unit!");
        }
        else if (storageLocation == null)
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.HTTP_MISSING_PARAMS_IN_REQUEST;
            logger.info("Validation fail => missing storageLocation!");
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
                        logger.info("Adding chemical...");

                        int i = 1;
                        statement = connection.prepareStatement(SQLStatements.EDIT_CHEMICAL_SQL);
                        statement.setString(i++, chemicalName);
                        statement.setString(i++, bruttoFormula);
                        statement.setString(i++, molarMass);
                        statement.setBigDecimal(i++, amount);
                        statement.setString(i++, unit);
                        statement.setInt(i++, storageLocation);
                        statement.setString(i++, manufacturer);
                        statement.setString(i++, supplier);
                        statement.setString(i++, additionalInfo);                       
                        statement.setInt(i++, id);
                        statement.execute();

                        try { if (statement != null) statement.close(); }
                        catch (Exception ex) {}

                        i = 1;
                        statement = connection.prepareStatement(SQLStatements.GET_EDITED_CHEMICAL);
                        statement.setInt(i++, id);
                        resultSet = statement.executeQuery();
                        while (resultSet.next())
                        {
                            editedChemical = new ObjectChemical();
                            editedChemical.setId(resultSet.getInt(1));
                            editedChemical.setChemicalName(resultSet.getString(2));
                            editedChemical.setBruttoFormula(resultSet.getString(3));
                            editedChemical.setMoralMass(resultSet.getString(4));
                            editedChemical.setQuantity(resultSet.getBigDecimal(5));
                            editedChemical.setUnit(resultSet.getString(6));
                            editedChemical.setStorageLocation(resultSet.getString(7));
                            editedChemical.setManufacturer(resultSet.getString(8));
                            editedChemical.setSupplier(resultSet.getString(9));
                            editedChemical.setDateOfEntry(resultSet.getString(10));
                            editedChemical.setAdditionalInfo(resultSet.getString(11));
                        }

                        try
                        {
                            if (resultSet != null) resultSet.close();
                            if (statement != null) statement.close();
                        }
                        catch (Exception ex) {}

                        RETURN_STATUS = true;
                        RETURN_MESSAGE = HTTPResponseMessages.HTTP_OK;
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
            JSONChemicalManagementResponse response = new JSONChemicalManagementResponse();
            response.setChemicals(editedChemical);
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
