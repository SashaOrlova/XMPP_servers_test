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
        for (InstanceConfig instance : config.getInstances()) {
            Client client;
            try {
                client = new Client(instance.getHost(), instance.getPort());
            } catch (IOException e) {
                Interpreter.reportAboutError("client connect fail " + e.getMessage());
                continue;
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
        for (Client client : clients) {
            ClientConfig config = new ClientConfig(this.config.getServiceIP(),
                    this.config.getServiceName(),
                    this.config.getSendingDelay(),
                    this.config.getUpdateTime(),
                    clientsCounter,
                    clientsCounter + stepSize,
                    this.config.getUsersCount(),
                    this.config.getSendingMessagesCount(),
                    this.config.getMaxWaitTime()
            );
            clientsCounter += stepSize;
            client.sendConfig(config);
        }
    }

    public void sendLoginConfig(int iteration) throws IOException {
        int clientsCounter = 0;
        int stepSize = (config.getIterationClients(iteration)) / config.getInstanceCount();
        for (Client client : clients) {
            ClientConfig config = new ClientConfig(this.config.getServiceIP(),
                    this.config.getServiceName(),
                    this.config.getSendingDelay(),
                    this.config.getUpdateTime(),
                    clientsCounter,
                    clientsCounter + stepSize,
                    this.config.getUsersCount(),
                    this.config.getSendingMessagesCount(),
                    this.config.getMaxWaitTime()
            );
            clientsCounter += stepSize;
            client.sendConfig(config);
        }
    }

    /**
     * launch tests in all clients
     */
    public void startTesting(final Queue<Long> globalQueue) {
        final AtomicInteger atomicInt = new AtomicInteger(0);

        for (final Client client : clients) {
            Thread clientsThread = new Thread(() -> {
                try {
                    client.startTest(globalQueue, atomicInt);
                } catch (IOException e) {
                    Interpreter.reportAboutError("client connection error");
                }
            });
            clientsThread.start();
        }
    }

    /**
     * launch login tests in all clients
     */
    public void startLoginTesting(final AtomicInteger successAnswers, final AtomicInteger failsAnswers) throws InterruptedException {
        ArrayList<Thread> threads = new ArrayList<>();

        for (final Client client : clients) {
            Thread clientsThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        client.startLoginTest(successAnswers, failsAnswers);
                    } catch (IOException e) {
                        Interpreter.reportAboutError("client connection error");
                    }
                }
            });
            clientsThread.start();
            threads.add(clientsThread);
        }
        for (Thread thread: threads) {
            thread.join();
        }
    }
}
