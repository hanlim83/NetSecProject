import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CAMain extends Application {
    private ScheduledExecutorService service;
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("CALanding.fxml"));
            System.out.println(getClass().getResource("CALanding.fxml"));
            System.out.println(getClass().getResource("CABackdrop.png"));
            Parent root = loader.load();
            ControllerCALanding controller = loader.<ControllerCALanding>getController();
            int cores = Runtime.getRuntime().availableProcessors();
            service = Executors.newScheduledThreadPool(cores);
            controller.passVariables(service);
            Scene scene = new Scene(root, 1067, 600);
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.setTitle("NSPJ");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop(){
        service.shutdownNow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}