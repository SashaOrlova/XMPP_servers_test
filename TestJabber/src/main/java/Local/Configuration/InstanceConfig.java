package Local.Configuration;

/**
 * Class describe one instance
 */
public class InstanceConfig {
    /**
     * instance ip address
     */
    private String host;
    /**
     * instance listen port
     */
    private int port;


    public InstanceConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
