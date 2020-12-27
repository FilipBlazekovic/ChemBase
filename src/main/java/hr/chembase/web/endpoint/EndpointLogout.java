package hr.chembase.web.endpoint;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import hr.chembase.web.db.DBConnection;
import hr.chembase.web.db.SQLStatements;
import hr.chembase.web.model.JSONErrorResponse;
import hr.chembase.web.model.JSONLogoutResponse;
import hr.chembase.web.utils.HTTPResponseMessages;
import hr.chembase.web.utils.Utils;

@Path("/logout")
public class EndpointLogout extends ResourceConfig {

    private Logger logger = LoggerFactory.getLogger("chembase-logger");

    /* ___________________________________________________________________________________________________________________________ */
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response performLogout(@CookieParam("sessionID") Cookie sessionCookie)
    {
        long startTime            = System.nanoTime();  
        boolean VALIDATION_STATUS = true;
        boolean RETURN_STATUS     = false;
        String RETURN_MESSAGE     = HTTPResponseMessages.HTTP_SESSION_ID_INVALID;       
        String sessionID          = null;

        if (sessionCookie != null)
            sessionID = sessionCookie.getValue();
        
        MDC.put("sessionID", (sessionID != null ? sessionID.substring(0,10) : ""));
        logger.info("ENDPOINT: /logout");
        logger.info("Retrieved params:");
        logger.info("sessionID: " + (sessionID != null ? sessionID : ""));

        logger.info("Performing validation...");
        if (sessionID == null || sessionID.length() == 0)
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.HTTP_SESSION_ID_INVALID;
            logger.info("Validation fail => missing sessionID!");
        }
        
        /* _______________________________________________________________________ */

        if (VALIDATION_STATUS)
        {
            logger.info("Validation success!");
            logger.info("Establishing database connection...");
            
            Connection connection       = null;
            PreparedStatement statement = null;
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
                    logger.info("Deleting session from the database...");
                    
                    statement = connection.prepareStatement(SQLStatements.DELETE_SESSION_SQL);
                    statement.setString(1, sessionID);
                    statement.execute();
                }
            }
            catch (Exception ex)
            {
                logger.info(Utils.generateStackTrace(ex));
                RETURN_MESSAGE = HTTPResponseMessages.HTTP_SQL_EXCEPTION;
            }
            finally
            {
                try { if (statement != null) statement.close(); }
                catch (Exception ex) {}
                
                try { DBConnection.closeDBConnection(connection); }
                catch (Exception ex) {}
            }
        }

        /* _______________________________________________________________________ */
        
        long endTime = System.nanoTime();
        logger.info("PROCESSING_TIME: " + ((endTime - startTime)/1000000) + " ms");


        // Returning a response in case of success
        // ---------------------------------------
        if (RETURN_STATUS)
        {
            final JSONLogoutResponse response = new JSONLogoutResponse();
            response.setStatus(RETURN_MESSAGE);
            logger.info("Returning HTTP 200");
            return Response.ok(response).cookie(new NewCookie("sessionID",                      // Label
                                                              null,                             // Value
                                                              "ChemBase/v1/",                   // Path
                                                              null,                             // Domain
                                                              null,                             // Comment
                                                              NewCookie.DEFAULT_MAX_AGE,        // Cookie expires with the current application/browser session
                                                              false,                            // Secure Flag => (Change to true if code in production to require TLS)
                                                              true                              // HTTP Ony Flag
                                                )).build();
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
