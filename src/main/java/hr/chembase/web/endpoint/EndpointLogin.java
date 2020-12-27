package hr.chembase.web.endpoint;

import java.sql.Connection;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import hr.chembase.web.db.DBConnection;
import hr.chembase.web.model.JSONErrorResponse;
import hr.chembase.web.model.JSONLoginRequest;
import hr.chembase.web.model.JSONLoginResponse;
import hr.chembase.web.utils.CryptoUtils;
import hr.chembase.web.utils.HTTPResponseMessages;
import hr.chembase.web.utils.Utils;

@Path("/login")
public class EndpointLogin extends ResourceConfig {
    
    private Logger logger = LoggerFactory.getLogger("chembase-logger");

    /* ___________________________________________________________________________________________________________________________ */

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response performLogin(JSONLoginRequest request)
    {
        long startTime            = System.nanoTime();
        boolean VALIDATION_STATUS = true;
        boolean RETURN_STATUS     = false;
        String RETURN_MESSAGE     = HTTPResponseMessages.HTTP_MISSING_PARAMS_IN_REQUEST;
        String sessionID          = null;
        String username           = null;
        String password           = null;

        if (request != null)
        {
            username = request.getUsername();
            password = request.getPassword();

            if (username != null) username = username.trim();
            if (password != null) password = password.trim();
        }

        MDC.put("sessionID", "");
        logger.info("ENDPOINT: /login");
        logger.info("Retrieved params:");
        logger.info("username: "    + (username != null ? username : ""));
        logger.info("password: "    + (password != null ? password : ""));

        /* _____________________________________________________________________________ */

        logger.info("Performing validation...");
        if (username == null || username.length() == 0)
        {
            VALIDATION_STATUS = false;
            RETURN_MESSAGE = HTTPResponseMessages.HTTP_MISSING_PARAMS_IN_REQUEST;
            logger.info("Validation fail => missing username!");
        }
        else if (username == null || username.length() == 0)
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
            Connection connection = null;
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
                    logger.info("Validating login credentials...");

                    boolean loginCredentialsValid = CryptoUtils.validateLoginCredentials(connection, username, password);
                    if (!loginCredentialsValid)
                    {
                        CryptoUtils.incrementInvalidPasswordCounter(connection, username);
                        boolean accountLocked = CryptoUtils.lockAccountIfConditionsAreMet(connection, username);
                        if (accountLocked)
                        {
                            logger.info("Invalid login credentials!");
                            logger.info("Account Locked!");
                            RETURN_MESSAGE = HTTPResponseMessages.HTTP_LOGIN_FAIL_LOCKED;
                        }
                        else
                        {
                            logger.info("Invalid login credentials!");
                            RETURN_MESSAGE = HTTPResponseMessages.HTTP_LOGIN_FAIL;
                        }
                    }
                    else
                    {
                        boolean accountLocked = CryptoUtils.checkIfAccountLocked(connection, username);
                        if (accountLocked)
                        {
                            logger.info("Account Locked!");
                            RETURN_MESSAGE = HTTPResponseMessages.HTTP_LOGIN_FAIL_LOCKED;
                        }
                        else
                        {
                            logger.info("Valid login credentials!");

                            CryptoUtils.deleteSessionIDForUser(connection, username);
                            final String tempSessionID = CryptoUtils.generateSessionID();
                            sessionID = tempSessionID;
                            CryptoUtils.insertSessionIDRecord(connection, username, sessionID);

                            RETURN_STATUS = true;
                            RETURN_MESSAGE = HTTPResponseMessages.HTTP_OK;
                            logger.info("Login success...");                                
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                logger.info(Utils.generateStackTrace(ex));
                RETURN_MESSAGE = HTTPResponseMessages.HTTP_ERROR;
            }
            finally
            {
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
            final JSONLoginResponse response = new JSONLoginResponse();
            response.setStatus(RETURN_MESSAGE);
            logger.info("Returning HTTP 200");
            return Response.ok(response).cookie(new NewCookie("sessionID",                  // Label
                                                              sessionID,                    // Value
                                                              "ChemBase/v1/",               // Path
                                                              null,                         // Domain
                                                              null,                         // Comment
                                                              NewCookie.DEFAULT_MAX_AGE,    // Cookie expires with the current application/browser session
                                                              false,                        // Secure Flag => (Change to true if code in production to require TLS)
                                                              true                          // HTTP Only Flag
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
