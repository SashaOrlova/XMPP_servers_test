package Client;

import Client.Configuration.ClientConfig;
import Common.Commands;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private static Logger log = Logger.getLogger(Client.class.getName());

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(Integer.parseInt(args[0]), 1);
            while (true) {
                Socket clientSocket = server.accept();
                log.info("Connect new client");
                new Client(clientSocket);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Socket error", e);
        }
    }

    Client(Socket clientSocket) throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        ClientConfig config = null;
        Queue<Integer> answers = new ConcurrentLinkedQueue<>();

        while (!clientSocket.isClosed()) {

            int command = inputStream.read();
            if (command == -1) {
                continue;
            }
            log.info("Get command: " + command);
            switch (command) {
                case Commands.START_CLIENT:
                    log.info("Start client");
                    break;

                case Commands.SEND_CONFIG:
                    log.info("Read config");
                    ObjectInputStream ois = new ObjectInputStream(inputStream);
                    try {
                        config = (ClientConfig) ois.readObject();
                        if (config == null) {
                            log.info("No config found");
                            clientSocket.close();
                        }
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

                    ArrayList<Thread> threads = new ArrayList<>();
                    for (int i = config.getUserStartIndex(); i < config.getUserFinishIndex(); i++) {
                        log.info("start user " + i);
                        Thread sender = new MessagesSender(i, answers, config);
                        threads.add(sender);
                        sender.start();
                    }

                    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                    ClientConfig finalConfig = config;
                    Thread uploadThread = new Thread(() -> {
                        while (true) {
                            Object[] array = answers.toArray();
                            answers.clear();
                            for (Object answer: array) {
                                try {
                                    out.writeInt((Integer) answer);
                                } catch (IOException e) {
                                    log.severe("Client socket error");
                                }
                            }
                            try {
                                Thread.sleep(finalConfig.getUpdateTime());
                            } catch (InterruptedException e) {
                                return;
                            }
                        }
                    });
                    uploadThread.run();

                    for (Thread thread: threads) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            log.severe("interrupt during wait threads");
                            clientSocket.close();
                            return;
                        }
                    }
                    uploadThread.interrupt();
                    uploadThread.join();
                    log.info("Finish test");
                    clientSocket.close();
                    break;

                case Commands.START_LOGIN:
                    log.info("Start login tests");
                    if (config == null) {
                        log.severe("No config");
                        clientSocket.close();
                        return;
                    }
                    ArrayList<Thread> loginThreads = new ArrayList<>();
                    for (int i = config.getUserStartIndex(); i < config.getUserFinishIndex(); i++) {
                        log.info("start user " + i);
                        Thread sender = new LoginChecker(i, answers, config);
                        loginThreads.add(sender);
                        sender.start();
                    }
                    for (Thread thread: loginThreads) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            log.severe("interrupt during wait threads");
                            clientSocket.close();
                            return;
                        }
                    }
                    log.info("Finish test");
                    out = new DataOutputStream(clientSocket.getOutputStream());

                    for (Integer answer: answers) {
                        out.writeInt(answer);
                    }
                    out.writeInt(Commands.FINISH);
                    out.close();
                    clientSocket.close();
                    break;

                default:
                    log.warning("Unknown command");
            }
        }
    }
}
