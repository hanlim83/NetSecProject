import Model.AWSSMS;
import Model.IPAddressPolicy;
import Model.OutlookEmail;
import Model.ScheduledThreadPoolExecutor;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNetworkInterface;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerCALandingVerifyDetails implements Initializable {
    public static AnchorPane rootP;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private JFXTextField chosenInterface;

    @FXML
    private JFXTextField chosenDetection;

    @FXML
    private JFXTextField chosenThreshold;

    @FXML
    private JFXButton previousBtn;

    @FXML
    private JFXButton saveBtn;

    @FXML
    private Label ipAddr;
    @FXML
    private JFXButton homeButton;

    private PcapNetworkInterface device;
    private Scene myScene;
    private ScheduledThreadPoolExecutor handler;
    private boolean ARPDetection;
    private int threshold;
    private AWSSMS SMSHandler;
    private OutlookEmail EmailHandler;
    private String intDisplayName;
    private boolean CaptureType;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        try {
            String whatismyIP = IPAddressPolicy.getIp();
            ipAddr.setText(whatismyIP);
            Boolean validityIP = IPAddressPolicy.isValidRange(whatismyIP);
            if (validityIP == true) {
                ipAddr.setTextFill(Color.rgb(1, 0, 199));
            } else {
                ipAddr.setTextFill(Color.rgb(255, 0, 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Path path2 = FileSystems.getDefault().getPath("src/View/baseline_home_white_18dp.png");
        File file2 = new File(path2.toUri());
        Image imageForFile2;
        try {
            imageForFile2 = new Image(file2.toURI().toURL().toExternalForm());
            ImageView imageView1 = new ImageView(imageForFile2);
//            imageView.setFitHeight(24.5);
//            imageView.setFitWidth(35);
            homeButton.setGraphic(imageView1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void passVariables(ScheduledThreadPoolExecutor handler, PcapNetworkInterface device, boolean ARPDetection, Integer threshold, AWSSMS SMSHandler, String intDisplayName, boolean CaptureType, OutlookEmail EmailHandler) {
        this.handler = handler;
        this.device = device;
        this.ARPDetection = ARPDetection;
        this.threshold = threshold;
        this.SMSHandler = SMSHandler;
        this.EmailHandler = EmailHandler;
        this.CaptureType = CaptureType;
        chosenInterface.setText(intDisplayName);
//        chosenInterface.setText(device.getName() + " (" + this.intDiplayName + ")");
        if (ARPDetection == true)
            chosenDetection.setText("Enabled");
        else {
            chosenDetection.setText("Not Enabled");
        }
        if (threshold == 0)
            chosenThreshold.setText("None");
        else
            chosenThreshold.setText(Integer.toString(threshold));
        this.intDisplayName = intDisplayName;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, null, this.ARPDetection, 0, this.SMSHandler, this.EmailHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goBackPreviousScreen(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CALandingSetOptions.fxml"));
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
            ControllerCALandingSetOptions controller = loader.getController();
            controller.passVariables(handler, device, ARPDetection, threshold, SMSHandler, intDisplayName, CaptureType, EmailHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(nextView));
        stage.setTitle("Set Options");
        stage.show();
    }

    @FXML
    public void transferToCaptureScreen(ActionEvent event) {
        if (CaptureType) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainPackets.fxml"));
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAMainPackets controller = loader.getController();
                controller.passVariables(device, handler, null, ARPDetection, threshold, SMSHandler, EmailHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("Capture - Packets View");
            stage.show();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainDashboard.fxml"));
            myScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAMainDashboard controller = loader.getController();
                controller.passVariables(device, handler, null, ARPDetection, threshold, SMSHandler, EmailHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("Capture - Statistics View");
            stage.show();
        }
    }

    public void hamburgerBar() {
        rootP = anchorPane;
        try {
            VBox box = FXMLLoader.load(getClass().getResource("AdminSideTab.fxml"));
            drawer.setSidePane(box);
            drawer.setVisible(false);
            drawer.setDefaultDrawerSize(219);
        } catch (IOException ex) {
            Logger.getLogger(ControllerBaseLayoutNew.class.getName()).log(Level.SEVERE, null, ex);
        }

        HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
        transition.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            transition.setRate(transition.getRate() * -1);
            transition.play();

            if (drawer.isOpened()) {
                drawer.close();
                drawer.setDisable(true);
                //drawer.setVisible(false);
            } else {
                drawer.open();
                drawer.setVisible(true);
                drawer.setDisable(false);
            }
        });
    }

    @FXML
    public void onClickHomeButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("AdminHome.fxml"));
        Scene myScene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();
        ControllerAdminHome controller = loader.getController();
        //controller.passData(admin);
        stage.setScene(new Scene(nextView));
        stage.setTitle("Home Page");
        stage.show();
    }
}

