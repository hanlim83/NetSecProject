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
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import sun.rmi.runtime.Log;

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

    int colourchecker=0;

    private int spinnerchecker;

    String errorMessage = "";
    String successfulMessage = "";
    String doubleConfirm = "";
    int CHECKING;
    int checker2;

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

                        severity.setCellFactory(new Callback<TableColumn<LogsExtract, String>, TableCell<LogsExtract, String>>() {
                            @Override
                            public TableCell<LogsExtract, String> call(TableColumn<LogsExtract, String> param) {
                                return new TableCell<LogsExtract, String>() {

                                    @Override
                                    public void updateItem(String item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (!isEmpty()) {
                                            this.setTextFill(Color.BLACK);
                                            // Get fancy and change color based on data
                                            if(item.contains("ERROR"))
                                                this.setTextFill(Color.RED);
                                            setText(item);

                                        }
                                    }

                                };
                            }
                        });
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
    }


    @FXML
    public void clickItem(MouseEvent event) {
        try {
            if (event.getClickCount() == 2) //Checking double click
            {
                spinner.setVisible(false);
                expandedDetails(anchorPane.getScene(), "Close");
            }
        } catch (com.google.cloud.logging.LoggingException e1) {
            e1.printStackTrace();
        } catch (io.grpc.StatusRuntimeException e2) {
            e2.printStackTrace();
        }
    }

    private void expandedDetails(Scene scene, String buttonContent) {
        myScene = scene;
        Stage stage = (Stage) (myScene).getWindow();

        String title = "Full-Details Logs Information";

        JFXButton close = new JFXButton(buttonContent);
        close.setButtonType(JFXButton.ButtonType.RAISED);
        close.setStyle("-fx-background-color: #00bfff;");

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setPrefSize(670, 350);
        layout.setHeading(new Label(title));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 100, 10, 0));
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        col1.setHalignment(HPos.LEFT);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(75);
        grid.getColumnConstraints().addAll(col1,col2);

        String timestamp1 = logsTable.getSelectionModel().getSelectedItem().getTimestamp();
        String severity1 = logsTable.getSelectionModel().getSelectedItem().getSeverity();
        String action1 = logsTable.getSelectionModel().getSelectedItem().getAction();
        String bucketname1 = logsTable.getSelectionModel().getSelectedItem().getBucketName();
        String user1 = logsTable.getSelectionModel().getSelectedItem().getFinalEmail();

        String string1 = "Timestamp :";
        Label label1 = new Label();
        label1.setTextFill(Color.rgb(1, 0, 199));
        label1.setText(string1);

        String string2 = "Severity :";
        Label label2 = new Label();
        label2.setTextFill(Color.rgb(1, 0, 199));
        label2.setText(string2);

        String string3 = "Action :";
        Label label3 = new Label();
        label3.setTextFill(Color.rgb(1, 0, 199));
        label3.setText(string3);

        String string4 = "Bucket Name :";
        Label label4 = new Label();
        label4.setTextFill(Color.rgb(1, 0, 199));
        label4.setText(string4);

        String string5 = "User :";
        Label label5 = new Label();
        label5.setTextFill(Color.rgb(1, 0, 199));
        label5.setText(string5);


        grid.add(label1,0,0);
        grid.add(label2,0,1);
        grid.add(label3,0,2);
        grid.add(label4,0,3);
        grid.add(label5,0,4);

        grid.add(new Label(timestamp1), 1, 0);
        grid.add(new Label(severity1), 1, 1);
        grid.add(new Label(action1), 1, 2);
        grid.add(new Label(bucketname1), 1, 3);
        grid.add(new Label(user1), 1, 4);

        layout.setBody(grid);

        layout.setActions(close);

        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);

        close.setOnAction(__ -> alert.hideWithAnimation());

        alert.show();
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

                        JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                        snackbar.show("Updating The Logs", 3000);
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
