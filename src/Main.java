import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("BucketsPage.fxml"));
//            System.out.println(getClass().getResource("LoginPage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1056, 600);
            scene.getStylesheets().add("Style.css");
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
        ControllerUserHome.StopTimer();
//        File file = new File(System.getProperty("user.home") + "\\" + ".store\\oauth2_sample\\StoredCredential");
//        file.delete();
    }

    public static void main(String[] args) {
        launch(args);
    }
}