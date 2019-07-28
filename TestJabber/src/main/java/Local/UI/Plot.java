package Local.UI;

import Local.Configuration.MainConfig;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class Plot extends Thread {
    private MainConfig config;
    private Queue<Long> queue;


    public Plot(MainConfig config, Queue<Long> queue) {
        this.config = config;
        this.queue = queue;
    }

    @Override
    public void run() {
        final XYChart chart = QuickChart.getChart("95 percentile", "Number", "Time", "Messages", new double[1], new double[1]);

        final SwingWrapper<XYChart> sw = new SwingWrapper<>(chart);
        sw.displayChart();

        while (true) {
            try {
                Thread.sleep(config.getUpdateTime());
            } catch (InterruptedException e) {
                Interpreter.reportAboutError("interrupt ui thread");
            }

            final double[][] data = getData(queue);

            chart.updateXYSeries("Messages", data[0], data[1], null);
            sw.repaintChart();
        }
    }

    /**
     * get data from global queue
     * only data for drawing
     * @param queue
     * @return
     */
    private static double[][] getData(Queue<Long> queue) {
        Object[] rowData = queue.toArray();
        double[] data = convertToDouble(rowData);

        Arrays.sort(data);
        double[] percentilesData = Arrays.copyOfRange(data, (int)(data.length*0.95), data.length);
        for (int i = 0; i < percentilesData.length; i++) {
            percentilesData[i] = Math.ceil(percentilesData[i]/1000)*1000;
        }

        HashMap<Double, Integer> counter = new HashMap<>();
        for (double number: percentilesData) {
            if (counter.containsValue(number)) {
                int newCounter = counter.get(number) + 1;
                counter.put(number, newCounter);
            } else {
                counter.put(number, 1);
            }
        }

        double[] count = new double[counter.size()];
        double[] value = new double[counter.size()];
        int arrayIndex = 0;
        for (Map.Entry<Double, Integer> entry : counter.entrySet()) {
            Double timestamp = entry.getKey();
            Integer number = entry.getValue();
            count[arrayIndex] = number;
            value[arrayIndex] = timestamp;
        }

        return new double[][] {count, value};
    }

    /**
     * convert object[] to double[]
     * @param data
     * @return
     */
    private static double[] convertToDouble(Object[] data) {
        double[] convertedData = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            convertedData[i] = ((Long)data[i]).doubleValue();
        }

        return convertedData;
    }
}
