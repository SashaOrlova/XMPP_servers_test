package Local.Configuration;

/**
 *
 */
public class MainConfig {
    /**
     * number of working instances
     */
    private int instanceCount;

    /**
     * information about every instance
     * instances.length == instanceCount
     * see InstanceConfig
     */
    private InstanceConfig[] instances;
    private String serviceName;
    private String serviceIP;
    /**
     * number of sending messages
     */
    private int sendingMessagesCount;
    /**
     * how many users register in server
     */
    private int usersCount;
    /**
     * time between messages
     */
    private int sendingDelay;
    /**
     * time between updating info in client
     */
    private int updateTime;
    /**
     * after how many time we need result
     */
    private int maxWaitTime;

    public MainConfig(int instanceCount,
                      int sendingMessagesCount,
                      int usersCount,
                      InstanceConfig[] instances,
                      String serviceName,
                      String serviceIP,
                      int sendingDelay,
                      int updateTime,
                      int maxWaitTime
    ) {
        this.instanceCount = instanceCount;
        this.sendingMessagesCount = sendingMessagesCount;
        this.usersCount = usersCount;
        this.instances = instances;
        this.serviceName = serviceName;
        this.serviceIP = serviceIP;
        this.sendingDelay = sendingDelay;
        this.updateTime = updateTime;
        this.maxWaitTime = maxWaitTime;
    }

    public InstanceConfig[] getInstances() {
        return instances;
    }

    public int getSendingDelay() {
        return sendingDelay;
    }

    public int getMaxWaitTime() {
        return maxWaitTime;
    }

    public int getInstanceCount() {
        return instanceCount;
    }

    public int getSendingMessagesCount() {
        return sendingMessagesCount;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public String getServiceIP() {
        return serviceIP;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getUpdateTime() {
        return updateTime;
    }
}
