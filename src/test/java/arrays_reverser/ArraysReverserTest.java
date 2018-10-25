package arrays_reverser;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

import jdk.nashorn.internal.codegen.CompilerConstants;
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
        LOG.info("stopServer() @AfterClass");
        server.stop();
    }

    @Test
    public void multiThreaded() throws Exception
    {
        LOG.info("Tests will finish after 2 minutes or when first error occur");

        AtomicBoolean stop = new AtomicBoolean(false);
        int workerCount = 5;
        CountDownLatch workerLatch = new CountDownLatch(workerCount);

        List<Worker> workers = new ArrayList<>();

        for (int i = 0; i < workerCount; i++)
        {
            workers.add(new Worker(stop, workerLatch));
        }

        ExecutorService executor = Executors.newFixedThreadPool(workerCount * 2);
        List<Future<Integer>> workerFutures = executor.invokeAll(workers);

        Long startTime = System.currentTimeMillis();
        while (!stop.get())
        {
            Thread.sleep(1000);
            if (System.currentTimeMillis() - startTime > 1000 * 60 * 2)
            {
                LOG.info("Tests finish: 2 minutes is up");
                break;
            }
        }

        stop.set(true);

        LOG.info("Awaiting completion of remaining workers");

        workerLatch.await(5, TimeUnit.SECONDS);

        executor.shutdown();
        
        for(Future<Integer> workerFuture: workerFutures)
        {
            int requests = workerFuture.get(1, TimeUnit.MILLISECONDS);
            LOG.info("{} Processed {} requests", workerFuture, requests);
        }

        LOG.info("multiThreaded is exiting");
    }

    public static class Worker implements Callable<Integer>
    {
        private static final String s1 = "[[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9]]";
        private static final String s2 = "[[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9]]";
        private static final String s3 = "[[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9]]";
        private final CountDownLatch completionLatch;
        private final AtomicBoolean stop;
        private int completedRequests = 0;

        public Worker(AtomicBoolean stopFlag, CountDownLatch completionLatch)
        {
            this.stop = stopFlag;
            this.completionLatch = completionLatch;
        }

        @Override
        public Integer call()
        {
            try
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

                while (!stop.get())
                {
                    URI serverEndpoint = server.getURI().resolve("/reverse-arrays");
                    WebTarget webTarget = client.target(serverEndpoint);
                    Invocation.Builder builder = webTarget.request();

                    builder.post(Entity.entity(s2, "application/json"), String.class);
                    completedRequests++;
                    builder.post(Entity.entity(s1, "application/json"), String.class);
                    completedRequests++;
                    builder.post(Entity.entity(s3, "application/json"), String.class);
                    completedRequests++;
                }
            }
            catch (Throwable t)
            {
                // Only log and throw Exception if 'stop' hasn't been called yet
                if (stop.compareAndSet(false, true))
                {
                    LOG.warn("Worker failed", t);
                    throw t;
                }
            }
            finally
            {
                completionLatch.countDown();
            }
            return completedRequests;
        }
    }
}
