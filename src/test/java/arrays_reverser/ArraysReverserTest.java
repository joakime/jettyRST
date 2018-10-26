package arrays_reverser;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
        LOG.info("stopServer() @AfterClass");
        server.stop();
    }

    @Test
    public void multiThreaded() throws Exception
    {
        Duration waitDuration = Duration.ofMinutes(2);
        LOG.info("Tests will finish after " + waitDuration + " (ISO-8601) or when first error occurs");

        AtomicBoolean stop = new AtomicBoolean(false);
        int workerCount = 5;
        CountDownLatch workerLatch = new CountDownLatch(workerCount);

        List<Callable<Integer>> workers = new ArrayList<>();

        workers.add(() -> {
            Thread.sleep(waitDuration.toMillis());
            LOG.info("Reached successful end of test, issuing stop");
            stop.set(true);
            return -1;
        });

        for (int i = 0; i < workerCount; i++)
        {
            workers.add(new Worker(stop, workerLatch));
        }

        ExecutorService executor = Executors.newFixedThreadPool(workerCount * 2);
        try
        {
            List<Future<Integer>> workerFutures = executor.invokeAll(workers);

            LOG.info("Awaiting completion of remaining workers");

            workerLatch.await(5, TimeUnit.SECONDS);

            for (Future<Integer> workerFuture : workerFutures)
            {
                int requests = workerFuture.get(1, TimeUnit.MILLISECONDS);
                LOG.info("{} Processed {} requests", workerFuture, requests);
            }
        }
        catch(InterruptedException e)
        {
            LOG.error("invokeAll failed", e);
            stop.set(true);
        }

        LOG.info("multiThreaded is exiting");
    }

    public static class Worker implements Callable<Integer>
    {
        private static final AtomicInteger IDGEN = new AtomicInteger(0);

        private static final String s1 = "[[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9]]";
        private static final String s2 = "[[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9]]";
        private static final String s3 = "[[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9],[5,2],[0,1,2],[],[9]]";
        private final CountDownLatch completionLatch;
        private final AtomicBoolean stop;
        private final String id;
        private int completedRequests = 0;

        public Worker(AtomicBoolean stopFlag, CountDownLatch completionLatch)
        {
            this.stop = stopFlag;
            this.completionLatch = completionLatch;
            this.id = "" + IDGEN.incrementAndGet();
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

                URI serverEndpoint = server.getURI().resolve("/reverse-arrays?id=" + id);
                LOG.info("Server Endpoint: " + serverEndpoint);
                WebTarget webTarget = client.target(serverEndpoint);
                Invocation.Builder builder = webTarget.request();

                while (!stop.get())
                {
                    builder.post(Entity.entity(s2, "application/json"), String.class);
                    completedRequests++;
                    builder.post(Entity.entity(s1, "application/json"), String.class);
                    completedRequests++;
                    builder.post(Entity.entity(s3, "application/json"), String.class);
                    completedRequests++;
                    Thread.yield();
                }
            }
            catch (Throwable t)
            {
                // Only log and throw Exception if 'stop' hasn't been called yet
                if (stop.compareAndSet(false, true))
                {
                    LOG.warn("Worker id=" + id + " failed", t);
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
