package exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {
  @Override
  public Response toResponse(JsonParseException exception) {
    return Response.status(Response.Status.BAD_REQUEST).entity(exception.getClass().getSimpleName()).type("text/plain").build();
  }
}
