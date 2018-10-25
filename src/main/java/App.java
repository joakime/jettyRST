import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.*;

@ApplicationPath("/")
public class App extends ResourceConfig
{
    public App()
    {
        property(ServerProperties.TRACING, "ALL");
        property(ServerProperties.TRACING_THRESHOLD, "VERBOSE");
    }
}
