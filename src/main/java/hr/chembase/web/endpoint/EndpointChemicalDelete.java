package hr.chembase.web.endpoint;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
import hr.chembase.web.model.JSONErrorResponse;
import hr.chembase.web.utils.CryptoUtils;
import hr.chembase.web.utils.HTTPResponseMessages;
import hr.chembase.web.utils.Utils;

@Path("/chemicals/delete")
public class EndpointChemicalDelete extends ResourceConfig {

    private Logger logger = LoggerFactory.getLogger("chembase-logger");

    /* ___________________________________________________________________________________________________________________________ */

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteChemical(@CookieParam("sessionID") Cookie sessionCookie, JSONChemicalManagementRequest request)
    {
        long startTime              = System.nanoTime();
        boolean RETURN_STATUS       = false;
        boolean VALIDATION_STATUS   = true;
        String RETURN_MESSAGE       = HTTPResponseMessages.HTTP_ERROR;
        String sessionID            = null;
        Integer id                  = null;

        if (sessionCookie != null)
            sessionID = sessionCookie.getValue(); 
        
        if (request != null)
            id = request.getId();

        MDC.put("sessionID", (sessionID != null ? sessionID.substring(0,10) : ""));
        logger.info("ENDPOINT: /chemicals/delete");
        logger.info("Retrieved params:");
        logger.info("sessionID: " + (sessionID != null ? sessionID : ""));
        logger.info("id: "        + (id != null ? id : ""));

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
        
        /* _____________________________________________________________________________ */
        
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

                    int sessionValidationStatus = CryptoUtils.validateSessionID(connection, sessionID);
                    if (sessionValidationStatus == CryptoUtils.SESSION_ID_INVALID)
                    {
                        RETURN_MESSAGE = HTTPResponseMessages.HTTP_SESSION_ID_INVALID;
                        logger.info("SessionID invalid!");
                    }
                    else
                    {
                        logger.info("SessionID valid!");
                        logger.info("Deleting chemical...");

                        statement = connection.prepareStatement(SQLStatements.DELETE_CHEMICAL_SQL);
                        statement.setInt(1, id);
                        statement.execute();

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
                try { if (statement != null) statement.close(); }
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
            logger.info("Returning HTTP 200");
            return Response.ok().build();
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
