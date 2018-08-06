import Database.Admin;
import Database.User;
import Database.User_InfoDB;
import Database.admin_DB;
import Model.*;
import com.jfoenix.animation.alert.JFXAlertAnimation;
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
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerEmployeePage implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label ipAddr;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXButton listPermissions;

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
    private TableColumn<User, Button> deletingEmployees;

    @FXML
    private TableColumn<User, String> activationTime;

    @FXML
    private JFXComboBox<String> jfxcombobox;

    @FXML
    private TableView<IAMExtract> rolesTable;

    @FXML
    private JFXButton createUser;

    @FXML
    private JFXButton createAdmin;

    @FXML
    private TableColumn<IAMExtract, String> roleColumn;

    @FXML
    private TableColumn<IAMExtract, String> userColumn;

    @FXML
    private TableColumn<IAMExtract, Button> revokePermissions;

    @FXML
    private AnchorPane secondAnchor;

    @FXML
    private TableView<Admin> adminTable;

    @FXML
    private TableColumn<Admin, String> entryID1;

    @FXML
    private TableColumn<Admin, String> email2;

    @FXML
    private TableColumn<Admin, String> handphone1;

    @FXML
    private TableColumn<Admin, Button> deletingAdmins;

    @FXML
    private JFXComboBox<String> employeeAdminCombobox;

    @FXML
    private JFXButton homeButton;

    private Scene myScene;

    public static AnchorPane rootP;

    private String myIPAddress;
    private boolean ipChecker;

    String errorMessage = "";
    String successfulMessage = "";
    String doubleConfirm = "";
    int CHECKING;
    int checker2;
    String CreateUser;
    String CreateRole;
    String CreateHandphone;

    String CreateAdmin;
    String CreateAdminRole;

    static String email1;
    static String handphoneNUMBER;
    static String emailAdmin;
    static String emailPermission;
    static String hpAdmin;

    static String rolePermission;

    User_InfoDB userinfodb = new User_InfoDB();
    User users = new User();

    IAMExtract iamExtracts = new IAMExtract();

    private ArrayList<User> userList;
    private ObservableList<User> userObservableList;

    admin_DB adminDB = new admin_DB();
    Admin admins = new Admin();

    private ArrayList<Admin> adminList;
    private ObservableList<Admin> adminObservableList;

    IAMPermissions permissions;
    GetIAM getiam = new GetIAM();
    IAMExtract iamExtract;

    static int globalChecker;

    private String chosenRole;

    AWSSMS sendSMS = new AWSSMS();

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


    ArrayList<String> stringArrayList = new ArrayList<>();
    String stringEmail;

    public User user(User user) {
        users = user;
        return users;
    }

    public Admin admin(Admin admin) {
        admins = admin;
        return admins;
    }

    public IAMExtract iamExtract(IAMExtract iamExtract) {
        iamExtracts = iamExtract;
        return iamExtracts;
    }

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

        Path path = FileSystems.getDefault().getPath("src\\View\\addingpicture.png");
        File file = new File(path.toUri());
        Image imageForFile;
        try {
            imageForFile = new Image(file.toURI().toURL().toExternalForm());
            ImageView iv1 = new ImageView(imageForFile);
            iv1.setFitHeight(20);
            iv1.setFitWidth(20);
            createUser.setGraphic(iv1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Path path1 = FileSystems.getDefault().getPath("src\\View\\addingpicture.png");
        File file1 = new File(path.toUri());
        Image imageForFile1;
        try {
            imageForFile1 = new Image(file.toURI().toURL().toExternalForm());
            ImageView iv2 = new ImageView(imageForFile1);
            iv2.setFitHeight(20);
            iv2.setFitWidth(20);
            createAdmin.setGraphic(iv2);
        } catch (MalformedURLException e) {
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

        createUser.setVisible(true);
        createAdmin.setVisible(false);
        spinner.setVisible(true);
        employeeAdminCombobox.setDisable(true);
        listPermissions.setDisable(true);

        initializeProcess.start();

        initializeProcess.setOnSucceeded(e -> {
            spinner.setVisible(false);
            employeeAdminCombobox.setDisable(false);
            listPermissions.setDisable(false);
            initializeProcess.reset();
        });
        initializeProcess.setOnCancelled(e -> {
            initializeProcess.reset();
        });
        initializeProcess.setOnFailed(e -> {
            initializeProcess.reset();
        });
    }

    @FXML
    void onClickHomeButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("AdminHome.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();

        ControllerAdminHome controller = loader.<ControllerAdminHome>getController();
        //controller.passData(admin);

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();
    }

    Service initializeProcess = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {

//                    Image addPerson = new Image(getClass().getResourceAsStream("View/addPerson.svg"));
//                    createUser.setGraphic(new ImageView(addPerson));

                    employeeAdminCombobox.getItems().addAll("Employee Database", "Administrator Database");


                    entryID.setCellValueFactory(new PropertyValueFactory<User, String>("entryID"));
                    email.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
                    handphone.setCellValueFactory(new PropertyValueFactory<User, String>("phoneNo"));
                    status.setCellValueFactory(new PropertyValueFactory<User, String>("status"));
                    hashpassword.setCellValueFactory(new PropertyValueFactory<User, String>("hashPassword"));
                    activationTime.setCellValueFactory(new PropertyValueFactory<User, String>("ActivationTime"));

                    entryID1.setCellValueFactory(new PropertyValueFactory<Admin, String>("entryID"));
                    email2.setCellValueFactory(new PropertyValueFactory<Admin, String>("email"));
                    handphone1.setCellValueFactory(new PropertyValueFactory<Admin, String>("phoneNo"));

//                    try {
                    userList = userinfodb.getUserList();
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }


                    deletingEmployees.setCellFactory(ActionButtonTableCell.<User>forTableColumn("Revoke", (User users) -> {
                        user(users);
                        email1 = users.getEmail();
                        System.out.println("THIS IS THE EMAIL!!" + email1);

                        handphoneNUMBER = users.getPhoneNo();
                        doubleConfirm = "This selected user \"" + users.getEmail() + "\" will be removed from the Employees Database. Are you sure to delete it?";
                        doubleConfirmation(anchorPane.getScene(), doubleConfirm, "No", "Yes");
                        CHECKING = checker2;
                        System.out.println("CHECKER NOW IS " + CHECKING);

                        return users;
                    }));

                    deletingAdmins.setCellFactory(ActionButtonTableCell.<Admin>forTableColumn("Revoke", (Admin admins) -> {
                        admin(admins);
                        emailAdmin = admins.getEmail();
                        System.out.println("THIS IS THE EMAIL!!" + emailAdmin);

                        hpAdmin = admins.getPhoneNo();
                        System.out.println("THIS IS THE HP NUMBER : " + hpAdmin);

                        doubleConfirm = "This selected user \"" + admins.getEmail() + "\" will be removed from the Admin Database. Are you sure to delete it?";
                        doubleConfirmation1(anchorPane.getScene(), doubleConfirm, "No", "Yes");
                        CHECKING = checker2;
                        System.out.println("CHECKER NOW IS " + CHECKING);

                        return admins;
                    }));

                    getIAMLists = IAMExtract.iamobjlist;

                    Platform.runLater(() -> {
                        userObservableList = FXCollections.observableList(userList);
                        employeeTable.setItems(userObservableList);

                        jfxcombobox.getItems().addAll("Owner", "Editor", "Viewer", "CloudSQL Admin", "Firebase Rules System", "Compute Engine Service", "Logging Admin", "Storage Admin", "Monitoring Admin", "API Keys Admin");

                        userColumn.setCellValueFactory(new PropertyValueFactory<IAMExtract, String>("globalUser"));
                        roleColumn.setCellValueFactory(new PropertyValueFactory<IAMExtract, String>("globalRole"));
                    });
                    return null;
                }

            };
        }
    };


    @FXML
    void handleEmployeeAdminComboBox(ActionEvent event) {
        String option = employeeAdminCombobox.getSelectionModel().getSelectedItem();
        if (option.equals("Employee Database")) {
            createUser.setVisible(true);
            createAdmin.setVisible(false);
            secondAnchor.setVisible(false);
            adminTable.setVisible(false);
            employeeTable.setVisible(true);
            spinner.setVisible(true);

            process1.start();
            employeeAdminCombobox.setDisable(true);

            process1.setOnSucceeded(e -> {
                spinner.setVisible(false);
                employeeAdminCombobox.setDisable(false);
                process1.reset();
            });
            process1.setOnCancelled(e -> {
                process1.reset();
            });
            process1.setOnFailed(e -> {
                process1.reset();
            });
        } else if (option.equals("Administrator Database")) {
            createUser.setVisible(false);
            createAdmin.setVisible(true);
            spinner.setVisible(true);
            secondAnchor.setVisible(false);
            employeeTable.setVisible(false);
            adminTable.setVisible(true);
            employeeAdminCombobox.setDisable(true);

            process1a.start();

            process1a.setOnSucceeded(e -> {
                employeeAdminCombobox.setDisable(false);
                spinner.setVisible(false);
                process1a.reset();
            });
            process1a.setOnCancelled(e -> {
                process1a.reset();
            });
            process1a.setOnFailed(e -> {
                process1a.reset();
            });
        }

    }

    //    Service processCOMBOXBOX = new Service() {
