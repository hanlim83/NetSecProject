import Model.AWSSMS;
import Model.IPAddressPolicy;
import Model.OutlookEmail;
import Model.ScheduledThreadPoolExecutorHandler;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNetworkInterface;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerCALandingSetOptions implements Initializable {
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
    private JFXCheckBox ARPSpoofingCheckbox;

    @FXML
    private JFXComboBox<String> ThresholdChooser;

    @FXML
    private JFXButton previousBtn;

    @FXML
    private JFXButton nextBtn;

    @FXML
    private Label ipAddr;

    @FXML
    private JFXButton homeButton;

    private PcapNetworkInterface device;
    private Scene myScene;
    private ScheduledThreadPoolExecutorHandler handler;
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
        nextBtn.setDisable(true);
        ArrayList<String> thresholds = new ArrayList<String>();
        thresholds.add("None");
        thresholds.add("100");
        thresholds.add("500");
        thresholds.add("1000");
        thresholds.add("2500");
        thresholds.add("5000");
        thresholds.add("10000");
        ObservableList<String> thresholdList = FXCollections.observableArrayList(thresholds);
        ThresholdChooser.setItems(thresholdList);
        ThresholdChooser.setValue("Select Threshold");
    }

    public void passVariables(ScheduledThreadPoolExecutorHandler handler, PcapNetworkInterface device, boolean ARPDetection, Integer threshold, AWSSMS SMSHandler, String intDisplayName, boolean CaptureType, OutlookEmail EmailHandler) {
        this.handler = handler;
        this.device = device;
        this.SMSHandler = SMSHandler;
        this.EmailHandler = EmailHandler;
        this.CaptureType = CaptureType;
        if (ARPDetection == true)
            ARPSpoofingCheckbox.setSelected(true);
        if (threshold != null && threshold != 0) {
            ThresholdChooser.setValue(Integer.toString(threshold));
            if (checkFields())
                nextBtn.setDisable(false);
            else {
                nextBtn.setDisable(true);
            }
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, null, ARPDetection, 0, this.SMSHandler, this.EmailHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.intDisplayName = intDisplayName;
    }

    @FXML
    public void checkThresholdValue(ActionEvent event) {
        if (checkFields())
            nextBtn.setDisable(false);
        else {
            nextBtn.setDisable(true);
        }
    }

    public boolean checkFields() {
        return !ThresholdChooser.getSelectionModel().getSelectedItem().equals("Select Threshold");
    }

    @FXML
    public void goBackPreviousScreen(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CALandingSelectInt.fxml"));
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
            ControllerCALandingSelectInt controller = loader.getController();
            if (ThresholdChooser.getSelectionModel().getSelectedItem().equals("Select Threshold") || ThresholdChooser.getSelectionModel().getSelectedItem().equals("None"))
                controller.passVariables(handler, device, ARPSpoofingCheckbox.isSelected(), 0, SMSHandler, intDisplayName, CaptureType, EmailHandler);
            else
                controller.passVariables(handler, device, ARPSpoofingCheckbox.isSelected(), Integer.parseInt(ThresholdChooser.getSelectionModel().getSelectedItem()), SMSHandler, intDisplayName, CaptureType, EmailHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(nextView));
        stage.setTitle("Interface Selection");
        stage.show();
    }

    @FXML
    public void goToNextScreen(ActionEvent event) {
        if (!ARPSpoofingCheckbox.isSelected()) {
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();
            String title = "Confirmation Dialog";
            String content = "Are you sure that you want to leave ARP Detection disabled?";
            JFXButton yes = new JFXButton("Yes");
            yes.setButtonType(JFXButton.ButtonType.RAISED);
            yes.setStyle("-fx-background-color: #00bfff;-fx-spacing: 10px,20px,10px,20px;");
            JFXButton no = new JFXButton("No");
            no.setButtonType(JFXButton.ButtonType.RAISED);
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Label(title));
            layout.setBody(new Label(content));
            layout.setActions(no, yes);
            JFXAlert<Void> alert = new JFXAlert<>(stage);
            alert.setOverlayClose(true);
            alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
            alert.setContent(layout);
            alert.initModality(Modality.APPLICATION_MODAL);
            yes.setOnAction(event1 -> {
                alert.hideWithAnimation();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CALandingVerifyDetails.fxml"));
                Parent nextView = null;
                try {
                    nextView = loader.load();
                    ControllerCALandingVerifyDetails controller = loader.getController();
                    if (ThresholdChooser.getSelectionModel().getSelectedItem().equals("Select Threshold") || ThresholdChooser.getSelectionModel().getSelectedItem().equals("None"))
                        controller.passVariables(handler, device, false, 0, SMSHandler, intDisplayName, CaptureType, EmailHandler);
                    else
                        controller.passVariables(handler, device, false, Integer.parseInt(ThresholdChooser.getSelectionModel().getSelectedItem()), SMSHandler, intDisplayName, CaptureType, EmailHandler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setScene(new Scene(nextView));
                stage.setTitle("Verify Options");
                stage.show();
            });
            no.setOnAction(event12 -> {
                alert.hideWithAnimation();
            });
            alert.showAndWait();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CALandingVerifyDetails.fxml"));
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCALandingVerifyDetails controller = loader.getController();
                if (ThresholdChooser.getSelectionModel().getSelectedItem().equals("Select Threshold") || ThresholdChooser.getSelectionModel().getSelectedItem().equals("None"))
                    controller.passVariables(handler, device, true, 0, SMSHandler, intDisplayName, CaptureType, EmailHandler);
                else
                    controller.passVariables(handler, device, true, Integer.parseInt(ThresholdChooser.getSelectionModel().getSelectedItem()), SMSHandler, intDisplayName, CaptureType, EmailHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("Verify Options");
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

