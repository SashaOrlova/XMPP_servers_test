package Client.Configuration;

import java.io.Serializable;

public class ClientConfig implements Serializable {
    /**
     * server ip address
     */
    private String host;
    private String serviceName;
    /**
     * time between messages
     */
    private int sendingDelay;
    /**
     * time between updating info in client
     */
    private int updateTime;
    /**
     * index from current instance logging and send message
     */
    private int userStartIndex;
    /**
     * index up to current instance logging and send message
     */
    private int userFinishIndex;
    /**
     * number of clients
     */
    private int clientsNumber;
    /**
     * how many massage will be send from one client
     */
    private int messageNumber;

    public ClientConfig(String host,
                        String serviceName,
                        int sendingDelay,
                        int updateTime,
                        int userStartIndex,
                        int userFinishIndex,
                        int clientsNumber,
                        int messageNumber) {
        this.host = host;
        this.serviceName = serviceName;
        this.sendingDelay = sendingDelay;
        this.updateTime = updateTime;
        this.userStartIndex = userStartIndex;
        this.userFinishIndex = userFinishIndex;
        this.clientsNumber = clientsNumber;
        this.messageNumber = messageNumber;
    }

    public String getHost() {
        return host;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getSendingDelay() {
        return sendingDelay;
    }

    public int getUpdateTime() {
        return updateTime;
    }

    public int getUserStartIndex() {
        return userStartIndex;
    }

    public int getUserFinishIndex() {
        return userFinishIndex;
    }

    public int getClientsNumber() {
        return clientsNumber;
    }

    public int getMessageNumber() {
        return messageNumber;
    }
}
