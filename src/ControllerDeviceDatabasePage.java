import Model.*;
import com.google.cloud.logging.LoggingOptions;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

//here LOL 11.01pm
public class ControllerDeviceDatabasePage implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private TableView<OSVersion> deviceTable;

    @FXML
    private TableColumn<OSVersion, String> versionName;

    @FXML
    private TableColumn<OSVersion, String> versionNumber;

    @FXML
    private TableColumn<OSVersion, String> entryID;

    @FXML
    private TableColumn<OSVersion, Button> revoke;

    @FXML
    private JFXButton insertOS;

    @FXML
    private AnchorPane insertAnchor;

    @FXML
    private JFXButton viewButton;

    @FXML
    private JFXSpinner spinner;

    private Scene myScene;

    public static AnchorPane rootP;

    String errorMessage="";
    String successfulMessage="";
    String doubleConfirm = "";
    int CHECKING;
    int checker2;

    String verName;
    String verNumber;

    Device_Build_NumberDB deviceDB = new Device_Build_NumberDB();
    OSVersion osvers = new OSVersion();

    private ArrayList<OSVersion> osList;
    private ObservableList<OSVersion> osObservableList;

    private int entry1;
    private String entryid;

    public OSVersion osversion(OSVersion osversion) {
        osvers = osversion;
        return osvers;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        versionName.setCellValueFactory(new PropertyValueFactory<OSVersion, String>("versionName"));
        versionNumber.setCellValueFactory(new PropertyValueFactory<OSVersion, String>("versionNumber"));
        entryID.setCellValueFactory(new PropertyValueFactory<OSVersion, String>("entryID"));

        try {
            osList = deviceDB.CheckSupportedVersion();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        revoke.setCellFactory(ActionButtonTableCell.<OSVersion>forTableColumn("Revoke", (OSVersion OSVERSIONS) -> {
            //get entry id first
            osversion(OSVERSIONS);
            entry1 = OSVERSIONS.getEntryID();

            doubleConfirm = "This selected OS Version \"" + OSVERSIONS.getVersionName()+ "\" will be removed from the cloud. Are you sure to delete it?";
            doubleConfirmation(anchorPane.getScene(), doubleConfirm, "No", "Yes");
            CHECKING=checker2;
            System.out.println("CHECKER NOW IS " + CHECKING);

            System.out.println("Entry id " + entry1);
            entryid = Integer.toString(entry1);

            return OSVERSIONS;
        }));

        osObservableList = FXCollections.observableList(osList);
        deviceTable.setItems(osObservableList);
    }

    @FXML
    void handleView(MouseEvent event) {
        process1.start();
        spinner.setVisible(true);

        process1.setOnSucceeded(e -> {
            spinner.setVisible(false);
            process1.reset();
        });
        process1.setOnCancelled(e -> {
            process1.reset();
        });
        process1.setOnFailed(e -> {
            process1.reset();
        });
    }

    Service process1 = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    try {
                        osList = deviceDB.CheckSupportedVersion();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> {
                        JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                        snackbar.show("Updating The Database", 3000);
                        osObservableList = FXCollections.observableList(osList);
                        deviceTable.setItems(osObservableList);
                    });
                    return null;
                }
            };
        }
    };



    @FXML
    void handleInsertOS(MouseEvent event) {
        creatingOS(anchorPane.getScene(), "No","Yes");
    }

    private void creatingOS(Scene scene, String buttonContent, String buttonContent2) {
        myScene = scene;
        Stage stage = (Stage) (myScene).getWindow();

        String message = "Please input the follow required data to insert the OS Versions.";
        String title = "Insert OS Versions" + "\n" + message;

        JFXButton no = new JFXButton(buttonContent);
        no.setButtonType(JFXButton.ButtonType.RAISED);
        no.setStyle("-fx-background-color: #00bfff;");

        JFXButton yes = new JFXButton(buttonContent2);
        yes.setButtonType(JFXButton.ButtonType.RAISED);
        yes.setStyle("-fx-background-color: #ff2828");

        String message1 = "Enter OS Version Number: ";
        String message2 = "Enter OS Version Name: ";

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setPrefSize(600, 270);
        layout.setHeading(new Label(title));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 100, 10, 0));

        JFXTextField inputOSName = new JFXTextField();
        inputOSName.setPrefWidth(250);
        inputOSName.setPromptText("Type OS Version Name Here");

        grid.add(new Label(message2), 0, 0);
        grid.add(inputOSName, 1, 0);

        JFXTextField inputOSNumber = new JFXTextField();
        inputOSNumber.setPrefWidth(250);
        inputOSNumber.setPromptText("Type OS Version Number Here");

        grid.add(new Label(message1), 0, 1);
        grid.add(inputOSNumber, 1, 1);

        layout.setBody(grid);

        layout.setActions(no, yes);

        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);

        yes.setOnAction(__addEvent -> {
            checker2 = 1;
            CHECKING = -1;
            System.out.println("YES IS PRESSED, CHECKER2 is " + checker2);
            CHECKING = checker2;
            verName = inputOSName.getText();
            System.out.println("INPUT NAME IS : " + verName);
            verNumber = inputOSNumber.getText();
            System.out.println("INPUT NUMBER IS : " + verNumber);
                try {
                    deviceDB.insertNewOSVersion(verName,verNumber);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                process1.start();
                successfulMessage = "This OS Version " + verName + " was created and added into the cloud. The device number is " + verNumber + ".";
                successfulMessage(anchorPane.getScene(), successfulMessage, "Close");
                alert.hideWithAnimation();
        });
        no.setOnAction(__addEvent -> {
            checker2 = 0;
            System.out.println("NO IS PRESSED, CHECKER2 is " + checker2);

            alert.hideWithAnimation();
        });

        alert.show();
    }

    private void doubleConfirmation(Scene scene, String doubleconfirm, String buttonContent, String buttonContent2) {
        checker2=-1;
        myScene = scene;
        Stage stage = (Stage) (myScene).getWindow();

        String message = doubleconfirm;
        String title = "Are you sure?";

        JFXButton no = new JFXButton(buttonContent);
        no.setButtonType(JFXButton.ButtonType.RAISED);
        no.setStyle("-fx-background-color: #00bfff;");

        JFXButton yes = new JFXButton(buttonContent2);
        yes.setButtonType(JFXButton.ButtonType.RAISED);
        yes.setStyle("-fx-background-color: #ff2828");


        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(title));
        layout.setBody(new Label(message));

        layout.setActions(no, yes);

        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);

        //GET WHETHER PRESS YES or NO
        yes.setOnAction(__addEvent -> {
            checker2 = 1;
            CHECKING = -1;
            System.out.println("YES IS PRESSED, CHECKER2 is " + checker2);

            CHECKING=checker2;
            try {
                deviceDB.deleteOSVersion(entryid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            deviceTable.getItems().remove(osvers);
            successfulMessage = "OS Version removed Successfully";
            successfulMessage(anchorPane.getScene(), successfulMessage, "Close");
            alert.hideWithAnimation();
        });
        no.setOnAction(__addEvent -> {
            checker2 = 0;
            System.out.println("NO IS PRESSED, CHECKER2 is " + checker2);
            errorMessage = "OS Version was not removed successfully";
            errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            alert.hideWithAnimation();
        });
        alert.show();
    }

    private void errorMessagePopOut(Scene scene, String errorMessage, String buttonContent) {
        myScene = scene;
        Stage stage = (Stage) (myScene).getWindow();

        String message = errorMessage;
        String title = "ERROR";
        JFXButton close = new JFXButton(buttonContent);

        close.setButtonType(JFXButton.ButtonType.RAISED);

        close.setStyle("-fx-background-color: #00bfff;");

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(title));
        layout.setBody(new Label(message));
        layout.setActions(close);
        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);
        close.setOnAction(__ -> alert.hideWithAnimation());
        alert.show();
    }

    private void successfulMessage(Scene scene, String successfulMessage, String buttonContent) {
        myScene = scene;
        Stage stage = (Stage) (myScene).getWindow();

        String message = successfulMessage;
        String title = "Success";
        JFXButton close = new JFXButton(buttonContent);

        close.setButtonType(JFXButton.ButtonType.RAISED);

        close.setStyle("-fx-background-color: #00bfff;");

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(title));
        layout.setBody(new Label(message));
        layout.setActions(close);
        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);
        close.setOnAction(__ -> alert.hideWithAnimation());
        alert.show();
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

}
