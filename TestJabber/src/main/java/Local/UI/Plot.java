package Local.UI;

import Local.Configuration.MainConfig;
import org.knowm.xchart.*;

import java.util.*;

public class Plot extends Thread {
    private MainConfig config;
    private Queue<Long> queue;


    public Plot(MainConfig config, Queue<Long> queue) {
        this.config = config;
        this.queue = queue;
    }

    @Override
    public void run() {
        final CategoryChart chart = new CategoryChartBuilder()
                .width(800)
                .height(600)
                .title("Score Histogram")
                .xAxisTitle("Score")
                .yAxisTitle("Number")
                .build();

        final SwingWrapper<CategoryChart> sw = new SwingWrapper<>(chart);
        chart.addSeries("Percentile 95", new int[]{1}, new int[]{1});
        chart.addSeries("Percentile 85", new int[]{1}, new int[]{1});
        chart.addSeries("Percentile 90", new int[]{1}, new int[]{1});

        sw.displayChart();

        while (true) {
            try {
                Thread.sleep(config.getUpdateTime());
            } catch (InterruptedException e) {
                Interpreter.reportAboutError("interrupt ui thread");
            }

            drawPlot(queue, chart);
            sw.repaintChart();
        }
    }

    /**
     * update existed plot
     * @param queue
     * @param chart
     */
    public static void drawPlot(Queue<Long> queue, CategoryChart chart) {
        final Integer[][] dataPercentile95 = getData(queue, 0.95);
        final Integer[][] dataPercentile90 = getData(queue, 0.90);
        final Integer[][] dataPercentile85 = getData(queue, 0.85);

        if (dataPercentile95[0] == null ||
                dataPercentile95[1] == null ||
                dataPercentile90[0] == null ||
                dataPercentile90[1] == null ||
                dataPercentile85[1] == null ||
                dataPercentile85[0] == null
        ) {
            Interpreter.reportAboutError("null in plot");
        }
        chart.updateCategorySeries("Percentile 95", Arrays.asList(dataPercentile95[0]), Arrays.asList(dataPercentile95[1]), null);
        chart.updateCategorySeries("Percentile 90", Arrays.asList(dataPercentile90[0]), Arrays.asList(dataPercentile90[1]), null);
        chart.updateCategorySeries("Percentile 85", Arrays.asList(dataPercentile85[0]), Arrays.asList(dataPercentile85[1]), null);
    }

    /**
     * draw constant plot from data
     * @param queue
     */
    public static void drawConstantPlot(Queue<Long> queue) {
        final CategoryChart chart = new CategoryChartBuilder()
                .width(800)
                .height(600)
                .title("Score Histogram")
                .xAxisTitle("Score")
                .yAxisTitle("Number")
                .build();

        final SwingWrapper<CategoryChart> sw = new SwingWrapper<>(chart);

        final Integer[][] dataPercentile95 = getData(queue, 0.95);
        final Integer[][] dataPercentile90 = getData(queue, 0.90);
        final Integer[][] dataPercentile85 = getData(queue, 0.85);

        chart.addSeries("Percentile 95", Arrays.asList(dataPercentile95[0]), Arrays.asList(dataPercentile95[1]));
        chart.addSeries("Percentile 90", Arrays.asList(dataPercentile90[0]), Arrays.asList(dataPercentile90[1]));
        chart.addSeries("Percentile 85", Arrays.asList(dataPercentile85[0]), Arrays.asList(dataPercentile85[1]));

        sw.displayChart();
    }

    /**
     * get data from global queue
     * only data for drawing
     * @param queue
     * @return
     */
    private static Integer[][] getData(Queue<Long> queue, double percentile) {
        Object[] rowData = queue.toArray();
        int[] data = convertToInt(rowData);

        Arrays.sort(data);
        int startIndex = data.length > 1 ? Math.min(data.length - 1, (int)(data.length*percentile)) : 0;

        int[] percentilesData = Arrays.copyOfRange(data, startIndex, data.length);
        int coeff = (int) (1 - percentile) * 10000;
        for (int i = 0; i < percentilesData.length; i++) {
            percentilesData[i] = (int)Math.ceil((double)percentilesData[i]/coeff)*coeff;
        }

        HashMap<Integer, Integer> counter = new HashMap<>();
        for (int number: percentilesData) {
            if (counter.containsKey(number)) {
                int newCounter = counter.get(number) + 1;
                counter.put(number, newCounter);
            } else {
                counter.put(number, 1);
            }
        }

        Integer[] count = new Integer[counter.size()];
        Integer[] value = new Integer[counter.size()];
        int arrayIndex = 0;
        for (Map.Entry<Integer, Integer> entry : counter.entrySet()) {
            Integer timestamp = entry.getKey();
            Integer number = entry.getValue();
            count[arrayIndex] = number;
            value[arrayIndex] = timestamp;
            arrayIndex++;
        }

        return new Integer[][] {value, count};
    }

    /**
     * convert object[] to double[]
     * @param data
     * @return
     */
    private static int[] convertToInt(Object[] data) {
        int[] convertedData = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            convertedData[i] = ((Long)data[i]).intValue();
        }

        return convertedData;
    }
}
