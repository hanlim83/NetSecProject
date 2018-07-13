import Model.ScheduledExecutorServiceHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CAMain extends Application {
    private ScheduledExecutorServiceHandler handler;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("CALandingSelectInt.fxml"));
            System.out.println(getClass().getResource("CALandingSelectInt.fxml"));
            Parent root = loader.load();
            ControllerCALandingSelectInt controller = loader.getController();
            handler = new ScheduledExecutorServiceHandler();
            controller.passVariables(handler, null, null, 0, null);
            Scene scene = new Scene(root, 1067, 600);
            String css = this.getClass().getResource("IntTreeTableViewStyle.css").toExternalForm();
            scene.getStylesheets().add(css);
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.setTitle("NSPJ");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        handler.shutdownService();
        if (!ScheduledExecutorServiceHandler.getService().isShutdown())
            handler.forceShutdownService();
    }
}