import Model.AWSSMS;
import Model.ExecutorServiceHandler;
import Model.NetworkCapture;
import Model.OutlookEmail;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.pcap4j.core.PcapNetworkInterface;

import java.io.File;
import java.io.IOException;

public class CATest extends Application {
    private static Stage primaryStage;
    private ExecutorServiceHandler handler;
    private NetworkCapture capture = null;
    private PcapNetworkInterface device = null;
    private boolean ARPDetection = false;
    private Integer threshold = null;
    private AWSSMS SMSHandler = null;
    private OutlookEmail EmailHandler = null;
    private Scene myScene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        CATest.primaryStage = primaryStage;
        try {
            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(getClass().getResource("CAMainAlertDashboard.fxml"));
            loader.setLocation(getClass().getResource("CAAlerts.fxml"));
            System.out.println(getClass().getResource("CAAlerts.fxml"));
            Parent root = loader.load();
            ControllerCAAlerts controller = loader.getController();
            handler = new ExecutorServiceHandler();
            NetworkCapture capture = new NetworkCapture(null, 0);
            capture.addAlert(true);
            capture.addAlert(false);
            controller.passVariables(null, handler, capture, false, 0, null, null);
//            controller.passVariables(null, handler, null, false, 0, null, new File("C:\\Users\\Hansen Lim\\Documents\\Hello.pcap").getAbsolutePath(), false, null);
            Scene scene = new Scene(root, 1056, 600);
            String css = this.getClass().getResource("IntTreeTableViewStyle.css").toExternalForm();
            scene.getStylesheets().add(css);
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image("FireIcon.png"));
            primaryStage.setTitle("Alert Dashboard");
            if (!new File("PcapExport").exists()) {
                FileUtils.forceMkdir(new File("PcapExport"));
            }
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        handler.shutdownService();
        if (!ExecutorServiceHandler.getService().isShutdown())
            handler.forceShutdownService();
        try {
            FileUtils.cleanDirectory(new File("PcapExport"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}