package Local.Comunicator;

import Client.Configuration.ClientConfig;
import Common.Commands;
import Common.Results;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Instance of client
 */
public class Client {
    private static Logger log = Logger.getLogger(Client.class.getName());

    private Socket socket;

    public Client(String host, int port) throws IOException {
        log.info("Create new client");
        socket = new Socket(host, port);
    }

    /**
     * activate client
     * make it ready for tests
     * @throws IOException
     */
    public void start() throws IOException {
        log.info("Start client");
        socket.getOutputStream().write(Commands.START_CLIENT);
    }

    /**
     * Send config to client instance
     * @param config
     * @throws IOException
     */
    public void sendConfig(ClientConfig config) throws IOException {
        log.info("Send config");
        socket.getOutputStream().write(Commands.SEND_CONFIG);
        new ObjectOutputStream(socket.getOutputStream()).writeObject(config);
    }

    /**
     * Send client command about tests start
     * Append result of tests in global queue
     * @param successAnswers
     * @param failsAnswers
     * @throws IOException
     */
    public void startTest(Queue<Long> successAnswers, AtomicInteger failsAnswers) throws IOException {
        log.info("Start test");
        InputStream stream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(stream);
        socket.getOutputStream().write(Commands.START_TESTING);
        while (!socket.isClosed()) {
            int result = dataInputStream.readInt();
            log.info("Get answer from client: " + result);
            switch (result) {
                case Results.SUCCESS:
                    long timestamp = dataInputStream.readLong();
                    log.info("Get time from client: " + timestamp);
                    successAnswers.add(timestamp);
                    break;
                case Results.FAIL:
                    log.info("Get fail from client");
                    failsAnswers.incrementAndGet();
                    break;
            }
        }
    }
}