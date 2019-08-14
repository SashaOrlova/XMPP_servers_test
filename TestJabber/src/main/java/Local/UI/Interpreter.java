package Local.UI;

import Local.Comunicator.ClientCommunicator;
import Local.Configuration.ConfigParser;
import Local.Configuration.MainConfig;

import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class Interpreter {
    private static Logger log = Logger.getLogger(Interpreter.class.getName());

    private static final String START = "start";
    private static final String LOAD_CONFIG = "load config";
    private static final String PARSE_CONFIG = "parse config";
    private static final String START_TEST = "start test";
    private static final String EXIT = "exit";


    public static void main(String[] args) throws Exception {
        MainConfig config = null;

        ClientCommunicator communicator = new ClientCommunicator();
        Scanner in = new Scanner(System.in);
        while (true) {
            String command = in.nextLine().toLowerCase().trim();
            log.info("Parse  command " + command);

            switch (command) {
                case START:
                    if (config == null) {
                        config = ConfigParser.readConfig();
                    }
                    if (!ConfigParser.validateConfig(config)) {
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
                    Thread ui = new Plot(config, globalQueue);
                    ui.start();
                    break;

                case PARSE_CONFIG:
                    config = ConfigParser.readConfig(in);
                    break;

                case EXIT:
                    System.exit(0);
                    break;

                default:
                    System.out.println("Unknown command");
                    break;

            }
        }
    }

    public static void reportAboutError(String error) {
        System.out.println("Critical error: " + error);
        System.exit(1);
    }
}