//        @Override
//        protected Task createTask() {
//            return new Task() {
//                @Override
//                protected Void call() throws Exception {
//
//                    employeeAdminCombobox.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
//                        System.out.println("DETECTED THE MOUSE LIAO!~!!");
//
////                        if (e.isPrimaryButtonDown()) {
////                            onEdit();
////                        }
//                    });
//                    Platform.runLater(() -> {
//
//                    });
//                    return null;
//                }
//
//            };
//        }
//    };
    @FXML
    public void onClickItem(MouseEvent event) {
        try {
            if (event.getClickCount() == 2) //Checking double click
            {
                spinner.setVisible(false);
                expandedDetails1(anchorPane.getScene(), "Close");
            }
        } catch (com.google.cloud.logging.LoggingException e1) {
            e1.printStackTrace();
        } catch (io.grpc.StatusRuntimeException e2) {
            e2.printStackTrace();
        }
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

        String title = "Full Employee Information";

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
        grid.getColumnConstraints().addAll(col1, col2);

        String email1 = employeeTable.getSelectionModel().getSelectedItem().getEmail();
        String entryid1 = employeeTable.getSelectionModel().getSelectedItem().getEntryID();
        String handphone1 = employeeTable.getSelectionModel().getSelectedItem().getPhoneNo();
        String status1 = employeeTable.getSelectionModel().getSelectedItem().getStatus();
        String hashpwd1 = employeeTable.getSelectionModel().getSelectedItem().getHashPassword();
        String activationTime = employeeTable.getSelectionModel().getSelectedItem().getActivationTime();

        String string1 = "Entry ID :";
        Label label1 = new Label();
        label1.setTextFill(Color.rgb(1, 0, 199));
        label1.setText(string1);

        String string2 = "Email :";
        Label label2 = new Label();
        label2.setTextFill(Color.rgb(1, 0, 199));
        label2.setText(string2);

        String string3 = "Handphone  :";
        Label label3 = new Label();
        label3.setTextFill(Color.rgb(1, 0, 199));
        label3.setText(string3);

        String string4 = "Status :";
        Label label4 = new Label();
        label4.setTextFill(Color.rgb(1, 0, 199));
        label4.setText(string4);

        String string5 = "Hash Password :";
        Label label5 = new Label();
        label5.setTextFill(Color.rgb(1, 0, 199));
        label5.setText(string5);

        String string6 = "Activation Time :";
        Label label6 = new Label();
        label6.setTextFill(Color.rgb(1, 0, 199));
        label6.setText(string6);

        grid.add(label1, 0, 0);
        grid.add(label2, 0, 1);
        grid.add(label3, 0, 2);
        grid.add(label4, 0, 3);
        grid.add(label5, 0, 4);
        grid.add(label6, 0, 5);


        grid.add(new Label(entryid1), 1, 0);
        grid.add(new Label(email1), 1, 1);
        grid.add(new Label(handphone1), 1, 2);
        grid.add(new Label(status1), 1, 3);
        grid.add(new Label(hashpwd1), 1, 4);
        grid.add(new Label(activationTime), 1, 5);

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

    private void expandedDetails1(Scene scene, String buttonContent) {
        myScene = scene;
        Stage stage = (Stage) (myScene).getWindow();

        String title = "Full Administrator Information";

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
        grid.getColumnConstraints().addAll(col1, col2);

        String email2 = adminTable.getSelectionModel().getSelectedItem().getEmail();
        String entryid2 = adminTable.getSelectionModel().getSelectedItem().getEntryID();
        String handphone2 = adminTable.getSelectionModel().getSelectedItem().getPhoneNo();
//        String status2 = adminTable.getSelectionModel().getSelectedItem().getStatus();
//        String hashpwd2 = adminTable.getSelectionModel().getSelectedItem().getHashPassword();

        String string1 = "Entry ID :";
        Label label1 = new Label();
        label1.setTextFill(Color.rgb(1, 0, 199));
        label1.setText(string1);

        String string2 = "Email :";
        Label label2 = new Label();
        label2.setTextFill(Color.rgb(1, 0, 199));
        label2.setText(string2);

        String string3 = "Handphone  :";
        Label label3 = new Label();
        label3.setTextFill(Color.rgb(1, 0, 199));
        label3.setText(string3);

//        String string4 = "Status :";
//        Label label4 = new Label();
//        label4.setTextFill(Color.rgb(1, 0, 199));
//        label4.setText(string4);
//
//        String string5 = "Hash Password :";
//        Label label5 = new Label();
//        label5.setTextFill(Color.rgb(1, 0, 199));
//        label5.setText(string5);


        grid.add(label1, 0, 0);
        grid.add(label2, 0, 1);
        grid.add(label3, 0, 2);
//        grid.add(label4,0,3);
//        grid.add(label5,0,4);

        grid.add(new Label(entryid2), 1, 0);
        grid.add(new Label(email2), 1, 1);
        grid.add(new Label(handphone2), 1, 2);
//        grid.add(new Label(status1), 1, 3);
//        grid.add(new Label(hashpwd1), 1, 4);

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
    void handleListPermissions(MouseEvent event) {
        secondAnchor.setVisible(true);
        employeeTable.setVisible(false);
        adminTable.setVisible(false);
    }

    Service process = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    permissions = new IAMPermissions();
                    iamExtract = new IAMExtract();
                    if (chosenRole.equals("Owner")) {
                        globalChecker = 1;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        System.out.println(stringArrayList);
                        for (int u = 0; u < stringArrayList.size(); u++) {
                            stringEmail = stringArrayList.get(u);
                            System.out.println("======================" + stringEmail);
                        }

                    } else if (chosenRole.equals("Editor")) {
                        globalChecker = 2;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        System.out.println(stringArrayList);
                        for (int u = 0; u < stringArrayList.size(); u++) {
                            stringEmail = stringArrayList.get(u);
                            System.out.println("======================" + stringEmail);
                        }
                    } else if (chosenRole.equals("Viewer")) {
                        globalChecker = 3;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        for (int u = 0; u < stringArrayList.size(); u++) {
                            stringEmail = stringArrayList.get(u);
                            System.out.println("======================" + stringEmail);
                        }
                    } else if (chosenRole.equals("CloudSQL Admin")) {
                        globalChecker = 4;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        for (int u = 0; u < stringArrayList.size(); u++) {
                            stringEmail = stringArrayList.get(u);
                            System.out.println("======================" + stringEmail);
                        }
                    } else if (chosenRole.equals("Firebase Rules System")) {
                        globalChecker = 5;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        for (int u = 0; u < stringArrayList.size(); u++) {
                            stringEmail = stringArrayList.get(u);
                            System.out.println("======================" + stringEmail);
                        }
                    } else if (chosenRole.equals("Compute Engine Service")) {
                        globalChecker = 6;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        for (int u = 0; u < stringArrayList.size(); u++) {
                            stringEmail = stringArrayList.get(u);
                            System.out.println("======================" + stringEmail);
                        }
                    } else if (chosenRole.equals("Logging Admin")) {
                        globalChecker = 7;
                        stringArrayList = IAMExtract.stringEmailList;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        for (int u = 0; u < stringArrayList.size(); u++) {
                            stringEmail = stringArrayList.get(u);
                            System.out.println("======================" + stringEmail);
                        }
                    } else if (chosenRole.equals("Storage Admin")) {
                        globalChecker = 8;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        for (int u = 0; u < stringArrayList.size(); u++) {
                            stringEmail = stringArrayList.get(u);
                            System.out.println("======================" + stringEmail);
                        }
                    } else if (chosenRole.equals("Monitoring Admin"))   {
                        globalChecker = 9;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        for (int u = 0; u < stringArrayList.size(); u++) {
                            stringEmail = stringArrayList.get(u);
                            System.out.println("======================" + stringEmail);
                        }
                    } else {
                        globalChecker = 10;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        for (int u = 0; u < stringArrayList.size(); u++) {
                            stringEmail = stringArrayList.get(u);
                            System.out.println("======================" + stringEmail);
                        }
                    }

                    Platform.runLater(() -> {
                        revokePermissions.setCellFactory(new Callback<TableColumn<IAMExtract, Button>, TableCell<IAMExtract, Button>>() {
                            String oneEmail;
                            Boolean checkingde = null;

                            @Override
                            public TableCell<IAMExtract, Button> call(TableColumn<IAMExtract, Button> param) {
                                return new TableCell<IAMExtract, Button>() {
//                                    JFXButton btn = new JFXButton();

                                    @Override
                                    public void updateItem(Button item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (empty) {
                                            System.out.println("EMPTY");
                                            setItem(null);
                                        } else {
//                                            btn.setDisable(true);
                                            System.out.println("STRING ARRAYLIST SIZE IS = " + stringArrayList.size());
                                            for (int r = 0; r < stringArrayList.size(); r++) {
                                                oneEmail = stringArrayList.get(r);
                                                if (oneEmail.contains("user:")) {
                                                    System.out.println("IT IS NOT A USER! GOOGLE SERVICE ACCOUNT! DISABLE BUTTON NOW!!!");
                                                    checkingde = true;
//                                                    btn.setDisable(false);
//                                                    btn.setOnAction(event -> {
//                                                        int selectdIndex = getTableRow().getIndex();
//                                                        System.out.println(selectdIndex);
//                                                        IAMExtract iamEX = rolesTable.getSelectionModel().getSelectedItem();
//                                                        emailPermission = iamExtracts.getGlobalUser();
//                                                        emailPermission = emailPermission.substring(9, emailPermission.length());
//                                                        System.out.println("THIS IS THE EMAIL!!" + emailPermission);
//
//                                                        rolePermission = iamExtracts.getGlobalRole();
//                                                        System.out.println("THIS IS THE ROLE: " + rolePermission);
//
//                                                        doubleConfirm = "This selected user \"" + emailPermission + "\" will be revoked of this role " + rolePermission + ". Are you sure to delete it?";
//                                                        doubleConfirmation2(anchorPane.getScene(), doubleConfirm, "No", "Yes");
//                                                        CHECKING = checker2;
//                                                        System.out.println("CHECKER NOW IS " + CHECKING);
//                                                    });
//
//                                                    Path path = FileSystems.getDefault().getPath("src\\View\\more.png");
//                                                    File file = new File(path.toUri());
//                                                    Image imageForFile;
//                                                    try {
//                                                        imageForFile = new Image(file.toURI().toURL().toExternalForm());
//                                                        ImageView iv1 = new ImageView(imageForFile);
//                                                        iv1.setFitHeight(28);
//                                                        iv1.setFitWidth(40);
//                                                        btn.setGraphic(iv1);
//                                                    } catch (MalformedURLException e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                    btn.setBorder(new Border(new BorderStroke(Color.rgb(41,221,244),
//                                                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
//
//                                                    setGraphic(btn);
//                                                    setText(null);
                                                    revokePermissions.setCellFactory(ActionButtonTableCell.<IAMExtract>forTableColumn("Revoke", checkingde, (IAMExtract iamExtracts) -> {
                                                        iamExtract(iamExtracts);
                                                        emailPermission = iamExtracts.getGlobalUser();
                                                        emailPermission = emailPermission.substring(9, emailPermission.length());
                                                        System.out.println("THIS IS THE EMAIL!!" + emailPermission);

                                                        rolePermission = iamExtracts.getGlobalRole();
                                                        System.out.println("THIS IS THE ROLE: " + rolePermission);

                                                        doubleConfirm = "This selected user \"" + emailPermission + "\" will be revoked of this role " + rolePermission + ". Are you sure to delete it?";
                                                        doubleConfirmation2(anchorPane.getScene(), doubleConfirm, "No", "Yes");
                                                        CHECKING = checker2;
                                                        System.out.println("CHECKER NOW IS " + CHECKING);

                                                        return iamExtracts;
                                                    }));
                                                } else {
                                                    checkingde = false;
                                                    revokePermissions.setCellFactory(ActionButtonTableCell.<IAMExtract>forTableColumn("Revoke", checkingde, (IAMExtract iamExtracts) -> {
                                                        iamExtract(iamExtracts);
                                                        emailPermission = iamExtracts.getGlobalUser();
                                                        emailPermission = emailPermission.substring(9, emailPermission.length());
                                                        System.out.println("THIS IS THE EMAIL!!" + emailPermission);

                                                        rolePermission = iamExtracts.getGlobalRole();
                                                        System.out.println("THIS IS THE ROLE: " + rolePermission);

                                                        doubleConfirm = "This selected user \"" + emailPermission + "\" will be revoked of this role " + rolePermission + ". Are you sure to delete it?";
                                                        doubleConfirmation2(anchorPane.getScene(), doubleConfirm, "No", "Yes");
                                                        CHECKING = checker2;
                                                        System.out.println("CHECKER NOW IS " + CHECKING);

                                                        return iamExtracts;
                                                    }));
                                                }
                                                setItem(item);
                                            }
                                        }

                                    }
                                };
                            }
                        });
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
        rolesTable.getItems().clear();

        process.start();
        jfxcombobox.setDisable(true);

        process.setOnSucceeded(e -> {
            jfxcombobox.setDisable(false);
            spinner.setVisible(false);

            if (globalChecker == 1) {
                ownerObservableList = FXCollections.observableList(getIAMLists);
                rolesTable.setItems(ownerObservableList);
            } else if (globalChecker == 2) {
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

            process.reset();
        });
        process.setOnCancelled(e -> {
            process.reset();
        });
        process.setOnFailed(e -> {
            process.reset();
        });
    }

    Service process1 = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
//                    try {
                    userList = userinfodb.getUserList();
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }

                    Platform.runLater(() -> {
                        userObservableList = FXCollections.observableList(userList);
                        employeeTable.setItems(userObservableList);
                    });
                    return null;
                }

            };
        }
    };

    Service process1a = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
