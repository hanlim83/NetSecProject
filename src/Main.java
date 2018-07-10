import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("LoggingPage.fxml"));
            System.out.println(getClass().getResource("LoginPage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1058, 600);
            scene.getStylesheets().add("Style.css");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.setTitle("NSPJ");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}