import Model.CloudBuckets;
import Model.LoggingSnippets;
import Model.LogsExtract;
import com.google.api.gax.paging.Page;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
//HI XD
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

    @FXML
    private JFXSpinner spinner;

    private Scene myScene;

    public static AnchorPane rootP;

    LoggingSnippets loggingsnippets;
    LoggingOptions options;
    LogsExtract logsextract = new LogsExtract();

    private int spinnerchecker;

    private String filters;

    private ArrayList<LogsExtract> logsList;
    private ObservableList<LogsExtract> logsObservableList;

    private ArrayList<Integer> globalCheckerList2 = new ArrayList<Integer>();

    Service process1 = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    loggingsnippets = new LoggingSnippets();
                    Platform.runLater(() -> {
                        options = LoggingOptions.getDefaultInstance();

                        popupanchor.setVisible(false);
                        logsTable.setVisible(true);
                        spinner.setVisible(false);

                        timestamp.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("timestamp"));
                        severity.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("severity"));
                        action.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("action"));
                        bucketName.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("bucketName"));
                        globalCheckerList2 = logsextract.getGlobalChckerList();
//                      user.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("nonFinalEmail"));
                        user.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("finalEmail"));

//                      globalCheckerList2 = logsextract.getGlobalChckerList();
//                      int check = logsextract.getGlobalchecker();
//                        System.out.println("MY CHECKER IS NOW " + check);
//                         if (check==-1) {
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
                    });
                    return null;
                }
            };
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        spinner.setVisible(false);
        process1.start();
//        loggingsnippets = new LoggingSnippets();
//        options = LoggingOptions.getDefaultInstance();
//
//        popupanchor.setVisible(false);
//        logsTable.setVisible(true);
//        spinner.setVisible(false);
//
//        timestamp.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("timestamp"));
//        severity.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("severity"));
//        action.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("action"));
//        bucketName.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("bucketName"));
//        globalCheckerList2 = logsextract.getGlobalChckerList();
////        user.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("nonFinalEmail"));
//        user.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("finalEmail"));
//
////        globalCheckerList2 = logsextract.getGlobalChckerList();
////        int check = logsextract.getGlobalchecker();
////        System.out.println("MY CHECKER IS NOW " + check);
////        if (check==-1) {
////            System.out.println("MY CHECKER IS NOW " + check);
////            user.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("nonFinalEmail"));
////
////        } else {
////            System.out.println("MY CHECKER IS NOW " + check);
////            user.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("finalEmail"));
////
////        }
//        //       projectID.setCellValueFactory(new PropertyValueFactory<LogsExtract,String>("projectID"));
//        logsList = loggingsnippets.getLogsExtractList();
    }


    @FXML
    public void clickItem(MouseEvent event) {
        try {
            if (event.getClickCount() == 2) //Checking double click
            {
                spinner.setVisible(false);
                logsTable.setVisible(false);
                popupanchor.setVisible(true);
                timestamp1.setText(logsTable.getSelectionModel().getSelectedItem().getTimestamp());
                severity1.setText(logsTable.getSelectionModel().getSelectedItem().getSeverity());
                action1.setText(logsTable.getSelectionModel().getSelectedItem().getAction());
                bucketName1.setText(logsTable.getSelectionModel().getSelectedItem().getBucketName());
                user1.setText(logsTable.getSelectionModel().getSelectedItem().getFinalEmail());
            }
        } catch (com.google.cloud.logging.LoggingException e1) {
            e1.printStackTrace();
        } catch (io.grpc.StatusRuntimeException e2) {
            e2.printStackTrace();
        }
    }

    @FXML
    void handledeleted(MouseEvent event) {
        logsTable.getItems().clear();
        createdlogs.setDisable(true);
        general.setDisable(true);
        deletedlogs.setDisable(true);
        spinner.setVisible(true);
        logsTable.setVisible(true);
        popupanchor.setVisible(false);
        filters = "delete";
        process.start();

        process.setOnSucceeded(e -> {
            spinner.setVisible(false);
            popupanchor.setVisible(false);
            createdlogs.setDisable(false);
            general.setDisable(false);
            deletedlogs.setDisable(false);
            process.reset();
        });
        process.setOnCancelled(e -> {
            process.reset();
        });
        process.setOnFailed(e -> {
            process.reset();
        });
    }


    @FXML
    void handlecreated(MouseEvent event) {
        logsTable.getItems().clear();
        createdlogs.setDisable(true);
        deletedlogs.setDisable(true);
        general.setDisable(true);
        spinner.setVisible(true);
        logsTable.setVisible(true);
        popupanchor.setVisible(false);
        filters = "create";
        process.start();

        process.setOnSucceeded(e -> {
            spinner.setVisible(false);
            createdlogs.setDisable(false);
            popupanchor.setVisible(false);
            deletedlogs.setDisable(false);
            general.setDisable(false);
            process.reset();
        });
        process.setOnCancelled(e -> {
            process.reset();
        });
        process.setOnFailed(e -> {
            process.reset();
        });
//        try (Logging logging = options.getService()) {
//            System.out.println(options.getProjectId());
//            loggingsnippets.listLogEntries("create");
//        } catch (com.google.cloud.logging.LoggingException e1) {
//            e1.printStackTrace();
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//        logsObservableList = FXCollections.observableList(logsList);
//        logsTable.setItems(logsObservableList);
    }


    @FXML
    void handleonetwo(MouseEvent event) {
        logsTable.getItems().clear();
        general.setDisable(true);
        deletedlogs.setDisable(true);
        createdlogs.setDisable(true);
        spinner.setVisible(true);
        logsTable.setVisible(true);
        popupanchor.setVisible(false);
        filters = "one two";
        process.start();

        process.setOnSucceeded(e -> {
            general.setDisable(false);
            deletedlogs.setDisable(false);
            createdlogs.setDisable(false);
            spinner.setVisible(false);
            popupanchor.setVisible(false);
            process.reset();
        });
        process.setOnCancelled(e -> {
            process.reset();
        });
        process.setOnFailed(e -> {
            process.reset();
        });
//        try (Logging logging = options.getService()) {
//            System.out.println(options.getProjectId());
//            loggingsnippets.listLogEntries("one two");
//        } catch (com.google.cloud.logging.LoggingException e1) {
//            e1.printStackTrace();
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//        logsObservableList = FXCollections.observableList(logsList);
//        logsTable.setItems(logsObservableList);
    }

    //Service method
    Service process = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    try (Logging logging = options.getService()) {
                        System.out.println(options.getProjectId());
                        loggingsnippets.listLogEntries(filters);
                    } catch (com.google.cloud.logging.LoggingException e1) {
                        e1.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> {
                        logsObservableList = FXCollections.observableList(logsList);
                        logsTable.setItems(logsObservableList);
                    });
                    return null;
                }
            };
        }
    };

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
