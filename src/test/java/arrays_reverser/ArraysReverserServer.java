package arrays_reverser;

import java.util.logging.Level;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class ArraysReverserServer
{
    static
    {
        SLF4JBridgeHandler.install();
        java.util.logging.Logger.getLogger("").setLevel(Level.FINEST); // Root logger, for example.
    }

    private final static Logger LOG = LoggerFactory.getLogger(ArraysReverserServer.class);

    public static void main(String[] args)
    {
        try
        {
            Server server = startServer(8888);
            LOG.info("Server started on " + server.getURI());
            server.join();
        }
        catch (Throwable t)
        {
            LOG.warn("Failed to start server", t);
        }
    }

    public static Server startServer(int port) throws Exception
    {
        Server server = new Server(port); // use OS selected port for server

        // Enable Annotation Scanning
        Configuration.ClassList classlist = Configuration.ClassList
                .setServerDefault(server);

        classlist.addBefore(
                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration");

        // Setup WAR/WebApp context
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setResourceBase("src/main/webapp");
        context.setExtraClasspath("target/classes");

        // Add WAR/WebApp context to server handler list
        HandlerList handlers = new HandlerList();
        handlers.addHandler(context);
        handlers.addHandler(new DefaultHandler());

        server.setHandler(handlers);
        // server.setDumpAfterStart(true);
        server.start();
        return server;
    }
}
