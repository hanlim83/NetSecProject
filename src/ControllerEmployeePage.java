import Database.User;
import Database.User_InfoDB;
import Model.GetIAM;
import Model.IAMExtract;
import Model.IAMPermissions;
import com.google.cloud.logging.Logging;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
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
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ControllerEmployeePage implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXButton listPermissions;

    @FXML
    private JFXButton employeeButton;

    @FXML
    private JFXSpinner spinner;

    @FXML
    private TableView<User> employeeTable;

    @FXML
    private TableColumn<User, String> entryID;

    @FXML
    private TableColumn<User, String> email;

    @FXML
    private TableColumn<User, String> handphone;

    @FXML
    private TableColumn<User, String> status;

    @FXML
    private TableColumn<User, String> hashpassword;

    @FXML
    private JFXComboBox<String> jfxcombobox;

    @FXML
    private TableView<IAMExtract> rolesTable;

    @FXML
    private TableColumn<IAMExtract, String> roleColumn;

    @FXML
    private TableColumn<IAMExtract, String> userColumn;

    @FXML
    private AnchorPane secondAnchor;

    private Scene myScene;

    public static AnchorPane rootP;

    User_InfoDB userinfodb = new User_InfoDB();
    User users = new User();

    private ArrayList<User> userList;
    private ObservableList<User> userObservableList;

    IAMPermissions permissions;
    GetIAM getiam = new GetIAM();
    IAMExtract iamExtract = new IAMExtract();

    static int globalChecker;

    private String chosenRole;

    String listPermission;

    ArrayList<IAMExtract> getIAMLists;

    private ObservableList<IAMExtract> cloudsqlObservableList;
    private ObservableList<IAMExtract> ownerObservableList;
    private ObservableList<IAMExtract> editorObservableList;
    private ObservableList<IAMExtract> viewerObservableList;
    private ObservableList<IAMExtract> firebaseObservableList;
    private ObservableList<IAMExtract> computeengineObservableList;
    private ObservableList<IAMExtract> loggingadminObservableList;
    private ObservableList<IAMExtract> monitoringadminObservableList;
    private ObservableList<IAMExtract> apikeysadminObservableList;
    private ObservableList<IAMExtract> storageadminObservableList;


    ArrayList<String> list1 = new ArrayList<>();
    String allInformation;

    public User user(User user) {
        users = user;
        return users;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        entryID.setCellValueFactory(new PropertyValueFactory<User, String>("entryID"));
        email.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        handphone.setCellValueFactory(new PropertyValueFactory<User, String>("phoneNo"));
        status.setCellValueFactory(new PropertyValueFactory<User, String>("status"));
        hashpassword.setCellValueFactory(new PropertyValueFactory<User, String>("hashPassword"));

        try {
            userList = userinfodb.getUserList();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        userObservableList = FXCollections.observableList(userList);
        employeeTable.setItems(userObservableList);

        jfxcombobox.getItems().addAll("Owner", "Editor", "Viewer", "CloudSQL Admin", "Firebase Rules System", "Compute Engine Service", "Logging Admin", "Storage Admin", "Monitoring Admin", "API Keys Admin");

        userColumn.setCellValueFactory(new PropertyValueFactory<IAMExtract, String>("globalUser"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<IAMExtract, String>("globalRole"));

        getIAMLists = getiam.getExtractingIAM();
    }

    @FXML
    void handleListPermissions(MouseEvent event) {
        secondAnchor.setVisible(true);
        employeeTable.setVisible(false);
    }

    Service process = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    permissions = new IAMPermissions();
                    if (chosenRole.equals("Owner")) {
                        globalChecker = 1;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
//                        getiam.splitDiffArrayList();
                    } else if (chosenRole == "Editor") {
                        globalChecker = 2;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
//                        getiam.splitDiffArrayList();
                    } else if (chosenRole == "Viewer") {
                        globalChecker = 3;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
//                        getiam.splitDiffArrayList();

                    } else if (chosenRole == "CloudSQL Admin") {
                        globalChecker = 4;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
//                        getiam.splitDiffArrayList();

                    } else if (chosenRole == "Firebase Rules System") {
                        globalChecker = 5;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        getiam.splitDiffArrayList();

                    } else if (chosenRole == "Compute Engine Service") {
                        globalChecker = 6;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        getiam.splitDiffArrayList();

                    } else if (chosenRole == "Logging Admin") {
                        globalChecker = 7;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        getiam.splitDiffArrayList();

                    } else if (chosenRole == "Storage Admin") {
                        globalChecker = 8;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        getiam.splitDiffArrayList();

                    } else if (chosenRole == "Monitoring Admin") {
                        globalChecker = 9;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        getiam.splitDiffArrayList();

                    } else {
                        globalChecker = 10;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        getiam.splitDiffArrayList();

                    }

                    Platform.runLater(() -> {
                        if (globalChecker == 1){
                            ownerObservableList = FXCollections.observableList(getIAMLists);
                            rolesTable.setItems(ownerObservableList);
                        }
                        else if (globalChecker == 2) {
                            editorObservableList = FXCollections.observableList(getIAMLists);
                            rolesTable.setItems(editorObservableList);
                        } else if (globalChecker == 3) {
                            viewerObservableList = FXCollections.observableList(getIAMLists);
                            rolesTable.setItems(viewerObservableList);
                        } else if (globalChecker == 4) {
                            cloudsqlObservableList = FXCollections.observableList(getIAMLists);
                            rolesTable.setItems(cloudsqlObservableList);
                        } else if (globalChecker == 5) {
                            firebaseObservableList = FXCollections.observableList(getIAMLists);
                            rolesTable.setItems(firebaseObservableList);
                        } else if (globalChecker == 6) {
                            computeengineObservableList = FXCollections.observableList(getIAMLists);
                            rolesTable.setItems(computeengineObservableList);
                        } else if (globalChecker == 7) {
                            loggingadminObservableList = FXCollections.observableList(getIAMLists);
                            rolesTable.setItems(loggingadminObservableList);
                        } else if (globalChecker == 8) {
                            storageadminObservableList = FXCollections.observableList(getIAMLists);
                            rolesTable.setItems(storageadminObservableList);
                        } else if (globalChecker == 9) {
                            monitoringadminObservableList = FXCollections.observableList(getIAMLists);
                            rolesTable.setItems(monitoringadminObservableList);
                        } else {
                            apikeysadminObservableList = FXCollections.observableList(getIAMLists);
                            rolesTable.setItems(apikeysadminObservableList);
                        }
                    });
                    return null;
                }
            };
        }
    };

    @FXML
    void handlejfxcombobox(ActionEvent event) {
        getiam.setwg();
        spinner.setVisible(true);
        chosenRole = jfxcombobox.getSelectionModel().getSelectedItem();
        System.out.println(chosenRole);
        process.start();
//        if (globalChecker == 1){
//            ownerObservableList = FXCollections.observableList(getIAMLists);
//            rolesTable.setItems(ownerObservableList);
//        }
//        else if (globalChecker == 2) {
//            editorObservableList = FXCollections.observableList(getIAMLists);
//            rolesTable.setItems(editorObservableList);
//        } else if (globalChecker == 3) {
//            viewerObservableList = FXCollections.observableList(getIAMLists);
//            rolesTable.setItems(viewerObservableList);
//        } else if (globalChecker == 4) {
//            cloudsqlObservableList = FXCollections.observableList(getIAMLists);
//            rolesTable.setItems(cloudsqlObservableList);
//        } else if (globalChecker == 5) {
//            firebaseObservableList = FXCollections.observableList(getIAMLists);
//            rolesTable.setItems(firebaseObservableList);
//        } else if (globalChecker == 6) {
//            computeengineObservableList = FXCollections.observableList(getIAMLists);
//            rolesTable.setItems(computeengineObservableList);
//        } else if (globalChecker == 7) {
//            loggingadminObservableList = FXCollections.observableList(getIAMLists);
//            rolesTable.setItems(loggingadminObservableList);
//        } else if (globalChecker == 8) {
//            storageadminObservableList = FXCollections.observableList(getIAMLists);
//            rolesTable.setItems(storageadminObservableList);
//        } else if (globalChecker == 9) {
//            monitoringadminObservableList = FXCollections.observableList(getIAMLists);
//            rolesTable.setItems(monitoringadminObservableList);
//        } else {
//            apikeysadminObservableList = FXCollections.observableList(getIAMLists);
//            rolesTable.setItems(apikeysadminObservableList);
//        }

        process.setOnSucceeded(e -> {
            spinner.setVisible(false);
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
    void onClickEmployee(MouseEvent event) {
        secondAnchor.setVisible(false);
        employeeTable.setVisible(true);
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