//                    try {
                        adminList = adminDB.getAdminList();
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }

                    Platform.runLater(() -> {
                        adminObservableList = FXCollections.observableList(adminList);
                        adminTable.setItems(adminObservableList);
                    });
                    return null;
                }

            };
        }
    };

    Service process2 = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
//                    try {
                    userList = userinfodb.getUserList();
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
                    Platform.runLater(() -> {
                        JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                        snackbar.getStylesheets().add("Style.css");
                        snackbar.show("Updating The Database", 3000);
                    });
                    return null;
                }

            };
        }
    };

    Service process2a = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
//                    try {
                        adminList = adminDB.getAdminList();
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
                    Platform.runLater(() -> {
                        JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                        snackbar.getStylesheets().add("Style.css");
                        snackbar.show("Updating The Database", 3000);
                    });
                    return null;
                }

            };
        }
    };

    public void newProcess() {
        spinner.setVisible(true);
        process2.start();
        employeeAdminCombobox.setDisable(true);


        process2.setOnSucceeded(e -> {
            spinner.setVisible(false);
            employeeAdminCombobox.setDisable(false);
            userObservableList = FXCollections.observableList(userList);
            employeeTable.setItems(userObservableList);
            process2.reset();
        });
        process2.setOnCancelled(e -> {
            process2.reset();
        });
        process2.setOnFailed(e -> {
            process2.reset();
        });
    }

    public void newProcessA() {
        spinner.setVisible(true);
        process2a.start();
        employeeAdminCombobox.setDisable(true);

        process2a.setOnSucceeded(e -> {
            spinner.setVisible(false);
            employeeAdminCombobox.setDisable(false);
            adminObservableList = FXCollections.observableList(adminList);
            adminTable.setItems(adminObservableList);
            process2a.reset();
        });
        process2a.setOnCancelled(e -> {
            process2a.reset();
        });
        process2a.setOnFailed(e -> {
            process2a.reset();
        });
    }

    @FXML
    void onClickCreateAdmin(MouseEvent event) {
        creatingAdmins(anchorPane.getScene(), "No", "Yes");
        CHECKING = checker2;
        System.out.println("CHECKER NOW IS " + CHECKING);
    }

    @FXML
    void onClickCreate(MouseEvent event) {
        creatingUSERS(anchorPane.getScene(), "No", "Yes");
        CHECKING = checker2;
        System.out.println("CHECKER NOW IS " + CHECKING);
    }

    private void creatingUSERS(Scene scene, String buttonContent, String buttonContent2) {
        try {
            myIPAddress = IPAddressPolicy.getIp();
            System.out.println(myIPAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        permissions = new IAMPermissions();
        myScene = scene;
        Stage stage = (Stage) (myScene).getWindow();

        String message = "Please input the follow required data to create the user.";
        String title = "Create User" + "\n" + message;
        JFXButton no = new JFXButton(buttonContent);
        no.setButtonType(JFXButton.ButtonType.RAISED);
        no.setStyle("-fx-background-color: #00bfff;");

        JFXButton yes = new JFXButton(buttonContent2);
        yes.setButtonType(JFXButton.ButtonType.RAISED);
        yes.setStyle("-fx-background-color: #ff2828");

        String message1 = "Enter email: ";

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setPrefSize(546, 299);
        layout.setHeading(new Label(title));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 100, 10, 0));

        JFXTextField inputUser = new JFXTextField();
        inputUser.setPrefWidth(250);
        inputUser.setPromptText("Type Email Here");

        grid.add(new Label(message1), 0, 0);
        grid.add(inputUser, 1, 0);
        layout.setBody(grid);

        String chooserole = "Choose Role: ";
        JFXComboBox<String> creatingRoles = new JFXComboBox<>();
        creatingRoles.getItems().addAll("Owner", "Editor", "Viewer", "CloudSQL Admin", "Firebase Rules System", "Compute Engine Service", "Logging Admin", "Storage Admin", "Monitoring Admin", "API Keys Admin");
        grid.add(new Label(chooserole), 0, 1);
        grid.add(creatingRoles, 1, 1);

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
            CreateUser = inputUser.getText();
            System.out.println("INPUT NAME IS : " + CreateUser);
            CreateRole = creatingRoles.getSelectionModel().getSelectedItem();


            ipChecker = IPAddressPolicy.isValidRange(myIPAddress);
            System.out.println("IS IT WITHIN IP RANGE? = " + ipChecker);
            if (ipChecker == false) {
                //Display not within ip range error message
                errorMessage = "You are not within the company's premises to perform this function.";
                errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            } else {
                if (CreateUser.contains("@gmail.com") && !creatingRoles.getSelectionModel().isEmpty()) {
                        userinfodb.createUser(CreateUser);

                        newProcess();

                        if (CreateRole.equals("Owner")) {
                            CreateRole = "roles/owner";
                            System.out.println("CHOOSE " + CreateRole);
                            permissions.addPermissions(CreateUser, CreateRole);
                        } else if (CreateRole.equals("Editor")) {
                            CreateRole = "roles/editor";
                            System.out.println("CHOOSE " + CreateRole);
                            permissions.addPermissions(CreateUser, CreateRole);
                        } else if (CreateRole.equals("Viewer")) {
                            CreateRole = "roles/viewer";
                            System.out.println("CHOOSE " + CreateRole);
                            permissions.addPermissions(CreateUser, CreateRole);
                        } else if (CreateRole.equals("CloudSQL Admin")) {
                            CreateRole = "roles/cloudsql.admin";
                            System.out.println("CHOOSE " + CreateRole);
                            permissions.addPermissions(CreateUser, CreateRole);
                        } else if (CreateRole.equals("Firebase Rules System")) {
                            CreateRole = "roles/firebaserules.system";
                            System.out.println("CHOOSE " + CreateRole);
                            permissions.addPermissions(CreateUser, CreateRole);
                        } else if (CreateRole.equals("Compute Engine Service")) {
                            CreateRole = "roles/compute.serviceAgent";
                            System.out.println("CHOOSE " + CreateRole);
                            permissions.addPermissions(CreateUser, CreateRole);
                        } else if (CreateRole.equals("Logging Admin")) {
                            CreateRole = "roles/logging.admin";
                            System.out.println("CHOOSE " + CreateRole);
                            permissions.addPermissions(CreateUser, CreateRole);
                        } else if (CreateRole.equals("Storage Admin")) {
                            CreateRole = "roles/storage.admin";
                            System.out.println("CHOOSE " + CreateRole);
                            permissions.addPermissions(CreateUser, CreateRole);
                        } else if (CreateRole.equals("Monitoring Admin")) {
                            CreateRole = "roles/monitoring.admin";
                            System.out.println("CHOOSE " + CreateRole);
                            permissions.addPermissions(CreateUser, CreateRole);
                        } else if (CreateRole.equals("API Keys Admin")) {
                            CreateRole = "roles/serviceusage.apiKeysAdmin";
                            System.out.println("CHOOSE " + CreateRole);
                            permissions.addPermissions(CreateUser, CreateRole);
                        }

                        successfulMessage = "This user " + CreateUser + " was created and added into the cloud.";
                        successfulMessage(anchorPane.getScene(), successfulMessage, "Close");

                        alert.hideWithAnimation();
                } else if (creatingRoles.getSelectionModel().isEmpty()) {
                    System.out.println("NO ROLE CHOSEN! PLEASE CHOOSE A ROLE");
                    errorMessage = "Please try again. Choose a role for the specified user.";
                    errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
                } else {
                    System.out.println("NOT ACCEPTED AS NOT A VALID EMAIL");
                    errorMessage = "Please try again. Email was not accepted.";
                    errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
                }

            }
        });
        no.setOnAction(__addEvent -> {
            checker2 = 0;
            System.out.println("NO IS PRESSED, CHECKER2 is " + checker2);

            alert.hideWithAnimation();
        });

        alert.show();
    }

    private void creatingAdmins(Scene scene, String buttonContent, String buttonContent2) {
        try {
            myIPAddress = IPAddressPolicy.getIp();
            System.out.println(myIPAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        permissions = new IAMPermissions();
        myScene = scene;
        Stage stage = (Stage) (myScene).getWindow();

        String message = "Please input the follow required data to create the administrator.";
        String title = "Create Administrator" + "\n" + message;
        JFXButton no = new JFXButton(buttonContent);
        no.setButtonType(JFXButton.ButtonType.RAISED);
        no.setStyle("-fx-background-color: #00bfff;");

        JFXButton yes = new JFXButton(buttonContent2);
        yes.setButtonType(JFXButton.ButtonType.RAISED);
        yes.setStyle("-fx-background-color: #ff2828");

        String message1 = "Enter email: ";

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setPrefSize(546, 299);
        layout.setHeading(new Label(title));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 100, 10, 0));

        JFXTextField inputAdmin = new JFXTextField();
        inputAdmin.setPrefWidth(250);
        inputAdmin.setPromptText("Type Email Here");

        grid.add(new Label(message1), 0, 0);
        grid.add(inputAdmin, 1, 0);

        String message2 = "Enter Handphone No: ";
        JFXTextField inputAdminHP = new JFXTextField();
        inputAdminHP.setPrefWidth(250);
        inputAdminHP.setPromptText("Type Handphone Number Here");

        grid.add(new Label(message2), 0, 1);
        grid.add(inputAdminHP, 1, 1);


        String chooserole = "Choose Role: ";
        JFXComboBox<String> creatingAdminRoles = new JFXComboBox<>();
        creatingAdminRoles.getItems().addAll("Owner", "Editor", "CloudSQL Admin", "Firebase Rules System", "Compute Engine Service", "Logging Admin", "Storage Admin", "Monitoring Admin", "API Keys Admin");
        grid.add(new Label(chooserole), 0, 2);
        grid.add(creatingAdminRoles, 1, 2);


        layout.setBody(grid);
        layout.setActions(no, yes);

        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);

        yes.setOnAction(__addEvent -> {
            String numbers = "[0-9]+";
            checker2 = 1;
            CHECKING = -1;
            System.out.println("YES IS PRESSED, CHECKER2 is " + checker2);
            CHECKING = checker2;
            CreateAdmin = inputAdmin.getText();
            System.out.println("INPUT NAME IS : " + CreateAdmin);
            CreateHandphone = inputAdminHP.getText();
            System.out.println("HANDPHONE NUMBER IS : " + CreateHandphone);

            CreateAdminRole = creatingAdminRoles.getSelectionModel().getSelectedItem();

            ipChecker = IPAddressPolicy.isValidRange(myIPAddress);
            System.out.println("IS IT WITHIN IP RANGE? = " + ipChecker);
            if (ipChecker == false) {
                //Display not within ip range error message
                errorMessage = "You are not within the company's premises to perform this function.";
                errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            } else {
                if (CreateAdmin.contains("@gmail.com") && !creatingAdminRoles.getSelectionModel().isEmpty()) {
                    if (CreateHandphone.matches(numbers) && CreateHandphone.length() == 8 && (CreateHandphone.startsWith("8") || CreateHandphone.startsWith("9"))) {
//                        try {
                            adminDB.createAdmin(CreateAdmin, CreateHandphone);
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
                        newProcessA();

                        if (CreateAdminRole.equals("Owner")) {
                            CreateAdminRole = "roles/owner";
                            System.out.println("CHOOSE " + CreateAdminRole);
                            permissions.addPermissions(CreateAdmin, CreateAdminRole);
                        } else if (CreateAdminRole.equals("Editor")) {
                            CreateAdminRole = "roles/editor";
                            System.out.println("CHOOSE " + CreateAdminRole);
                            permissions.addPermissions(CreateAdmin, CreateAdminRole);
                        } else if (CreateRole.equals("CloudSQL Admin")) {
                            CreateRole = "roles/cloudsql.admin";
                            System.out.println("CHOOSE " + CreateAdminRole);
                            permissions.addPermissions(CreateAdmin, CreateAdminRole);
                        } else if (CreateRole.equals("Firebase Rules System")) {
                            CreateRole = "roles/firebaserules.system";
                            System.out.println("CHOOSE " + CreateAdminRole);
                            permissions.addPermissions(CreateAdmin, CreateAdminRole);
                        } else if (CreateRole.equals("Compute Engine Service")) {
                            CreateRole = "roles/compute.serviceAgent";
                            System.out.println("CHOOSE " + CreateAdminRole);
                            permissions.addPermissions(CreateAdmin, CreateAdminRole);
                        } else if (CreateRole.equals("Logging Admin")) {
                            CreateRole = "roles/logging.admin";
                            System.out.println("CHOOSE " + CreateAdminRole);
                            permissions.addPermissions(CreateAdmin, CreateAdminRole);
                        } else if (CreateRole.equals("Storage Admin")) {
                            CreateRole = "roles/storage.admin";
                            System.out.println("CHOOSE " + CreateAdminRole);
                            permissions.addPermissions(CreateAdmin, CreateAdminRole);
                        } else if (CreateRole.equals("Monitoring Admin")) {
                            CreateRole = "roles/monitoring.admin";
                            System.out.println("CHOOSE " + CreateAdminRole);
                            permissions.addPermissions(CreateAdmin, CreateAdminRole);
                        } else if (CreateRole.equals("API Keys Admin")) {
                            CreateRole = "roles/serviceusage.apiKeysAdmin";
                            System.out.println("CHOOSE " + CreateAdminRole);
                            permissions.addPermissions(CreateAdmin, CreateAdminRole);
                        }

                        successfulMessage = "This Administrator " + CreateAdmin + " was created and added into the cloud as Administrator.";
                        successfulMessage(anchorPane.getScene(), successfulMessage, "Close");
                        alert.hideWithAnimation();
                    } else {
                        System.out.println("Invalid Handphone Number! Please choose a valid hp number.");
                        errorMessage = "Please try again. Handphone Number was invalid.";
                        errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
                    }
                } else if (creatingAdminRoles.getSelectionModel().isEmpty()) {
                    System.out.println("NO ROLE CHOSEN! PLEASE CHOOSE A ROLE");
                    errorMessage = "Please try again. Choose a role for the specified user.";
                    errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
                } else {
                    System.out.println("NOT ACCEPTED AS NOT A VALID EMAIL");
                    errorMessage = "Please try again. Email was not accepted.";
                    errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
                }
            }
        });
        no.setOnAction(__addEvent -> {
            checker2 = 0;
            System.out.println("NO IS PRESSED, CHECKER2 is " + checker2);

            alert.hideWithAnimation();
        });

        alert.show();
    }

    private void doubleConfirmation2(Scene scene, String doubleconfirm, String buttonContent, String buttonContent2) {
        checker2 = -1;
        try {
            myIPAddress = IPAddressPolicy.getIp();
            System.out.println(myIPAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            CHECKING = checker2;
            ipChecker = IPAddressPolicy.isValidRange(myIPAddress);
            System.out.println("IS IT WITHIN IP RANGE? = " + ipChecker);
            if (ipChecker == false) {
                //Display not within ip range error message
                errorMessage = "You are not within the company's premises to perform this function.";
                errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            } else {
                permissions.revokePermissions(emailPermission, rolePermission);
                rolesTable.getItems().remove(iamExtracts);

                successfulMessage = "This user " + emailPermission + " was successfully revoked of the role, " + rolePermission + ".";
                successfulMessage(anchorPane.getScene(), successfulMessage, "Close");
                alert.hideWithAnimation();
            }
        });
        no.setOnAction(__addEvent -> {
            checker2 = 0;
            System.out.println("NO IS PRESSED, CHECKER2 is " + checker2);
            errorMessage = "This user " + emailPermission + " was not successfully revoked of the role, " + rolePermission + ".";
            errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            alert.hideWithAnimation();
        });
        alert.show();
    }

    private void doubleConfirmation1(Scene scene, String doubleconfirm, String buttonContent, String buttonContent2) {
        checker2 = -1;
        try {
            myIPAddress = IPAddressPolicy.getIp();
            System.out.println(myIPAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            CHECKING = checker2;
            ipChecker = IPAddressPolicy.isValidRange(myIPAddress);
            System.out.println("IS IT WITHIN IP RANGE? = " + ipChecker);
            if (ipChecker == false) {
                //Display not within ip range error message
                errorMessage = "You are not within the company's premises to perform this function.";
                errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            } else {
//                try {
                    adminDB.deleteAdmin(emailAdmin, hpAdmin);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
                String sendingMessage = "You, " + emailAdmin + " have been removed from FireE's Cloud Administrator Database. Contact Administrator if unknown.";
                System.out.println("SENDING SMS HP NUMBER " + hpAdmin);
                sendSMS.sendSMS(hpAdmin, sendingMessage);

                adminTable.getItems().remove(admins);
                successfulMessage = "This Administrator " + emailAdmin + " was successfully removed.";
                successfulMessage(anchorPane.getScene(), successfulMessage, "Close");
                alert.hideWithAnimation();
            }
        });
        no.setOnAction(__addEvent -> {
            checker2 = 0;
            System.out.println("NO IS PRESSED, CHECKER2 is " + checker2);
            errorMessage = "This Administrator " + emailAdmin + " was not successfully removed.";
            errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            alert.hideWithAnimation();
        });
        alert.show();
    }

    private void doubleConfirmation(Scene scene, String doubleconfirm, String buttonContent, String buttonContent2) {
        checker2 = -1;
        try {
            myIPAddress = IPAddressPolicy.getIp();
            System.out.println(myIPAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            CHECKING = checker2;
            ipChecker = IPAddressPolicy.isValidRange(myIPAddress);
            System.out.println("IS IT WITHIN IP RANGE? = " + ipChecker);
            if (ipChecker == false) {
                //Display not within ip range error message
                errorMessage = "You are not within the company's premises to perform this function.";
                errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            } else {
//                try {
                userinfodb.deleteUser(email1);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
                String sendingMessage = "You, " + email1 + " have been removed from FireE's Cloud Database. Contact Administrator if unknown.";
                System.out.println("SENDING SMS HP NUMBER " + handphoneNUMBER);
                if (handphoneNUMBER == null) {
                    System.out.println("NO PHONE NUMBER BUT STILL DELETING");
                    employeeTable.getItems().remove(users);
                    successfulMessage = "This user " + email1 + " was successfully removed.";
                    successfulMessage(anchorPane.getScene(), successfulMessage, "Close");
                    alert.hideWithAnimation();
                } else {
                    sendSMS.sendSMS(handphoneNUMBER, sendingMessage);
                    employeeTable.getItems().remove(users);
                    successfulMessage = "This user " + email1 + " was successfully removed.";
                    successfulMessage(anchorPane.getScene(), successfulMessage, "Close");
                    alert.hideWithAnimation();
                }

            }
        });
        no.setOnAction(__addEvent -> {
            checker2 = 0;
            System.out.println("NO IS PRESSED, CHECKER2 is " + checker2);
            errorMessage = "This user " + email1 + " was not successfully removed.";
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
