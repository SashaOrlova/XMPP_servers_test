package Client;

import Client.Configuration.ClientConfig;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.core.stanza.model.Message;


import java.io.IOException;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessagesSender extends Thread {
    private static Logger log = Logger.getLogger(MessagesSender.class.getName());

    private int id;
    private Random rand = new Random();
    private Queue<Integer> answers;
    private ClientConfig config;

    MessagesSender(int id, Queue<Integer> answers, ClientConfig config) {
        this.id = id;
        this.answers = answers;
        this.config = config;
    }

    /**
     * Create connection this server
     *
     * @return XMPPConnection
     */
    private SocketConnectionConfiguration getConnection() {
        SocketConnectionConfiguration config;
        config = SocketConnectionConfiguration.builder()
                .hostname(this.config.getHost())
                .channelEncryption(ChannelEncryption.DISABLED)
                .port(5222)
                .build();
        log.info("Connect to server");
        return config;
    }

    /**
     * Login user on xmpp server
     * NOTICE: expensive operation for server
     *
     * @param xmppClient
     */
    private void login(XmppClient xmppClient) throws XmppException {
        xmppClient.login("testuser" + id, "pass123");
        log.info("Login in: " + "testuser" + id);
    }

    /**
     * send result to local machine
     *
     * @param message
     * @throws IOException
     */
    private void sendToLocal(Message message) {
        long timestamp = System.currentTimeMillis();
        int duration = (int) (timestamp - Long.parseLong(message.getBody(), 10));
        answers.add(duration);
        System.out.println(duration);
        log.info("Send timestamp from user" + id + ' ' + timestamp);
    }

    /**
     * send message to server
     * message contain current timestamp
     *
     * @param xmppClient
     */
    private void sendMessageToChat(XmppClient xmppClient, String userJid) {
        xmppClient.sendMessageNoWait(new Message(Jid.of(userJid), Message.Type.CHAT, Long.toString(System.currentTimeMillis())));
    }

    @Override
    public void run() {
        XmppClient xmppClient = XmppClient.create(this.config.getServiceName(), getConnection());
        xmppClient.addInboundMessageListener(e -> {
            Message message = e.getMessage();
            sendToLocal(message);
        });
        xmppClient.addOutboundMessageListener((MessageEvent e)->{
            e.getMessage().setBody(Long.toString(System.currentTimeMillis()));
            System.out.println(Long.getLong(e.getMessage().getBody()) - System.currentTimeMillis()); });
        try {
            xmppClient.connect();
            login(xmppClient);

            for (int k = 0; k < config.getMessageNumber(); k++) {
                int number = rand.nextInt(config.getClientsNumber()) + 1;
                int l = rand.nextInt(number);
                log.info("Send to " + l + " by " + id);
                sendMessageToChat(xmppClient, "testuser" + k + "@" + config.getServiceName());
                if (config.getSendingDelay() != 0) {
                    Thread.sleep(config.getSendingDelay());
                }
            }

            log.info("Finish messaging " + id);

            try {
                sleep(config.getMaxSleepTime());
            } catch (InterruptedException e) {
                log.log(Level.INFO, "Interruptes while sleeping", e);
                return;
            }

            log.info("Finish all" + id);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error while messaging", e);
        }
    }
}
