package Local.UI;

import Local.Comunicator.ClientCommunicator;
import Local.Configuration.ConfigParser;
import Local.Configuration.MainConfig;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class Interpreter {
    private static Logger log = Logger.getLogger(Interpreter.class.getName());

    private static final String FILE_NAME = "result";

    private static final String START = "start";
    private static final String LOAD_CONFIG = "load config";
    private static final String PARSE_CONFIG = "parse config";
    private static final String START_TEST = "start test";
    private static final String EXIT = "exit";
    private static final String MODE_ONLINE = "mode online";
    private static final String MODE_OFFLINE = "mode offline";


    public static void main(String[] args) throws Exception {
        MainConfig config = null;

        ClientCommunicator communicator = new ClientCommunicator();
        Scanner in = new Scanner(System.in);
        int mode = Mode.ONLINE;

        while (true) {
            String command = in.nextLine().toLowerCase().trim();
            log.info("Parse  command " + command);

            switch (command) {
                case START:
                    if (!checkConfig(config)) {
                    Interpreter.reportAboutError("wrong config");
                    }
                    communicator.startCommunication(config);
                    break;

                case LOAD_CONFIG:
                    communicator.sendConfig();
                    break;

                case START_TEST:
                    Queue<Long> globalQueue = new ConcurrentLinkedQueue<>();
                    communicator.startTesting(globalQueue);
                    if (!checkConfig(config)) {
                        Interpreter.reportAboutError("wrong config");
                    }
                    if (mode == Mode.ONLINE) {
                        Thread ui = new Plot(config, globalQueue);
                        ui.start();
                    } else {
                        waitUntilEnd(globalQueue, config);
                        saveInFile(globalQueue);
                        return;
                    }
                    break;

                case PARSE_CONFIG:
                    config = ConfigParser.readConfig(in);
                    break;

                case EXIT:
                    System.exit(0);
                    break;
                case MODE_OFFLINE:
                    mode = Mode.OFFLINE;
                    break;

                case MODE_ONLINE:
                    mode = Mode.ONLINE;
                    break;

                default:
                    System.out.println("Unknown command");
                    break;

            }
        }
    }

    /**
     * write about error and finish program
     * @param error
     */
    public static void reportAboutError(String error) {
        System.out.println("Critical error: " + error);
        System.exit(1);
    }

    /**
     * wait end of clients work
     * @param queue
     * @param config
     */
    private static void waitUntilEnd(Queue<Long> queue, MainConfig config) {
        int queueSize = queue.size();
        while (true) {
            try {
                Thread.sleep(config.getUpdateTime());
            } catch (InterruptedException e) {
                Interpreter.reportAboutError("interrupt main thread");
            }
            if (queue.size() == queueSize) {
                return;
            }
            queueSize = queue.size();
        }
    }

    /**
     * check is config null and validate it
     * @param config
     * @return
     */
    private static boolean checkConfig(MainConfig config) {
        if (config == null) {
            return false;
        }

        return ConfigParser.validateConfig(config);
    }

    /**
     * save result from client into file
     * @param queue
     * @throws IOException
     */
    private static void saveInFile(Queue<Long> queue) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));
        for (Long time: queue) {
            writer.write(time.toString() + '\n');
        }

        writer.close();
    }
}
