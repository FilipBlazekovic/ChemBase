package hr.chembase.web.endpoint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
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
import hr.chembase.web.model.JSONInitResponse;
import hr.chembase.web.model.ObjectChemical;
import hr.chembase.web.model.ObjectLocation;
import hr.chembase.web.model.ObjectUser;
import hr.chembase.web.model.SelectableLocation;
import hr.chembase.web.utils.CryptoUtils;
import hr.chembase.web.utils.HTTPResponseMessages;
import hr.chembase.web.utils.Utils;

@Path("/init")
public class EndpointInit extends ResourceConfig {

    private Logger logger = LoggerFactory.getLogger("chembase-logger");
    
    /* ___________________________________________________________________________________________________________________________ */

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response processInitRequest(@CookieParam("sessionID") Cookie sessionCookie)
    {
        long startTime                               = System.nanoTime();
        boolean VALIDATION_STATUS                    = true;
        boolean RETURN_STATUS                        = false;
        String RETURN_MESSAGE                        = HTTPResponseMessages.HTTP_ERROR;
        String sessionID                             = null;
        Integer adminUser                            = 0;
        List<SelectableLocation> selectableLocations = null;
        List<ObjectLocation> locations               = null;
        List<ObjectUser> users                       = null;
        List<ObjectChemical> chemicals               = null;

        if (sessionCookie != null)
            sessionID = sessionCookie.getValue();

        MDC.put("sessionID", (sessionID != null ? sessionID.substring(0,10) : ""));
        logger.info("ENDPOINT: /init");
        logger.info("Retrieved params:");
        logger.info("sessionID: " + (sessionID != null ? sessionID : ""));

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
                        
                        if (sessionValidationStatus == CryptoUtils.SESSION_ID_VALID_AND_USER_ADMIN)
                            adminUser = 1;
                        
                        users     = new LinkedList<ObjectUser>();
                        locations = new LinkedList<ObjectLocation>();
                        chemicals = new LinkedList<ObjectChemical>();
                        
                        selectableLocations = new LinkedList<SelectableLocation>();

                        /* ------------------------------------------------------------------ */
                        
                        final SelectableLocation emptyLocation = new SelectableLocation();
                        emptyLocation.setSelectval(0);
                        emptyLocation.setDisplayval("");
                        selectableLocations.add(emptyLocation);
                        
                        /* ------------------------------------------------------------------ */
                                                
                        // Retrieving users
                        // ----------------
                        logger.info("Retrieving users...");
                        statement = connection.prepareStatement(SQLStatements.GET_USERS_SQL);
                        resultSet = statement.executeQuery();
                        while (resultSet.next())
                        {                           
                            final ObjectUser currentUser = new ObjectUser();
                            currentUser.setId(resultSet.getInt(1));
                            currentUser.setUsername(resultSet.getString(2));
                            currentUser.setLocked(resultSet.getString(3));
                            currentUser.setLockDate(resultSet.getString(4));
                            users.add(currentUser);
                        }
                        
                        try
                        {
                            if (resultSet != null) resultSet.close();
                            if (statement != null) statement.close();
                        }
                        catch (Exception ex) {}
                        
                        /* ------------------------------------------------------------------ */
                    
                        // Retrieving locations
                        // --------------------
                        logger.info("Retrieving locations...");
                        statement = connection.prepareStatement(SQLStatements.GET_LOCATIONS_SQL);
                        resultSet = statement.executeQuery();
                        while (resultSet.next())
                        {
                            final ObjectLocation currentLocation = new ObjectLocation();
                            currentLocation.setId(resultSet.getInt(1));
                            currentLocation.setLocation(resultSet.getString(2));
                            locations.add(currentLocation);

                            final SelectableLocation selectableLocation = new SelectableLocation();
                            selectableLocation.setSelectval(resultSet.getInt(1));
                            selectableLocation.setDisplayval(resultSet.getString(2));
                            selectableLocations.add(selectableLocation);
                        }
                        
                        try
                        {
                            if (resultSet != null) resultSet.close();
                            if (statement != null) statement.close();
                        }
                        catch (Exception ex) {}

                        /* ------------------------------------------------------------------ */

                        // Retrieving newest 1000 chemicals
                        // --------------------------------
                        logger.info("Retrieving chemicals...");
                        statement = connection.prepareStatement(SQLStatements.GET_CHEMICALS_SQL);
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

                        /* ------------------------------------------------------------------ */
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
            final JSONInitResponse response = new JSONInitResponse();
            response.setUsers(users);
            response.setLocations(locations);
            response.setChemicals(chemicals);
            response.setSelectableLocations(selectableLocations);
            response.setAdminUser(adminUser);
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
