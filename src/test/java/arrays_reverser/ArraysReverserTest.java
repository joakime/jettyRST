package arrays_reverser;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArraysReverserTest
{
    private final static Logger LOG = LoggerFactory.getLogger(ArraysReverserTest.class);
    private static Server server;

    @BeforeClass
    public static void startServer() throws Exception
    {
        server = ArraysReverserServer.startServer(0); // use OS selected port for server
    }

    @AfterClass
    public static void stopServer() throws Exception
    {
        server.stop();
    }

    String s1 = "[[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9]]";
    String s2 = "[[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9]]";
    String s3 = "[[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9]]";

    volatile boolean stop = false;
    volatile Exception e = null;

    @Test
    public void multiThreaded() throws Exception
    {
        LOG.info("Tests will finish after 2 minutes or when first error occur");
        Runnable worker = () -> {
            try
            {
                simpleSingle();
            }
            catch (Exception ex)
            {
                LOG.warn("Unable to execute simpleSingle", ex);
                e = ex;
                stop = true;
            }
        };
        for (int i = 0; i < 5; i++)
        {
            Thread t = new Thread(worker);
            t.setDaemon(true);
            t.start();
        }
        Long startTime = System.currentTimeMillis();
        while (!stop)
        {
            Thread.sleep(1000);
            if (e != null) throw e;
            if (System.currentTimeMillis() - startTime > 1000 * 60 * 2)
            {
                LOG.info("Tests finish: 2 minutes is up");
                stop = true;
            }
        }
    }


    public void simpleSingle()
    {
        ClientConfig clientConfig = new ClientConfig();

        int readTimeoutMS = 5 * 60 * 1000;
        int connectTimeoutMS = 60 * 1000;

        // set up some HTTP client timeouts
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, connectTimeoutMS);
        clientConfig.property(ClientProperties.READ_TIMEOUT, readTimeoutMS);

        clientConfig.property(ClientProperties.ASYNC_THREADPOOL_SIZE, 10);
        clientConfig.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.CHUNKED);

        Client client = ClientBuilder.newClient(clientConfig);

        while (!stop)
        {
            URI serverEndpoint = server.getURI().resolve("/reverse-arrays");
            WebTarget webTarget = client.target(serverEndpoint);
            Invocation.Builder builder = webTarget.request();

            builder.post(Entity.entity(s2, "application/json"), String.class);
            builder.post(Entity.entity(s1, "application/json"), String.class);
            builder.post(Entity.entity(s3, "application/json"), String.class);
        }
    }

}
