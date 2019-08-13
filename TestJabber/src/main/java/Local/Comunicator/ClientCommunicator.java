package Local.Comunicator;

import Client.Configuration.ClientConfig;
import Local.Configuration.InstanceConfig;
import Local.Configuration.MainConfig;
import Local.UI.Interpreter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class for communication of local machine and client
 */
public class ClientCommunicator {
    private ArrayList<Client> clients = new ArrayList<>();
    private MainConfig config = null;

    /**
     * make clients awake
     * initialize knowing clients
     */
    public void startCommunication(MainConfig config) throws IOException {
        this.config = config;
        for (InstanceConfig instance: config.getInstances()) {
            Client client = null;
            try {
                client = new Client(instance.getHost(), instance.getPort());
            } catch (IOException e) {
                Interpreter.reportAboutError("client connect fail " + e.getMessage());
            }
            clients.add(client);
            client.start();
        }
    }

    /**
     * Send serialized config to each client
     */
    public void sendConfig() throws IOException {
        int clientsCounter = 0;
        int stepSize = config.getUsersCount() / config.getInstanceCount();
        for (Client client: clients) {
            ClientConfig config = new ClientConfig(this.config.getServiceIP(),
                    this.config.getServiceName(),
                    this.config.getSendingDelay(),
                    this.config.getUpdateTime(),
                    clientsCounter,
                    clientsCounter + stepSize,
                    this.config.getUsersCount(),
                    this.config.getSendingMessagesCount());
            clientsCounter += stepSize;
            client.sendConfig(config);
        }
    }

    /**
     * launch tests in all clients
     */
    public void startTesting(final Queue<Long> globalQueue) {
        final AtomicInteger atomicInt = new AtomicInteger(0);

        for (final Client client: clients) {
            Thread myThready = new Thread(new Runnable() {
                public void run() {
                    try {
                        client.startTest(globalQueue, atomicInt);
                    } catch (IOException e) {
                        Interpreter.reportAboutError("client connection error");
                    }
                }
            });
            myThready.start();
        }
    }
}
