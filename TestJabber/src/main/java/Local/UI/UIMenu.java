package Local.UI;

import Local.Comunicator.Monitoring;
import Local.Controller;
import Local.Mode;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Collection;

public class UIMenu extends Application {
    private Controller controller = new Controller();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("XMPP Tester");
        Button messageBtn = new Button("Start Messages Test");
        Button loginBtn = new Button("Start Login Test");
        Button registerBtn = new Button("Start Register Test");
        CheckBox mode = new CheckBox("Online mode");

        messageBtn.setOnAction((event) -> safeTestStart(() -> controller.startTest(),
                Arrays.asList(messageBtn, loginBtn, registerBtn)));

        loginBtn.setOnAction((event) -> safeTestStart(() -> controller.startTest(),
                Arrays.asList(messageBtn, loginBtn, registerBtn)));

        registerBtn.setOnAction((event) -> safeTestStart(() -> controller.startTest(),
                Arrays.asList(messageBtn, loginBtn, registerBtn)));

        mode.setOnAction((event -> controller.switchMode(mode.isSelected() ? Mode.ONLINE : Mode.OFFLINE)));

        VBox vbox = new VBox(messageBtn, loginBtn, registerBtn, mode);
        vbox.setSpacing(20);
        vbox.setAlignment(Pos.BASELINE_CENTER);

        primaryStage.setScene(new Scene(vbox, 300, 200));
        primaryStage.show();
    }

    private void safeTestStart(Procedure test, Collection<Button> buttons) {
        for (Button btn: buttons) {
            btn.setDisable(true);
        }

        try {
            controller.start();
            controller.loadConfig();
            test.call();
        } catch (Exception e) {
            showAlertBox(e);
        }
        for (Button btn: buttons) {
            btn.setDisable(false);
        }
    }

    private void showAlertBox(Exception e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");

        alert.setHeaderText(null);
        alert.setContentText(e.getMessage());

        alert.showAndWait();
    }

    private interface Procedure {
        void call() throws Exception;
    }
}