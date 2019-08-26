package Client;

import Client.Configuration.ClientConfig;
import org.jivesoftware.smack.*;

import java.util.Queue;
import java.util.logging.Logger;

public class RegisterationChecker extends Thread {
    private static Logger log = Logger.getLogger(LoginChecker.class.getName());

    private int id;
    private Queue<Integer> queue;
    private ClientConfig config;

    RegisterationChecker(int id, Queue<Integer> queue, ClientConfig config) {
        this.id = id;
        this.queue = queue;
        this.config = config;
    }

    /**
     * Create connection this server
     *
     * @return XMPPConnection
     */
    private XMPPConnection getConnection() {
        ConnectionConfiguration config = new ConnectionConfiguration(this.config.getHost(), 5222, this.config.getServiceName());
        SASLAuthentication.supportSASLMechanism("PLAIN", 0);
        log.info("Connect to server");
        return new XMPPConnection(config);
    }

    /**
     * Check is xmpp server allow register users
     *
     * @return
     */
    private boolean canRegisterUsers() {
        XMPPConnection connection = getConnection();
        AccountManager accountManager = new AccountManager(connection);
        boolean canRegisterUsers = accountManager.supportsAccountCreation();
        connection.disconnect();
        return canRegisterUsers;
    }


    @Override
    public void run() {
        if (!canRegisterUsers()) {
            return;
        }

        for (int i = 0; i < config.getMessageNumber(); i++) {
            try {
                XMPPConnection connection = getConnection();
                long startTimestamp = System.currentTimeMillis();
                AccountManager accountManager = new AccountManager(connection);
                accountManager.createAccount("user" + id + '_' + i, "pass123");
                long finishTimestamp = System.currentTimeMillis();
                log.info("Success register, send to local instance");
                queue.add((int) (finishTimestamp - startTimestamp));
                connection.disconnect();
                if (config.getSendingDelay() != 0) {
                    Thread.sleep(config.getSendingDelay());
                }
            } catch (Exception ignored) {}
        }
    }
}
