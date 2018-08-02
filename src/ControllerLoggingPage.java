import Model.CloudBuckets;
import Model.IPAddressPolicy;
import Model.LoggingSnippets;
import Model.LogsExtract;
import com.google.api.gax.paging.Page;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
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
    private Label ipAddr;

    @FXML
    private com.jfoenix.controls.JFXTreeTableView<LogsExtract> JFXTreeTableView;

    @FXML
    private PieChart piechart1;

    @FXML
    private AnchorPane treeTableAnchor;

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
    private JFXTextField searchFunction;

    @FXML
    private JFXSpinner spinner;

    private Scene myScene;

    public static AnchorPane rootP;

    LoggingSnippets loggingsnippets;
    LoggingOptions options;
    LogsExtract logsextract = new LogsExtract();

    private String myIPAddress;
    private boolean ipChecker;

    private ObservableList<PieChart.Data> errorReport = FXCollections.observableArrayList();

    private int GLOBALCHECKER = 0;
//    private ObservableList<TableBlob1> blobs;


    int colourchecker = 0;

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

//    Service process1 = new Service() {
//        @Override
//        protected Task createTask() {
//            return new Task() {
//                @Override
//                protected Void call() throws Exception {
//                    loggingsnippets = new LoggingSnippets();
//                    Platform.runLater(() -> {
//                        options = LoggingOptions.getDefaultInstance();
//
//                        popupanchor.setVisible(false);
//                        logsTable.setVisible(true);
//                        spinner.setVisible(false);
//
//                        timestamp.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("timestamp"));
//                        severity.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("severity"));
//                        action.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("action"));
//                        bucketName.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("bucketName"));
//                        globalCheckerList2 = logsextract.getGlobalChckerList();
////                      user.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("nonFinalEmail"));
//                        user.setCellValueFactory(new PropertyValueFactory<LogsExtract, String>("finalEmail"));
//
//                        severity.setCellFactory(new Callback<TableColumn<LogsExtract, String>, TableCell<LogsExtract, String>>() {
//                            @Override
//                            public TableCell<LogsExtract, String> call(TableColumn<LogsExtract, String> param) {
//                                return new TableCell<LogsExtract, String>() {
//
//                                    @Override
//                                    public void updateItem(String item, boolean empty) {
//                                        super.updateItem(item, empty);
//                                        if (!isEmpty()) {
//                                            this.setTextFill(Color.BLACK);
//                                            // Get fancy and change color based on data
//                                            if (item.contains("ERROR"))
//                                                this.setTextFill(Color.RED);
//                                            setText(item);
//
//                                        }
//                                    }
//
//                                };
//                            }
//                        });
//                        logsList = loggingsnippets.getLogsExtractList();
//                    });
//                    return null;
//                }
//            };
//        }
//    };

    private void TableMethod() {
        JFXTreeTableColumn<LogsExtract, String> timestampCol = new JFXTreeTableColumn<>("Timestamp");
        timestampCol.setPrefWidth(150);
        timestampCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<LogsExtract, String> param) -> {
            if (timestampCol.validateValue(param)) {
                return param.getValue().getValue().getTimestamp();
            } else {
                return timestampCol.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<LogsExtract, String> actionCol = new JFXTreeTableColumn<>("Task Category");
        actionCol.setPrefWidth(130);
        actionCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<LogsExtract, String> param) -> {
            if (actionCol.validateValue(param)) {
                return param.getValue().getValue().getAction();
            } else {
                return actionCol.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<LogsExtract, String> severityCol = new JFXTreeTableColumn<>("Severity");
        severityCol.setPrefWidth(140);
        severityCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<LogsExtract, String> param) -> {
            if (severityCol.validateValue(param)) {
                return param.getValue().getValue().getSeverity();
            } else {
                return severityCol.getComputedValue(param);
            }

        });

        JFXTreeTableColumn<LogsExtract, String> bucketCol = new JFXTreeTableColumn<>("Source");
        bucketCol.setPrefWidth(440);
        bucketCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<LogsExtract, String> param) -> {
            if (bucketCol.validateValue(param)) {
                return param.getValue().getValue().getBucketName();
            } else {
                return bucketCol.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<LogsExtract, String> emailCol = new JFXTreeTableColumn<>("Information");
        emailCol.setPrefWidth(200);
        emailCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<LogsExtract, String> param) -> {
            if (emailCol.validateValue(param)) {
                return param.getValue().getValue().getFinalEmail();
            } else {
                return emailCol.getComputedValue(param);
            }
        });

        timestampCol.setCellFactory((TreeTableColumn<LogsExtract, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        timestampCol.setOnEditCommit((TreeTableColumn.CellEditEvent<LogsExtract, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                .getValue().getTimestamp().set(t.getNewValue()));

        actionCol.setCellFactory((TreeTableColumn<LogsExtract, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        actionCol.setOnEditCommit((TreeTableColumn.CellEditEvent<LogsExtract, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                .getValue().getAction().set(t.getNewValue()));

        bucketCol.setCellFactory((TreeTableColumn<LogsExtract, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        bucketCol.setOnEditCommit((TreeTableColumn.CellEditEvent<LogsExtract, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                .getValue().getBucketName().set(t.getNewValue()));

        emailCol.setCellFactory((TreeTableColumn<LogsExtract, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        emailCol.setOnEditCommit((TreeTableColumn.CellEditEvent<LogsExtract, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                .getValue().getFinalEmail().set(t.getNewValue()));

        severityCol.setCellFactory((TreeTableColumn<LogsExtract, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        severityCol.setOnEditCommit((TreeTableColumn.CellEditEvent<LogsExtract, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                .getValue().getSeverity().set(t.getNewValue()));

        final TreeItem<LogsExtract> root = new RecursiveTreeItem<>(logsObservableList, RecursiveTreeObject::getChildren);

        JFXTreeTableView = new JFXTreeTableView<>(root);
        JFXTreeTableView.setShowRoot(false);
        JFXTreeTableView.setEditable(true);
        JFXTreeTableView.getColumns().setAll(timestampCol, actionCol, bucketCol, emailCol, severityCol);
        JFXTreeTableView.setEditable(false);
        treeTableAnchor.getChildren().add(JFXTreeTableView);

        severityCol.setCellFactory(new Callback<TreeTableColumn<LogsExtract, String>, TreeTableCell<LogsExtract, String>>() {
            @Override
            public TreeTableCell<LogsExtract, String> call(TreeTableColumn<LogsExtract, String> param) {
                return new TreeTableCell<LogsExtract, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            this.setTextFill(Color.BLUE);
                            // Get fancy and change color based on data
                            if (item.contains("ERROR")) {
                                this.setTextFill(Color.RED);
                            }
                            setText(item);
                        }
                    }
                };
            }
        });

        searchFunction.textProperty().addListener((o, oldVal1, newVal1) -> {
            JFXTreeTableView.setPredicate(userProp -> {
                final LogsExtract extracted = userProp.getValue();
                    return extracted.getTimestamp().get().toLowerCase().contains(newVal1.toLowerCase())
                            || extracted.getAction().get().toLowerCase().contains(newVal1.toLowerCase())
                            || extracted.getBucketName().get().toLowerCase().contains(newVal1.toLowerCase())
                            || extracted.getFinalEmail().get().toLowerCase().contains(newVal1.toLowerCase())
                            || extracted.getSeverity().get().toLowerCase().contains(newVal1.toLowerCase())
                            || extracted.getTimestamp().get().contains(newVal1)
                            || extracted.getAction().get().contains(newVal1)
                            || extracted.getBucketName().get().contains(newVal1)
                            || extracted.getFinalEmail().get().contains(newVal1)
                            || extracted.getSeverity().get().contains(newVal1);
            });
        });

        JFXTreeTableView.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (e.isPrimaryButtonDown()) {
                onEdit();
            }
        });

        errorReport.add(new PieChart.Data("Error Report",logsextract.getError()));
        errorReport.add(new PieChart.Data("Notice Report",logsextract.getNotice()));

        piechart1.setData(errorReport);

    }

    private void onEdit() {
        expandedDetails(anchorPane.getScene(), "Close");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loggingsnippets = new LoggingSnippets();
        options = LoggingOptions.getDefaultInstance();

        hamburgerBar();
        spinner.setVisible(false);

        try {
            String whatismyIP = IPAddressPolicy.getIp();
            ipAddr.setText(whatismyIP);
            Boolean validityIP = IPAddressPolicy.isValidRange(whatismyIP);
            if(validityIP==true){
                ipAddr.setTextFill(Color.rgb(1, 0, 199));
            }
            else{
                ipAddr.setTextFill(Color.rgb(255, 0, 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        logsList = loggingsnippets.getLogsExtractList();
        logsObservableList = FXCollections.observableList(logsList);

        searchFunction.setVisible(false);
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
        grid.setPrefSize(650,330);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 100, 10, 0));
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        col1.setHalignment(HPos.LEFT);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(75);
        grid.getColumnConstraints().addAll(col1, col2);

        StringProperty timestamp0 = JFXTreeTableView.getSelectionModel().getSelectedItem().getValue().getTimestamp();
        Label LABEL0 = new Label();
//        LABEL0.setFont(new Font(LABEL0.getFont().getName(),11));
        LABEL0.setText(String.valueOf(timestamp0).substring(23, String.valueOf(timestamp0).length() - 1));
        StringProperty severity0 = JFXTreeTableView.getSelectionModel().getSelectedItem().getValue().getSeverity();
        Label LABEL1 = new Label();
//        LABEL1.setFont(new Font(LABEL0.getFont().getName(),11));
        String severity00= String.valueOf(severity0);
        if(severity00.contains("ERROR")){
            LABEL1.setTextFill(Color.rgb(255, 0, 0));
            LABEL1.setText(String.valueOf(severity0).substring(23, String.valueOf(severity0).length() - 1));
        }else{
            LABEL1.setTextFill(Color.rgb(1, 0, 199));
            LABEL1.setText(String.valueOf(severity0).substring(23, String.valueOf(severity0).length() - 1));
        }

        StringProperty action0 = JFXTreeTableView.getSelectionModel().getSelectedItem().getValue().getAction();
        Label LABEL2 = new Label();
//        LABEL2.setFont(new Font(LABEL0.getFont().getName(),11));
        LABEL2.setText(String.valueOf(action0).substring(23, String.valueOf(action0).length() - 1));
        LABEL2.setWrapText(true);
        StringProperty bucketname0 = JFXTreeTableView.getSelectionModel().getSelectedItem().getValue().getBucketName();
        Label LABEL3 = new Label();
//        LABEL3.setFont(new Font(LABEL0.getFont().getName(),11));
        LABEL3.setText(String.valueOf(bucketname0).substring(23, String.valueOf(bucketname0).length() - 1));
        LABEL3.setWrapText(true);
        StringProperty user0 = JFXTreeTableView.getSelectionModel().getSelectedItem().getValue().getFinalEmail();
        Label LABEL4 = new Label();
//        LABEL4.setFont(new Font(LABEL0.getFont().getName(),11));
        LABEL4.setText(String.valueOf(user0).substring(23, String.valueOf(user0).length() - 1));

        String string1 = "Timestamp :";
        Label label1 = new Label();
//        label1.setFont(new Font(LABEL0.getFont().getName(),11));
        label1.setTextFill(Color.rgb(1, 0, 199));
        label1.setText(string1);

        String string2 = "Severity :";
        Label label2 = new Label();
//        label2.setFont(new Font(LABEL0.getFont().getName(),11));
        label2.setTextFill(Color.rgb(1, 0, 199));
        label2.setText(string2);

        String string3 = "Task Category :";
        Label label3 = new Label();
//        label3.setFont(new Font(LABEL0.getFont().getName(),11));
        label3.setTextFill(Color.rgb(1, 0, 199));
        label3.setText(string3);

        String string4 = "Source :";
        Label label4 = new Label();
//        label4.setFont(new Font(LABEL0.getFont().getName(),11));
        label4.setTextFill(Color.rgb(1, 0, 199));
        label4.setText(string4);

        String string5 = "Information :";
        Label label5 = new Label();
//        label5.setFont(new Font(LABEL0.getFont().getName(),11));
        label5.setTextFill(Color.rgb(1, 0, 199));
        label5.setText(string5);


        grid.add(label1, 0, 0);
        grid.add(label2, 0, 1);
        grid.add(label3, 0, 2);
        grid.add(label4, 0, 3);
        grid.add(label5, 0, 4);

        grid.add(LABEL0, 1, 0);
        grid.add(LABEL1, 1, 1);
        grid.add(LABEL2, 1, 2);
        grid.add(LABEL3, 1, 3);
        grid.add(LABEL4, 1, 4);

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
        GLOBALCHECKER=0;
        try {
            myIPAddress= IPAddressPolicy.getIp();
            System.out.println(myIPAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //    logsTable.getItems().clear();
        createdlogs.setDisable(true);
        general.setDisable(true);
        deletedlogs.setDisable(true);
        spinner.setVisible(true);
        //   logsTable.setVisible(true);
        popupanchor.setVisible(false);
        filters = "delete";

        ipChecker=IPAddressPolicy.isValidRange(myIPAddress);
        System.out.println("IS IT WITHIN IP RANGE? = "+ipChecker);
        if(ipChecker==false) {
            //Display not within ip range error message
            errorMessage = "You are not within the company's premises to perform this function.";
            errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            process.reset();
            spinner.setVisible(false);
            general.setDisable(false);
            deletedlogs.setDisable(false);
            createdlogs.setDisable(false);
        }
        else {
            process.start();
        }
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
        GLOBALCHECKER=0;
        try {
            myIPAddress=IPAddressPolicy.getIp();
            System.out.println(myIPAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //      logsTable.getItems().clear();
        createdlogs.setDisable(true);
        deletedlogs.setDisable(true);
        general.setDisable(true);
        spinner.setVisible(true);
        //      logsTable.setVisible(true);
        popupanchor.setVisible(false);
        filters = "create";

        ipChecker=IPAddressPolicy.isValidRange(myIPAddress);
        System.out.println("IS IT WITHIN IP RANGE? = "+ipChecker);
        if(ipChecker==false) {
            //Display not within ip range error message
            errorMessage = "You are not within the company's premises to perform this function.";
            errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            process.reset();
            spinner.setVisible(false);
            general.setDisable(false);
            deletedlogs.setDisable(false);
            createdlogs.setDisable(false);
        }
        else {
            process.start();
        }
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
        GLOBALCHECKER=0;
        try {
            myIPAddress=IPAddressPolicy.getIp();
            System.out.println(myIPAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //    logsTable.getItems().clear();
        general.setDisable(true);
        deletedlogs.setDisable(true);
        createdlogs.setDisable(true);
        spinner.setVisible(true);
        //     logsTable.setVisible(true);
        popupanchor.setVisible(false);
        filters = "one two";

        ipChecker=IPAddressPolicy.isValidRange(myIPAddress);
        System.out.println("IS IT WITHIN IP RANGE? = "+ipChecker);
        if(ipChecker==false) {
            //Display not within ip range error message
            errorMessage = "You are not within the company's premises to perform this function.";
            errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            process.reset();
            spinner.setVisible(false);
            general.setDisable(false);
            deletedlogs.setDisable(false);
            createdlogs.setDisable(false);
        }
        else {
            process.start();
        }
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
                        TableMethod();
                        searchFunction.setVisible(true);

                        JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                        snackbar.getStylesheets().add("Style.css");
                        snackbar.show("Updating The Logs", 3000);
                    });
                    return null;
                }
            };
        }
    };

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
