import Model.CloudBuckets;
import Model.LoggingSnippets;
import Model.LogsExtract;
import com.google.api.gax.paging.Page;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerLoggingPage implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private TableView<LogsExtract> logsTable;

    @FXML
    private TableColumn<LogsExtract, String> timestamp;

    @FXML
    private TableColumn<LogsExtract, String> action;

    @FXML
    private TableColumn<LogsExtract, String> bucketName;

    @FXML
    private TableColumn<LogsExtract, String> user;

    @FXML
    private TableColumn<LogsExtract, String> projectID;

    @FXML
    private TableColumn<LogsExtract, String> severity;

    @FXML
    private JFXButton deletedlogs;

    @FXML
    private JFXButton createdlogs;

    private Scene myScene;

    public static AnchorPane rootP;

    LoggingSnippets loggingsnippets;
    LoggingOptions options;

    private ArrayList<LogsExtract> logsList;
    private ObservableList<LogsExtract> logsObservableList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        loggingsnippets = new LoggingSnippets();
        options = LoggingOptions.getDefaultInstance();

        //test inputfilter to trigger loglist
        loggingsnippets.listLogEntries("one two");

        timestamp.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("timestamp"));
        severity.setCellValueFactory(new PropertyValueFactory<LogsExtract,String>("severity"));
        action.setCellValueFactory(new PropertyValueFactory<LogsExtract,String>("action"));
        bucketName.setCellValueFactory(new PropertyValueFactory<LogsExtract,String>("bucketName"));
        user.setCellValueFactory(new PropertyValueFactory<LogsExtract,String>("user"));

        logsList = loggingsnippets.getLogsExtractList();
    }



    @FXML
    void handledeleted(MouseEvent event) {
        try (Logging logging = options.getService()) {
            System.out.println(options.getProjectId());
            loggingsnippets.listLogEntries("delete");
        } catch (Exception e) {
            e.printStackTrace();
        }
        logsObservableList = FXCollections.observableList(logsList);
        logsTable.setItems(logsObservableList);
    }

    @FXML
    void handlecreated(MouseEvent event) {

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
