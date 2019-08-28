package Local.UI;

import Local.Configuration.MainConfig;
import Local.Interpreter;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.util.*;

public class QuantilesPlot extends Thread {
    private MainConfig config;
    private Queue<Long> queue;
    long startTime = System.currentTimeMillis();
    private List<Long> testTime = new ArrayList<>();
    private List<Integer> answerTime95 = new ArrayList<>();
    private List<Integer> answerTime99 = new ArrayList<>();
    private List<Integer> answerTime90 = new ArrayList<>();



    public QuantilesPlot(MainConfig config, Queue<Long> queue) {
        this.config = config;
        this.queue = queue;
    }

    @Override
    public void run() {
        XYChart chart = new XYChartBuilder().width(800).height(600).title(getClass().getSimpleName()).xAxisTitle("seconds from start").yAxisTitle("ms").build();

        testTime.add((System.currentTimeMillis() - startTime) / 1000);
        answerTime90.add(0);
        answerTime99.add(0);
        answerTime95.add(0);
        final SwingWrapper<XYChart> sw = new SwingWrapper<>(chart);
        chart.addSeries("99", testTime, answerTime99);
        chart.addSeries("90", testTime, answerTime90);
        chart.addSeries("70", testTime, answerTime95);

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);

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
     *  @param queue
     * @param chart
     */
    public void drawPlot(Queue<Long> queue, XYChart chart) {
        updateData(queue);

        chart.updateXYSeries("99", testTime, answerTime99, null);
        chart.updateXYSeries("90", testTime, answerTime90, null);
        chart.updateXYSeries("70", testTime, answerTime95, null);
    }

    /**
     * get data from global queue
     * only data for drawing
     *
     * @param queue
     * @return
     */
    private void updateData(Queue<Long> queue) {
        Object[] rowData = queue.toArray();
        queue.clear();
        int[] data = convertToInt(rowData);

        Arrays.sort(data);

        if (data.length > 0) {
            testTime.add((System.currentTimeMillis() - startTime) / 1000);
            int index = (int) (data.length * 0.70);
            if (index >= data.length) {
                index = data.length - 1;
            }
            answerTime95.add(data[index]);
            index = (int) (data.length * 0.99);
            if (index >= data.length) {
                index = data.length - 1;
            }
            answerTime99.add(data[index]);
            index = (int) (data.length * 0.90);
            if (index >= data.length) {
                index = data.length - 1;
            }
            answerTime90.add(data[index]);
        } else {
            testTime.add((System.currentTimeMillis() - startTime) / 1000);
            answerTime95.add(0);
            answerTime99.add(0);
            answerTime90.add(0);
        }

        if (testTime.size() > 30) {
            testTime = testTime.subList(testTime.size() - 30, testTime.size());
            answerTime90 = answerTime90.subList(answerTime90.size() - 30, answerTime90.size());
            answerTime95 = answerTime95.subList(answerTime95.size() - 30, answerTime95.size());
            answerTime99 = answerTime99.subList(answerTime99.size() - 30, answerTime99.size());
        }
    }

    /**
     * convert object[] to double[]
     *
     * @param data
     * @return
     */
    private static int[] convertToInt(Object[] data) {
        int[] convertedData = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            convertedData[i] = ((Long) data[i]).intValue();
        }

        return convertedData;
    }
}
