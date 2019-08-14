package Client;

import Client.Configuration.ClientConfig;
import Common.Results;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessagesSender extends Thread {
    private static Logger log = Logger.getLogger(MessagesSender.class.getName());

    private int id;
    private Random rand = new Random();
    private DataOutputStream out;
    private ClientConfig config;

    MessagesSender(int id, Socket socket, ClientConfig config) {
        this.id = id;
        try {
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Can't create stream", e);
        }
        this.config = config;
    }

    /**
     * Create connection this server
     * @return XMPPConnection
     */
    private XMPPConnection getConnection() {
        ConnectionConfiguration config = new ConnectionConfiguration(this.config.getHost()  , 5222, this.config.getServiceName());
        SASLAuthentication.supportSASLMechanism("PLAIN", 0);
        log.info("Connect to server");
        return new XMPPConnection(config);
    }

    /**
     * Login user on xmpp server
     * NOTICE: expensive operation for server
     * @param connection
     * @throws XMPPException
     */
    private void login(XMPPConnection connection) throws XMPPException {
        connection.connect();
        connection.login("testuser" + id, "pass123");
        log.info("Login in: " + "testuser" + id);
    }

    /**
     * send result to local machine
     * @param message
     * @throws IOException
     */
    private void sendToLocal(Message message) throws IOException {
        long timestamp = System.currentTimeMillis();
        long duration = timestamp - Long.parseLong(message.getBody(), 10);
        out.writeInt(Results.SUCCESS);
        out.writeLong(duration);
        log.info("Send timestamp from user" + id + ' ' + timestamp);
    }

    /**
     * send message to server
     * message contain current timestamp
     * @param chat
     * @throws XMPPException
     */
    private void sendMessageToChat(Chat chat) throws XMPPException {
        long timestamp = System.currentTimeMillis();
        chat.sendMessage(Long.toString(timestamp));
    }

    @Override
    public void run() {
        XMPPConnection connection = getConnection();
        try {
            login(connection);
            ArrayList<Chat> chats = new ArrayList<>();
            for (int j = 0; j < 1; j++) {
                int number = rand.nextInt(config.getClientsNumber()) + 1;
                Chat chat = connection.getChatManager().createChat("testuser" + number + "@localhost", new MessageListener() {
                    public void processMessage(Chat chat, Message message) {
                        try {
                            sendToLocal(message);
                        } catch (IOException e) {
                            log.log(Level.WARNING, "Error in sending result", e);
                        }
                    }
                });
                chats.add(chat);
                log.info("Add chart between " + id + " and " + number);
            }

            for (int k = 0; k < config.getMessageNumber(); k++) {
                int l = rand.nextInt(chats.size());
                log.info("Send to " + chats.get(l) + " by " + id);
                Chat chat = chats.get(l);
                sendMessageToChat(chat);
                if (config.getSendingDelay() != 0) {
                    Thread.sleep(config.getSendingDelay());
                }
            }

            log.info("Finish messaging " + id);

            try {
                sleep(100000);
            } catch (InterruptedException e) {
               log.log(Level.SEVERE, "Interruptes while sleeping", e);
            }
            log.info("Finish all" + id);
        } catch (Exception e) {
            try {
                out.writeInt(Results.FAIL);
            } catch (IOException e1) {
                log.info("Cant report about error");
            }
            log.log(Level.SEVERE, "Error while messaging", e);
        }
    }
}
