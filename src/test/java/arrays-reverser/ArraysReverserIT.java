import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.junit.*;

import static org.junit.Assert.*;

import javax.ws.rs.client.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ws.rs.core.GenericType;

public class ArraysReverserIT {
  private static String ENDPOINT = "http://localhost:8080/reverse-arrays";


  String s1 = "[[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9]]";
  String s2 = "[[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9]]";
  String s3 = "[[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9]]";

  volatile boolean stop = false;


  @Test
  public void multiThreded() throws InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(5);
    Runnable worker = () -> {
      try {
        simpleSingle();
      } catch (Exception e) {
        e.printStackTrace();
        stop = true;
        throw e;
      }
    };
    for (int i = 0; i < 5; i++) {
      new Thread(worker).start();
    }
    while (!stop) {
      Thread.sleep(1000);
    }
  }


  public void simpleSingle() {
    ClientConfig clientConfig = new ClientConfig();

    int readTimeoutMS = 5 * 60 * 1000;
    int connectTimeoutMS = 60 * 1000;
    int defaultMaxPerRoute = 100;
    int maxTotalConnections = 1000;

    // set up some HTTP client timeouts
    clientConfig.property(ClientProperties.CONNECT_TIMEOUT, connectTimeoutMS);
    clientConfig.property(ClientProperties.READ_TIMEOUT, readTimeoutMS);

    clientConfig.property(ClientProperties.ASYNC_THREADPOOL_SIZE, 10);
    clientConfig.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.CHUNKED);

    Client client = ClientBuilder.newClient(clientConfig);

    while (true) {
      WebTarget webTarget = client.target(ENDPOINT);
      Invocation.Builder builder = webTarget.request();

      builder.post(Entity.entity(s2, "application/json"), String.class);
      builder.post(Entity.entity(s1, "application/json"), String.class);
      builder.post(Entity.entity(s3, "application/json"), String.class);
    }
  }

}
