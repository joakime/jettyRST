package arrays_reverser;

import java.util.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.*;

@Path("reverse-arrays")
public class ArraysReverserEndpoints {
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public List<List<Integer>> reverseArrays(List<List<Integer>> sequences) throws InterruptedException {
    return ArraysReverser.reverse(sequences);
  }
}
