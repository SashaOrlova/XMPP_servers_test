package Local.UI;

import Local.Controller;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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

        messageBtn.setOnAction((event) -> safeTestStart(() -> {
            controller.startTest();
        }, Arrays.asList(messageBtn, loginBtn, registerBtn)));

        loginBtn.setOnAction((event) -> safeTestStart(() -> {
            controller.startTest();
        }, Arrays.asList(messageBtn, loginBtn, registerBtn)));

        registerBtn.setOnAction((event) -> safeTestStart(() -> {
            controller.startTest();
        }, Arrays.asList(messageBtn, loginBtn, registerBtn)));

        VBox vbox = new VBox(messageBtn, loginBtn, registerBtn);
        vbox.setSpacing(20);
        vbox.setAlignment(Pos.BASELINE_CENTER);

        primaryStage.setScene(new Scene(vbox, 300, 250));
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