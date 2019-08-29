package Local;

import Local.Comunicator.ClientCommunicator;
import Local.Comunicator.Monitoring;
import Local.Configuration.ConfigParser;
import Local.Configuration.MainConfig;
import Local.UI.QuantilesPlot;
import Local.UI.UIMonitor;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller {
    private int mode = Mode.ONLINE;
    private MainConfig config;
    private ClientCommunicator communicator = new ClientCommunicator();
    private static final String FILE_NAME = "result";
    private Monitoring monitoring;

    public void monitoring() throws Exception {
        monitoring = new Monitoring(config);
    }

    /**
     * connect to clients
     *
     * @throws IOException
     */
    public void start() throws IOException {
        if (config == null) {
            config = ConfigParser.readConfig();
        }

        if (!checkConfig(config)) {
            Interpreter.reportAboutError("wrong config");
        }
        communicator.startCommunication(config);
    }

    /**
     * loaf config into clients instance
     *
     * @throws IOException
     */
    public void loadConfig() throws IOException {
        communicator.sendConfig();
    }

    /**
     * parse config from command line
     */
    public void parseConfig() {
        Scanner in = new Scanner(System.in);

        config = ConfigParser.readConfig(in);
    }

    /**
     * launch tests on clients
     *
     * @throws IOException
     */
    public void startTest() throws IOException {
        Queue<Long> globalQueue = new ConcurrentLinkedQueue<>();
        AtomicInteger counter = new AtomicInteger(0);
        communicator.startTesting(globalQueue, counter);
        if (!checkConfig(config)) {
            Interpreter.reportAboutError("wrong config");
        }
        if (monitoring != null) {
            monitoring.start();
            Thread t = new Thread(new UIMonitor(monitoring, counter, config.getUpdateTime()));
            t.start();
        }
        if (mode == Mode.ONLINE) {
            Thread ui = new QuantilesPlot(config, globalQueue);
            ui.start();
        } else {
            waitUntilEnd(globalQueue, config);
            saveInFile(globalQueue);
        }
    }

    public void startLoginTest() throws IOException, InterruptedException {
        Queue<Long> globalQueue = new ConcurrentLinkedQueue<>();
        AtomicInteger counter = new AtomicInteger(0);
        communicator.startLoginTesting(globalQueue, counter);
        if (!checkConfig(config)) {
            Interpreter.reportAboutError("wrong config");
        }
        if (monitoring != null) {
            monitoring.start();
            Thread t = new Thread(new UIMonitor(monitoring, counter, config.getUpdateTime()));
            t.start();
        }
        if (mode == Mode.ONLINE) {
            Thread ui = new QuantilesPlot(config, globalQueue);
            ui.start();
        } else {
            waitUntilEnd(globalQueue, config);
            saveInFile(globalQueue);
        }
    }

    public void startRegisterTest() throws IOException, InterruptedException {
        Queue<Long> globalQueue = new ConcurrentLinkedQueue<>();
        AtomicInteger counter = new AtomicInteger(0);
        if (!checkConfig(config)) {
            Interpreter.reportAboutError("wrong config");
        }
        communicator.startRegisterTesting(globalQueue, counter);
        if (monitoring != null) {
            monitoring.start();
            Thread t = new Thread(new UIMonitor(monitoring, counter, config.getUpdateTime()));
            t.start();
        }
        if (mode == Mode.ONLINE) {
            Thread ui = new QuantilesPlot(config, globalQueue);
            ui.start();
        } else {
            waitUntilEnd(globalQueue, config);
            saveInFile(globalQueue);
        }
    }


    /**
     * exit from program
     */
    public void exit() {
        System.exit(0);
    }

    /**
     * change mode of work
     * online - get updates online
     * offline - get result after tests end
     *
     * @param mode
     */
    public void switchMode(int mode) {
        this.mode = mode;
    }

    /**
     * draw plot from file
     *
     * @throws FileNotFoundException
     */
    public void plotFromFile() throws FileNotFoundException {
        Queue<Long> queue = getDataFromFile();

//        QuantilesPlot.drawConstantPlot(queue);
    }


    /**
     * check is config null and validate it
     *
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
     * wait end of clients work
     *
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
     * save result from client into file
     *
     * @param queue
     * @throws IOException
     */
    private static void saveInFile(Queue<Long> queue) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));
        for (Long time : queue) {
            writer.write(time.toString() + '\n');
        }

        writer.close();
    }

    /**
     * Read data from file saved by offline mode
     *
     * @return
     * @throws FileNotFoundException
     */
    private static Queue<Long> getDataFromFile() throws FileNotFoundException {
        Queue<Long> globalQueue = new ArrayDeque<>();
        Scanner scanner = new Scanner(new File(FILE_NAME));

        while (scanner.hasNext()) {
            globalQueue.add(scanner.nextLong());
        }

        return globalQueue;
    }
}
