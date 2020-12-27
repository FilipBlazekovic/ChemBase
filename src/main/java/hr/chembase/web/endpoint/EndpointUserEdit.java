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
import hr.chembase.web.model.JSONUserManagementRequest;
import hr.chembase.web.model.JSONUserManagementResponse;
import hr.chembase.web.model.ObjectUser;
import hr.chembase.web.utils.CryptoUtils;
import hr.chembase.web.utils.HTTPResponseMessages;
import hr.chembase.web.utils.Utils;

@Path("/users/edit")
public class EndpointUserEdit extends ResourceConfig {

    private Logger logger = LoggerFactory.getLogger("chembase-logger");

    /* ___________________________________________________________________________________________________________________________ */

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editUser(@CookieParam("sessionID") Cookie sessionCookie, JSONUserManagementRequest request)
    {
        long startTime            = System.nanoTime();
        boolean VALIDATION_STATUS = true;
        boolean RETURN_STATUS     = false;
        String RETURN_MESSAGE     = HTTPResponseMessages.HTTP_ERROR;
        String sessionID          = null;
        Integer userID            = null;
        String password           = null;
        List<ObjectUser> users    = null;

        if (sessionCookie != null)
            sessionID = sessionCookie.getValue();

        if (request != null)
        {
            userID = request.getId();
            password = request.getPassword();
            if (password != null)
                password = password.trim();
        }

        MDC.put("sessionID", (sessionID != null ? sessionID.substring(0,10) : ""));
        logger.info("ENDPOINT: /users/edit");
        logger.info("Retrieved params:");
        logger.info("id: "       + (userID != null ? userID : ""));
        logger.info("password: " + (password != null ? password : ""));
        
        /* _____________________________________________________________________________ */

        logger.info("Performing validation...");
        if (sessionID == null || sessionID.length() == 0)
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.HTTP_SESSION_ID_INVALID;
            logger.info("Validation fail => missing sessionID!");
        }
        else if (userID == null)
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.HTTP_MISSING_PARAMS_IN_REQUEST;
            logger.info("Validation fail => missing id!");
        }
        else if (password == null || password.length() == 0)
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.HTTP_MISSING_PARAMS_IN_REQUEST;
            logger.info("Validation fail => missing password!");
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
                    else if (sessionValidationStatus != CryptoUtils.SESSION_ID_VALID_AND_USER_ADMIN)
                    {
                        logger.info("SessionID valid!");
                        logger.info("Action not allowed => Only admin user can perform user-management!");                      
                        RETURN_MESSAGE = HTTPResponseMessages.HTTP_FORBIDDEN;
                    }
                    else
                    {
                        logger.info("SessionID valid!");
                        
                        final String salt = CryptoUtils.generateSalt();
                        final String passwordHash = CryptoUtils.generatePasswordHash(password, salt);
                        statement = connection.prepareStatement(SQLStatements.EDIT_USER_SQL);
                        statement.setString(1, passwordHash);
                        statement.setString(2, salt);
                        statement.setInt(3, userID);
                        statement.execute();

                        try { if (statement != null) statement.close(); }
                        catch (Exception ex) {}
                        
                        logger.info("Retrieving updated users...");
                        users     = new LinkedList<ObjectUser>();
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
            final JSONUserManagementResponse response = new JSONUserManagementResponse();
            response.setUsers(users);
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
