import Database.User;
import Database.User_InfoDB;
import Model.GetIAM;
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
    private TableView<String> rolesTable;

    @FXML
    private TableColumn<String,String> roleColumn;

    @FXML
    private TableColumn<String,String> userColumn;

    @FXML
    private AnchorPane secondAnchor;

    private Scene myScene;

    public static AnchorPane rootP;

    User_InfoDB userinfodb = new User_InfoDB();
    User users = new User();

    private ArrayList<User> userList;
    private ObservableList<User> userObservableList;

    IAMPermissions permissions = new IAMPermissions();
    GetIAM getiam = new GetIAM();

    private static int w;

    private String chosenRole;

    private String globalRole;
    private String globalUser;

    String listPermission;

    ArrayList<String> getiamlist = new ArrayList<>();
    ArrayList<String> cloudsqladminLIST = new ArrayList<>();
    private ObservableList<String> cloudsqlObservableList;
    ArrayList<String> ownerList = new ArrayList<>();
    private ObservableList<String> ownerObservableList;
    ArrayList<String> editorList = new ArrayList<>();
    private ObservableList<String> editorObservableList;
    ArrayList<String> viewerList = new ArrayList<>();
    private ObservableList<String> viewerObservableList;
    ArrayList<String> firebaseList = new ArrayList<>();
    private ObservableList<String> firebaseObservableList;
    ArrayList<String> computeEngineList = new ArrayList<>();
    private ObservableList<String> computeengineObservableList;
    ArrayList<String> loggingadminList = new ArrayList<>();
    private ObservableList<String> loggingadminObservableList;
    ArrayList<String> monitoringadminList = new ArrayList<>();
    private ObservableList<String> monitoringadminObservableList;
    ArrayList<String> apikeysadminList = new ArrayList<>();
    private ObservableList<String> apikeysadminObservableList;
    ArrayList<String> storageadminList = new ArrayList<>();
    private ObservableList<String> storageadminObservableList;


    ArrayList<String> list1 = new ArrayList<>();
    String allInformation;

    //    ArrayList<GetIAM> getIAMLists;
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

        jfxcombobox.getItems().addAll("Owner","Editor","Viewer","CloudSQL Admin","Firebase Rules System","Compute Engine Service","Logging Admin","Storage Admin", "Monitoring Admin", "API Keys Admin");

        userColumn.setCellValueFactory(new PropertyValueFactory<String,String>("globalUser"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<String,String>("globalRole"));


    }

    @FXML
    void handleListPermissions(MouseEvent event) {
        process.start();
        spinner.setVisible(true);

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

    Service process = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    permissions.listPermissions();

                    getiamlist = getiam.getTempPermissionList();
                    System.out.println("HIHI " + getiamlist);
                    for (int i = 0; i < getiamlist.size(); i++) {
                        System.out.println("Controller trying to print permissions from getiamlist :" + getiamlist.get(i));
                    }

                    //START OF CloudSQL List
                    for (int o = 0; o < getiamlist.size(); o++) {
                        String s = getiamlist.get(o);
                        cloudsqladminLIST.add(getiamlist.get(o));
                        System.out.println("Adding into cloudsql list : " + s);
                        if (getiamlist.get(o).contains("role: roles/cloudsql.admin")) {
                            w = getiamlist.indexOf(getiamlist.get(o));
                            System.out.println("INDEX OF ROLE LINE : " + w);
                            System.out.println("FOUND THIS");
                            break;
                        }
                    }
                    for (int p = 0; p < cloudsqladminLIST.size(); p++) {
                        System.out.println("Inside of CLOUDSQLADMINLIST : " + cloudsqladminLIST.get(p));
                    }
                    while (w != -1) {
                        getiamlist.remove(w);
                        w--;
                    }
                    for (int i = 0; i < getiamlist.size(); i++) {
                        System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
                    }
                    //END OF CLOUDSQL LIST

                    //START OF COMPUTE ENGINE LIST
                    for (int o = 0; o < getiamlist.size(); o++) {
                        String s = getiamlist.get(o);
                        computeEngineList.add(getiamlist.get(o));
                        System.out.println("Adding into compute engine list : " + s);
                        if (getiamlist.get(o).contains("role: roles/compute.serviceAgent")) {
                            w = getiamlist.indexOf(getiamlist.get(o));
                            System.out.println("INDEX OF ROLE LINE : " + w);
                            System.out.println("FOUND THIS");
                            break;
                        }
                    }
                    for (int p = 0; p < computeEngineList.size(); p++) {
                        System.out.println("Inside of COMPUTE ENGINE LIST : " + computeEngineList.get(p));
                    }
                    while (w != -1) {
                        getiamlist.remove(w);
                        w--;
                    }
                    for (int i = 0; i < getiamlist.size(); i++) {
                        System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
                    }
                    //END OF COMPUTE ENGINE LIST

                    //START OF EDITOR LIST
                    for (int o = 0; o < getiamlist.size(); o++) {
                        String s = getiamlist.get(o);
                        editorList.add(getiamlist.get(o));
                        System.out.println("Adding into editor list : " + s);
                        if (getiamlist.get(o).contains("role: roles/editor")) {
                            w = getiamlist.indexOf(getiamlist.get(o));
                            System.out.println("INDEX OF ROLE LINE : " + w);
                            System.out.println("FOUND THIS");
                            break;
                        }
                    }
                    for (int p = 0; p < editorList.size(); p++) {
                        System.out.println("Inside of EDITOR LIST : " + editorList.get(p));
                    }
                    while (w != -1) {
                        getiamlist.remove(w);
                        w--;
                    }
                    for (int i = 0; i < getiamlist.size(); i++) {
                        System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
                    }
                    //END OF EDITOR LIST

                    //START OF FIREBASE LIST
                    for (int o = 0; o < getiamlist.size(); o++) {
                        String s = getiamlist.get(o);
                        firebaseList.add(getiamlist.get(o));
                        System.out.println("Adding into firebase list : " + s);
                        if (getiamlist.get(o).contains("role: roles/firebaserules.system")) {
                            w = getiamlist.indexOf(getiamlist.get(o));
                            System.out.println("INDEX OF ROLE LINE : " + w);
                            System.out.println("FOUND THIS");
                            break;
                        }
                    }
                    for (int p = 0; p < firebaseList.size(); p++) {
                        System.out.println("Inside of FIRE BASE LIST : " + firebaseList.get(p));
                    }
                    while (w != -1) {
                        getiamlist.remove(w);
                        w--;
                    }
                    for (int i = 0; i < getiamlist.size(); i++) {
                        System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
                    }
                    //END OF FIREBASE LIST

                    //START OF LOGGING ADMIN LIST
                    for (int o = 0; o < getiamlist.size(); o++) {
                        String s = getiamlist.get(o);
                        loggingadminList.add(getiamlist.get(o));
                        System.out.println("Adding into logging admin list : " + s);
                        if (getiamlist.get(o).contains("role: roles/logging.admin")) {
                            w = getiamlist.indexOf(getiamlist.get(o));
                            System.out.println("INDEX OF ROLE LINE : " + w);
                            System.out.println("FOUND THIS");
                            break;
                        }
                    }
                    for (int p = 0; p < loggingadminList.size(); p++) {
                        System.out.println("Inside of LOGGING ADMIN LIST : " + loggingadminList.get(p));
                    }
                    while (w != -1) {
                        getiamlist.remove(w);
                        w--;
                    }
                    for (int i = 0; i < getiamlist.size(); i++) {
                        System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
                    }
                    //END OF LOGGING ADMIN LIST

                    //START OF MONITORING ADMIN LIST
                    for (int o = 0; o < getiamlist.size(); o++) {
                        String s = getiamlist.get(o);
                        monitoringadminList.add(getiamlist.get(o));
                        System.out.println("Adding into monitoring admin list : " + s);
                        if (getiamlist.get(o).contains("role: roles/monitoring.admin")) {
                            w = getiamlist.indexOf(getiamlist.get(o));
                            System.out.println("INDEX OF ROLE LINE : " + w);
                            System.out.println("FOUND THIS");
                            break;
                        }
                    }
                    for (int p = 0; p < monitoringadminList.size(); p++) {
                        System.out.println("Inside of MONITORING ADMIN LIST : " + monitoringadminList.get(p));
                    }
                    while (w != -1) {
                        getiamlist.remove(w);
                        w--;
                    }
                    for (int i = 0; i < getiamlist.size(); i++) {
                        System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
                    }
                    //END OF MONITORING ADMIN LIST

                    //START OF OWNER LIST
                    for (int o = 0; o < getiamlist.size(); o++) {
                        String s = getiamlist.get(o);
                        ownerList.add(getiamlist.get(o));
                        System.out.println("Adding into owner list : " + s);
                        if (getiamlist.get(o).contains("role: roles/owner")) {
                            w = getiamlist.indexOf(getiamlist.get(o));
                            System.out.println("INDEX OF ROLE LINE : " + w);
                            System.out.println("FOUND THIS");
                            break;
                        }
                    }
                    for (int p = 0; p < ownerList.size(); p++) {
                        System.out.println("Inside of OWNER LIST : " + ownerList.get(p));
                    }
                    while (w != -1) {
                        getiamlist.remove(w);
                        w--;
                    }
                    for (int i = 0; i < getiamlist.size(); i++) {
                        System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
                    }
                    //END OF OWNER LIST

                    //START OF API KEYS ADMIN
                    for (int o = 0; o < getiamlist.size(); o++) {
                        String s = getiamlist.get(o);
                        apikeysadminList.add(getiamlist.get(o));
                        System.out.println("Adding into api keys admin list : " + s);
                        if (getiamlist.get(o).contains("role: roles/serviceusage.apiKeysAdmin")) {
                            w = getiamlist.indexOf(getiamlist.get(o));
                            System.out.println("INDEX OF ROLE LINE : " + w);
                            System.out.println("FOUND THIS");
                            break;
                        }
                    }
                    for (int p = 0; p < apikeysadminList.size(); p++) {
                        System.out.println("Inside of API KEYS ADMIN LIST : " + apikeysadminList.get(p));
                    }
                    while (w != -1) {
                        getiamlist.remove(w);
                        w--;
                    }
                    for (int i = 0; i < getiamlist.size(); i++) {
                        System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
                    }
                    //END OF API KEYS ADMIN

                    //START OF STORAGE ADMIN LIST
                    for (int o = 0; o < getiamlist.size(); o++) {
                        String s = getiamlist.get(o);
                        storageadminList.add(getiamlist.get(o));
                        System.out.println("Adding into storage admin list : " + s);
                        if (getiamlist.get(o).contains("role: roles/storage.admin")) {
                            w = getiamlist.indexOf(getiamlist.get(o));
                            System.out.println("INDEX OF ROLE LINE : " + w);
                            System.out.println("FOUND THIS");
                            break;
                        }
                    }
                    for (int p = 0; p < storageadminList.size(); p++) {
                        System.out.println("Inside of STORAGE ADMIN LIST : " + storageadminList.get(p));
                    }
                    while (w != -1) {
                        getiamlist.remove(w);
                        w--;
                    }
                    for (int i = 0; i < getiamlist.size(); i++) {
                        System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
                    }
                    //END OF STORAGE ADMIN LIST

                    //START OF VIEWER LIST
                    for (int o = 0; o < getiamlist.size(); o++) {
                        String s = getiamlist.get(o);
                        viewerList.add(getiamlist.get(o));
                        System.out.println("Adding into viewer list : " + s);
                        if (getiamlist.get(o).contains("role: roles/viewer")) {
                            w = getiamlist.indexOf(getiamlist.get(o));
                            System.out.println("INDEX OF ROLE LINE : " + w);
                            System.out.println("FOUND THIS");
                            break;
                        }
                    }
                    for (int p = 0; p < viewerList.size(); p++) {
                        System.out.println("Inside of VIEWER LIST : " + viewerList.get(p));
                    }
                    while (w != -1) {
                        getiamlist.remove(w);
                        w--;
                    }
                    for (int i = 0; i < getiamlist.size(); i++) {
                        System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
                    }
                    //END OF VIEWER LIST

                    Platform.runLater(() -> {
                    secondAnchor.setVisible(true);
                    employeeTable.setVisible(false);
                    });
                    return null;
                }
            };
        }
    };

    @FXML
    void handlejfxcombobox(ActionEvent event) {
        chosenRole = jfxcombobox.getSelectionModel().getSelectedItem();
        System.out.println(chosenRole);

        if(chosenRole.equals("Owner")){
            //role is the same as the jfxcombobox
            //user follow arraylist -> search google on how to display in a single column
            ownerObservableList = FXCollections.observableList(ownerList);
            rolesTable.setItems(ownerObservableList);
        }
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
