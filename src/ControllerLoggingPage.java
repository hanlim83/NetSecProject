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
import javafx.scene.control.Label;
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

//    @FXML
//    private TableColumn<LogsExtract, String> projectID;

    @FXML
    private TableColumn<LogsExtract, String> severity;

    @FXML
    private JFXButton deletedlogs;

    @FXML
    private JFXButton createdlogs;

    @FXML
    private JFXButton general;

    @FXML
    private AnchorPane popupanchor;

    @FXML
    private Label timestamp1;

    @FXML
    private Label action1;

    @FXML
    private Label bucketName1;

    @FXML
    private Label user1;

    @FXML
    private Label severity1;

    private Scene myScene;

    public static AnchorPane rootP;

    LoggingSnippets loggingsnippets;
    LoggingOptions options;
    LogsExtract logsextract = new LogsExtract();

    private ArrayList<LogsExtract> logsList;
    private ObservableList<LogsExtract> logsObservableList;

    private ArrayList<Integer> globalCheckerList2 = new ArrayList<Integer>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        loggingsnippets = new LoggingSnippets();
        options = LoggingOptions.getDefaultInstance();

        popupanchor.setVisible(false);
        logsTable.setVisible(true);


        //test inputfilter to trigger loglist
        //  loggingsnippets.listLogEntries("one two");

        timestamp.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("timestamp"));
        severity.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("severity"));
        action.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("action"));
        bucketName.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("bucketName"));
        globalCheckerList2 = logsextract.getGlobalChckerList();
//        user.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("nonFinalEmail"));
        user.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("finalEmail"));


//        globalCheckerList2 = logsextract.getGlobalChckerList();
//        int check = logsextract.getGlobalchecker();
//        System.out.println("MY CHECKER IS NOW " + check);
//        if (check==-1) {
//            System.out.println("MY CHECKER IS NOW " + check);
//            user.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("nonFinalEmail"));
//
//        } else {
//            System.out.println("MY CHECKER IS NOW " + check);
//            user.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("finalEmail"));
//
//        }
        //       projectID.setCellValueFactory(new PropertyValueFactory<LogsExtract,String>("projectID"));
        logsList = loggingsnippets.getLogsExtractList();
    }

    @FXML
    public void clickItem(MouseEvent event) {
        try {
            if (event.getClickCount() == 2) //Checking double click
            {
                logsTable.setVisible(false);
                popupanchor.setVisible(true);
                timestamp1.setText(logsTable.getSelectionModel().getSelectedItem().getTimestamp());
                severity1.setText(logsTable.getSelectionModel().getSelectedItem().getSeverity());
                action1.setText(logsTable.getSelectionModel().getSelectedItem().getAction());
                bucketName1.setText(logsTable.getSelectionModel().getSelectedItem().getBucketName());
                user1.setText(logsTable.getSelectionModel().getSelectedItem().getFinalEmail());
            }
        }catch (com.google.cloud.logging.LoggingException e1) {
            e1.printStackTrace();
        } catch(io.grpc.StatusRuntimeException e2) {
            e2.printStackTrace();
        }
    }

    @FXML
    void handledeleted(MouseEvent event) {
        popupanchor.setVisible(false);
        logsTable.setVisible(true);
        try (Logging logging = options.getService()) {
            System.out.println(options.getProjectId());
            loggingsnippets.listLogEntries("delete");
        } catch (com.google.cloud.logging.LoggingException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logsObservableList = FXCollections.observableList(logsList);
        logsTable.setItems(logsObservableList);
    }

    @FXML
    void handlecreated(MouseEvent event) {
        popupanchor.setVisible(false);
        logsTable.setVisible(true);
        try (Logging logging = options.getService()) {
            System.out.println(options.getProjectId());
            loggingsnippets.listLogEntries("create");
        } catch (com.google.cloud.logging.LoggingException e1) {
            e1.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        logsObservableList = FXCollections.observableList(logsList);
        logsTable.setItems(logsObservableList);
    }


    @FXML
    void handleonetwo(MouseEvent event) {
        popupanchor.setVisible(false);
        logsTable.setVisible(true);
        try (Logging logging = options.getService()) {
            System.out.println(options.getProjectId());
            loggingsnippets.listLogEntries("one two");
        } catch (com.google.cloud.logging.LoggingException e1) {
            e1.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        logsObservableList = FXCollections.observableList(logsList);
        logsTable.setItems(logsObservableList);
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
