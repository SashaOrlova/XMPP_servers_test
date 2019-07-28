package Client;

import Client.Configuration.ClientConfig;
import Common.Commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private static Logger log = Logger.getLogger(Client.class.getName());

    public static void main(String[] args) {
        try {
            while (true) {
                ServerSocket server = new ServerSocket(Integer.parseInt(args[0]), 1);
                Socket clientSocket = server.accept();
                log.info("Connect new client");
                new Client(clientSocket);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Socket error", e);
        }
    }

    Client(Socket clientSocket) throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        while (!clientSocket.isClosed()) {
            int command = inputStream.read();
            log.info("Get command: " + command);
            ClientConfig config = null;
            switch (command) {
                case Commands.START_CLIENT:
                    log.info("Start client");
                    break;

                case Commands.SEND_CONFIG:
                    log.info("Read config");
                    ObjectInputStream ois = new ObjectInputStream(inputStream);
                    try {
                        config = (ClientConfig) ois.readObject();
                    } catch (ClassNotFoundException e) {
                        log.info("Wrong config type");
                        clientSocket.close();
                    }
                    break;

                case Commands.START_TESTING:
                    log.info("Start tests");
                    if (config == null) {
                        log.severe("No config");
                        clientSocket.close();
                        return;
                    }
                    for (int i = config.getUserStartIndex(); i < config.getUserFinishIndex(); i++) {
                        log.info("start user " + i);
                        Thread sender = new MessagesSender(i, clientSocket, config);
                        sender.start();
                    }
                    break;

                default:
                    log.warning("Unknown command");
            }
        }
    }
}
