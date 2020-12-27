package hr.chembase.web.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hr.chembase.web.model.JSONErrorResponse;
import hr.chembase.web.utils.HTTPResponseMessages;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private Logger logger = LoggerFactory.getLogger("chembase-logger");

    @Override
    public Response toResponse(Throwable exception)
    {   
        logger.info("*** EXECPTION ***");
        logger.info("Error message: " + exception.getMessage());
        final StackTraceElement[] stackTrace = exception.getStackTrace();
        for (int i = 0; i < stackTrace.length; i++)
        {
            logger.info(stackTrace[i].toString());
        }
        
        final JSONErrorResponse jsonResponse = new JSONErrorResponse();
        jsonResponse.setError(HTTPResponseMessages.HTTP_ERROR);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
    }
}
