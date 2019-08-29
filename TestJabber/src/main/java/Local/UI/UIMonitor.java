package Local.UI;

import Local.Comunicator.Monitoring;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class UIMonitor implements Runnable {
    private Monitoring monitoring;
    private AtomicInteger counter;
    private int updateTime;

    public UIMonitor(Monitoring monitoring, AtomicInteger counter, int updateTime) {
        this.monitoring = monitoring;
        this.counter = counter;
        this.updateTime = updateTime;
    }

    public static void showMonitor(Monitoring monitoring, AtomicInteger counter, int updateTime) {
        try {
            monitoring.start();
        } catch (IOException e) {
            showAlertBox(e);
        }

        Text textCPU = new Text("CPU usage: 0");
        Text textMemory = new Text("Free memory: 0");
        Text textRPS = new Text("Messages per second: 0");

        VBox vbox = new VBox(textCPU, textMemory, textRPS);
        Scene scene = new Scene(vbox, 300, 250);
        Stage stage = new Stage();

        stage.setScene(scene);
        stage.show();

        while (true) {
            try {
                Thread.sleep(updateTime);
            } catch (InterruptedException e) {
                return;
            }

            try {
                Monitoring.Info info = monitoring.monitor();
                textCPU.setText("CPU usage: " + info.cpuUsage);
                textMemory.setText("Free memory: " + info.freeMemory);
                textRPS.setText("Messages per second: " + counter.getAndSet(0));
            } catch (IOException e) {
                showAlertBox(e);
            }

        }
    }

    private static void showAlertBox(Exception e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");

        alert.setHeaderText(null);
        alert.setContentText(e.getMessage());

        alert.showAndWait();
    }

    @Override
    public void run() {
        showMonitor(monitoring, counter, updateTime);
    }
}
