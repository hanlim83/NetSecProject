import Model.ActionButtonTableCell;
import Model.CloudBuckets;
import Model.Device_Build_NumberDB;
import Model.OSVersion;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

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

    private Scene myScene;

    public static AnchorPane rootP;

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

//        revoke.setCellFactory(ActionButtonTableCell.<OSVersion>forTableColumn("Revoke", (OSVersion OSVers) -> {
//            osversion(OSVers);
//            return OSVers;
//        }));

        revoke.setCellFactory(ActionButtonTableCell.<OSVersion>forTableColumn("Revoke", (OSVersion OSVERSIONS) -> {
            //get entry id first
            osversion(OSVERSIONS);
            entry1 = OSVERSIONS.getEntryID();
       //     entry1 = deviceTable.getSelectionModel().getSelectedItem().getEntryID();
            System.out.println("Entry id " + entry1);
//          String deviceNAME=  deviceTable.getSelectionModel().getSelectedItem().getVersionName();
//            System.out.println("Version Number " +deviceNAME );
            entryid = Integer.toString(entry1);
            //call delete method in device build number db
            try {
                deviceDB.deleteOSVersion(entryid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //do if else deleted from arraylist
            deviceTable.getItems().remove(OSVERSIONS);
            return OSVERSIONS;
        }));

        osObservableList = FXCollections.observableList(osList);
        deviceTable.setItems(osObservableList);
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
