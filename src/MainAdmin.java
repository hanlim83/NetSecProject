import Model.ScheduledExecutorServiceHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainAdmin extends Application {
    ScheduledExecutorServiceHandler handler = new ScheduledExecutorServiceHandler();

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AdminLoginPage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1056, 600);
            scene.getStylesheets().add("Style.css");
            scene.getStylesheets().add("IntTreeTableViewStyle.css");
            loadAdminSideTabCtrl();
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
//        File file = new File(System.getProperty("user.home") + "\\" + ".store\\oauth2_sample\\StoredCredential");
//        file.delete();
        handler.shutdownService();
        if (!ScheduledExecutorServiceHandler.getService().isShutdown())
            handler.forceShutdownService();
    }

    public void loadAdminSideTabCtrl() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(null, this.handler, null, null, 0, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}