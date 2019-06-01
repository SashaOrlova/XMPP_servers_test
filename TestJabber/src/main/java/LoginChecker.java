import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.io.FileWriter;
import java.io.IOException;

public class LoginChecker extends Thread {
    private int id;

    LoginChecker(int id) {
        this.id = id;
    }

    private XMPPConnection getConnection() {
        ConnectionConfiguration config = new ConnectionConfiguration("35.228.228.77", 5222, "localhost");
        SASLAuthentication.supportSASLMechanism("PLAIN", 0);
        return new XMPPConnection(config);
    }

    private void login(XMPPConnection connection) throws XMPPException {
        connection.connect();
        connection.login("testuser" + id, "pass123");
    }

    @Override
    public void run() {
        XMPPConnection connection = getConnection();
        try {
            login(connection);
        } catch (XMPPException e) {
            try {
                final FileWriter writer = new FileWriter(Main.FOLDER + "error" + id + ".txt", false);
                long timestamp = System.currentTimeMillis();
                writer.append(Long.toString(timestamp));
                writer.append(e.getMessage());
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
