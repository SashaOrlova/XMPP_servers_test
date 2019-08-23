package Local.UI;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.util.ArrayList;

public class LinePlot {
    public LinePlot(ArrayList<Integer> fails, ArrayList<Integer> succsess, ArrayList<Integer> usersNumber) {
        XYChart chart = new XYChartBuilder().width(800).height(600).title(getClass().getSimpleName()).xAxisTitle("User number").yAxisTitle("Percent").build();

        chart.addSeries("Fails", usersNumber, fails);
        chart.addSeries("Success", usersNumber, succsess);

        new SwingWrapper<>(chart).displayChart();
    }
}
