package Local.UI;

import Local.Controller;

import java.util.Scanner;
import java.util.logging.Logger;

public class Interpreter {
    private static Logger log = Logger.getLogger(Interpreter.class.getName());

    private static final String START = "start";
    private static final String LOAD_CONFIG = "load config";
    private static final String PARSE_CONFIG = "parse config";
    private static final String START_TEST = "start message test";
    private static final String EXIT = "exit";
    private static final String MODE_ONLINE = "mode online";
    private static final String MODE_OFFLINE = "mode offline";
    private static final String PLOT_FROM_FILE = "plot from file";
    private static final String START_LOGIN_TEST = "start login test";

    public static void main(String[] args) throws Exception {
        Controller controller = new Controller();
        Scanner in = new Scanner(System.in);

        while (true) {
            String command = in.nextLine().toLowerCase().trim();
            log.info("Parse  command " + command);

            try {
                switch (command) {
                    case START:
                        controller.start();
                        break;

                    case LOAD_CONFIG:
                        controller.loadConfig();
                        break;

                    case START_TEST:
                        controller.startTest();
                        break;

                    case PARSE_CONFIG:
                        controller.parseConfig();
                        break;

                    case EXIT:
                        controller.exit();
                        break;

                    case MODE_OFFLINE:
                        controller.switchMode(Mode.OFFLINE);
                        break;

                    case MODE_ONLINE:
                        controller.switchMode(Mode.ONLINE);
                        break;

                    case PLOT_FROM_FILE:
                        controller.plotFromFile();
                        break;

                    case START_LOGIN_TEST:
                        controller.startLoginTest();
                        break;

                    default:
                        System.out.println("Unknown command");
                        break;

                }
            } catch (Exception e) {
                reportAboutError(e.getMessage());
            }
        }
    }

    /**
     * write about error and finish program
     *
     * @param error
     */
    public static void reportAboutError(String error) {
        System.out.println("Critical error: " + error);
        System.exit(1);
    }
}
