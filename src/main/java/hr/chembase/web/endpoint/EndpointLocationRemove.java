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
import hr.chembase.web.model.JSONLocationManagementRequest;
import hr.chembase.web.model.JSONLocationManagementResponse;
import hr.chembase.web.model.ObjectLocation;
import hr.chembase.web.model.SelectableLocation;
import hr.chembase.web.utils.CryptoUtils;
import hr.chembase.web.utils.HTTPResponseMessages;
import hr.chembase.web.utils.Utils;

@Path("/locations/remove")
public class EndpointLocationRemove extends ResourceConfig {

    private Logger logger = LoggerFactory.getLogger("chembase-logger");

    /* ___________________________________________________________________________________________________________________________ */

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeLocation(@CookieParam("sessionID") Cookie sessionCookie, JSONLocationManagementRequest request)
    {
        long startTime                               = System.nanoTime();       
        boolean VALIDATION_STATUS                    = true;
        boolean RETURN_STATUS                        = false;
        String RETURN_MESSAGE                        = HTTPResponseMessages.HTTP_ERROR;
        String sessionID                             = null;
        Integer locationID                           = null;
        List<SelectableLocation> selectableLocations = null;
        List<ObjectLocation> locations               = null;
        
        if (sessionCookie != null)
            sessionID = sessionCookie.getValue();
        
        if (request != null)
            locationID = request.getId();

        MDC.put("sessionID", (sessionID != null ? sessionID.substring(0,10) : ""));
        logger.info("ENDPOINT: /locations/remove");
        logger.info("Retrieved params:");
        logger.info("sessionID: " + (sessionID != null ? sessionID : ""));
        logger.info("id: "        + (locationID != null ? locationID : ""));

        /* _____________________________________________________________________________ */
        
        logger.info("Performing validation...");
        if (sessionID == null || sessionID.length() == 0)
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.HTTP_SESSION_ID_INVALID;
            logger.info("Validation fail => missing sessionID!");
        }
        else if (locationID == null)
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.HTTP_MISSING_PARAMS_IN_REQUEST;
            logger.info("Validation fail => missing id!");
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
                    logger.info("Database connection established...");

                    logger.info("Checking sessionID...");
                    int sessionValidationStatus = CryptoUtils.validateSessionID(connection, sessionID);
                    if (sessionValidationStatus == CryptoUtils.SESSION_ID_INVALID)
                    {
                        RETURN_MESSAGE = HTTPResponseMessages.HTTP_SESSION_ID_INVALID;
                        logger.info("SessionID invalid!");
                    }
                    else
                    {
                        logger.info("SessionID valid!");

                        logger.info("Checking if a location can be removed...");
                        statement = connection.prepareStatement(SQLStatements.CHECK_IF_DELETABLE_LOCATION_SQL);
                        statement.setInt(1, locationID);
                        resultSet = statement.executeQuery();

                        if (resultSet.next())
                        {
                            logger.info("Action not allowed => Location can't be deleted from the database!");
                            RETURN_MESSAGE = HTTPResponseMessages.HTTP_FORBIDDEN;
                        }
                        else
                        {
                            try
                            {
                                if (resultSet != null) resultSet.close();
                                if (statement != null) statement.close();
                            }
                            catch (Exception ex) {}
                            
                            logger.info("Deleting location...");
                            statement = connection.prepareStatement(SQLStatements.DELETE_LOCATION_SQL);
                            statement.setInt(1, locationID);
                            statement.execute();

                            try { if (statement != null) statement.close(); }
                            catch (Exception ex) {}

                            logger.info("Retrieving updated locations...");
                            locations           = new LinkedList<ObjectLocation>();
                            selectableLocations = new LinkedList<SelectableLocation>();
                            
                            final SelectableLocation emptyLocation = new SelectableLocation();
                            emptyLocation.setSelectval(0);
                            emptyLocation.setDisplayval("");
                            selectableLocations.add(emptyLocation);
                            
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
                            
                            RETURN_STATUS = true;
                            RETURN_MESSAGE = HTTPResponseMessages.HTTP_OK;
                        }
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
            final JSONLocationManagementResponse response = new JSONLocationManagementResponse();
            response.setLocations(locations);
            response.setSelectableLocations(selectableLocations);
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
