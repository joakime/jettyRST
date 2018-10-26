package arrays_reverser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("reverse-arrays")
public class ArraysReverserEndpoints
{
    private final static Logger LOG = Logger.getLogger(ArraysReverserEndpoints.class.getName());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<List<Integer>> reverseArrays(@Context HttpServletRequest request, List<List<Integer>> sequences) throws InterruptedException
    {
        // If we reached this point, Jersey and Jackson has read the entire JSON from the
        // HttpServletRequest.getInputStream and left the InputStream unconsumed.
        // On persistent connections, it is important to read (from the network) the entire
        // request body content (till -1).
        // For a container, where is the end of request body content?
        // For chunked request body content, which has no Content-Length, the final chunk
        // indicator ("0\r\n\r\n") is the end of the body content.
        // For requests with Content-Length, merely reading enough bytes from the network to
        // satisfy the Content-Length is sufficient.
        consumeAll(request);
        return ArraysReverser.reverse(sequences);
    }

    private void consumeAll(HttpServletRequest request)
    {
        try
        {
            int length = 0;
            InputStream in = request.getInputStream();
            // read till EOF (-1)
            while (in.read() != -1) {
                length++;
            }
            if (length > 0)
                LOG.info("consumeAll read " + length + " bytes");
        }
        catch (IOException ignore)
        {
        }
    }
}
