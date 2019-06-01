import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MessagesSender extends Thread {
    private int id;
    private Random rand = new Random();

    MessagesSender(int id) {
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

    private void addToFile(FileWriter writer, Message message) throws IOException {
        long timestamp = System.currentTimeMillis();
        writer.append(String.valueOf(timestamp))
                .append(' ')
                .append(Long.toString(Long.parseLong(message.getBody(), 10) - timestamp))
                .append(System.lineSeparator());
    }

    private void sendMessageToChat(Chat chat) throws XMPPException {
        long timestamp = System.currentTimeMillis();
        chat.sendMessage(Long.toString(timestamp));
    }

    @Override
    public void run() {
        XMPPConnection connection = getConnection();
        try {
            login(connection);
            try {
                final FileWriter writer = new FileWriter(Main.FOLDER + id + ".txt", false);
                ArrayList<Chat> chats = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    int number = rand.nextInt(Main.N) + 1;
                    Chat chat = connection.getChatManager().createChat("testuser" + number + "@localhost", new MessageListener() {
                        public void processMessage(Chat chat, Message message) {
                            try {
                                addToFile(writer, message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    chats.add(chat);
                }

                for (int k = 0; k < 100000; k++) {
                    int l = rand.nextInt(chats.size());
                    System.out.println("Send to " + chats.get(l) + " by " + id);
                    Chat chat = chats.get(l);
                    sendMessageToChat(chat);
                }

                System.out.println("Finish messaging" + id);

                try {
                    sleep(100000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                writer.close();

                System.out.println("Finish all" + id);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            try {
                final FileWriter writer = new FileWriter(Main.FOLDER + id + ".txt", false);
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
