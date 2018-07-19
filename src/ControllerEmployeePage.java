import Database.Admin;
import Database.User;
import Database.User_InfoDB;
import Database.admin_DB;
import Model.*;
import com.google.api.services.iam.v1.Iam;
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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private TableColumn<User, Button> deletingEmployees;

    @FXML
    private JFXComboBox<String> jfxcombobox;

    @FXML
    private TableView<IAMExtract> rolesTable;

    @FXML
    private JFXButton createUser;

    @FXML
    private TableColumn<IAMExtract, String> roleColumn;

    @FXML
    private TableColumn<IAMExtract, String> userColumn;

    @FXML
    private TableColumn<IAMExtract, Button> revokePermissions;

    @FXML
    private AnchorPane secondAnchor;

    @FXML
    private JFXButton adminButton;

    @FXML
    private JFXButton createAdmin;

    @FXML
    private TableView<Admin> adminTable;

    @FXML
    private TableColumn<Admin, String> entryID1;

    @FXML
    private TableColumn<Admin, String> email2;

    @FXML
    private TableColumn<Admin, String> handphone1;

    @FXML
    private TableColumn<Admin, String> status1;

    @FXML
    private TableColumn<Admin, String> hashpassword1;

    @FXML
    private TableColumn<Admin, Button> deletingAdmins;

    private Scene myScene;

    public static AnchorPane rootP;

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

    SMS sendSMS = new SMS();

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

    public IAMExtract iamExtract(IAMExtract iamExtract){
        iamExtracts=iamExtract;
        return iamExtracts;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        entryID.setCellValueFactory(new PropertyValueFactory<User, String>("entryID"));
        email.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        handphone.setCellValueFactory(new PropertyValueFactory<User, String>("phoneNo"));
        status.setCellValueFactory(new PropertyValueFactory<User, String>("status"));
        hashpassword.setCellValueFactory(new PropertyValueFactory<User, String>("hashPassword"));

        entryID1.setCellValueFactory(new PropertyValueFactory<Admin, String>("entryID"));
        email2.setCellValueFactory(new PropertyValueFactory<Admin, String>("email"));
        handphone1.setCellValueFactory(new PropertyValueFactory<Admin, String>("phoneNo"));
        status1.setCellValueFactory(new PropertyValueFactory<Admin, String>("status"));
        hashpassword1.setCellValueFactory(new PropertyValueFactory<Admin, String>("hashPassword"));

        try {
            userList = userinfodb.getUserList();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            adminList = adminDB.getAdminList();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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

        userObservableList = FXCollections.observableList(userList);
        employeeTable.setItems(userObservableList);

        jfxcombobox.getItems().addAll("Owner", "Editor", "Viewer", "CloudSQL Admin", "Firebase Rules System", "Compute Engine Service", "Logging Admin", "Storage Admin", "Monitoring Admin", "API Keys Admin");

        userColumn.setCellValueFactory(new PropertyValueFactory<IAMExtract, String>("globalUser"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<IAMExtract, String>("globalRole"));

        getIAMLists = IAMExtract.iamobjlist;
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
                        for(int u=0;u<stringArrayList.size();u++){
                            stringEmail=stringArrayList.get(u);
                            System.out.println("======================"+stringEmail);
                        }

                    } else if (chosenRole == "Editor") {
                        globalChecker = 2;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        for(int u=0;u<stringArrayList.size();u++){
                            stringEmail=stringArrayList.get(u);
                            System.out.println("======================"+stringEmail);
                        }
                    } else if (chosenRole == "Viewer") {
                        globalChecker = 3;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        for(int u=0;u<stringArrayList.size();u++){
                            stringEmail=stringArrayList.get(u);
                            System.out.println("======================"+stringEmail);
                        }
                    } else if (chosenRole == "CloudSQL Admin") {
                        globalChecker = 4;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        for(int u=0;u<stringArrayList.size();u++){
                            stringEmail=stringArrayList.get(u);
                            System.out.println("======================"+stringEmail);
                        }
                    } else if (chosenRole == "Firebase Rules System") {
                        globalChecker = 5;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        for(int u=0;u<stringArrayList.size();u++){
                            stringEmail=stringArrayList.get(u);
                            System.out.println("======================"+stringEmail);
                        }
                    } else if (chosenRole == "Compute Engine Service") {
                        globalChecker = 6;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        for(int u=0;u<stringArrayList.size();u++){
                            stringEmail=stringArrayList.get(u);
                            System.out.println("======================"+stringEmail);
                        }
                    } else if (chosenRole == "Logging Admin") {
                        globalChecker = 7;
                        stringArrayList = IAMExtract.stringEmailList;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        for(int u=0;u<stringArrayList.size();u++){
                            stringEmail=stringArrayList.get(u);
                            System.out.println("======================"+stringEmail);
                        }
                    } else if (chosenRole == "Storage Admin") {
                        globalChecker = 8;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        for(int u=0;u<stringArrayList.size();u++){
                            stringEmail=stringArrayList.get(u);
                            System.out.println("======================"+stringEmail);
                        }
                    } else if (chosenRole == "Monitoring Admin") {
                        globalChecker = 9;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        for(int u=0;u<stringArrayList.size();u++){
                            stringEmail=stringArrayList.get(u);
                            System.out.println("======================"+stringEmail);
                        }
                    } else {
                        globalChecker = 10;
                        getiam.takeinGlobalChecker(globalChecker);
                        permissions.listPermissions();
                        stringArrayList = IAMExtract.stringEmailList;
                        for(int u=0;u<stringArrayList.size();u++){
                            stringEmail=stringArrayList.get(u);
                            System.out.println("======================"+stringEmail);
                        }
                    }

                    Platform.runLater(() -> {
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
        //IF EMAIL IS NOT -users: , DO NOT PUT BUTTON INSIDE CELL

        revokePermissions.setCellFactory(new Callback<TableColumn<IAMExtract, Button>, TableCell<IAMExtract, Button>>() {
            String oneEmail;
            @Override
            public TableCell<IAMExtract, Button> call(TableColumn<IAMExtract, Button> param) {
                return new TableCell<IAMExtract, Button>() {

                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);

                        if(!isEmpty()) {
                            revokePermissions.setCellFactory(ActionButtonTableCell.<IAMExtract>forTableColumn("Revoke", (IAMExtract iamExtracts) -> {
                                iamExtract(iamExtracts);
                                emailPermission = iamExtracts.getGlobalUser();
                                emailPermission = emailPermission.substring(9,emailPermission.length());
                                System.out.println("THIS IS THE EMAIL!!" + emailPermission);

                                rolePermission = iamExtracts.getGlobalRole();
                                System.out.println("THIS IS THE ROLE: " + rolePermission);

                                doubleConfirm = "This selected user \"" + emailPermission + "\" will be revoked of this role " + rolePermission + ". Are you sure to delete it?";
                                doubleConfirmation2(anchorPane.getScene(), doubleConfirm, "No", "Yes");
                                CHECKING = checker2;
                                System.out.println("CHECKER NOW IS " + CHECKING);

                                return iamExtracts;
                            }));
                            for (int b = 0; b < stringArrayList.size(); b++) {
                                oneEmail = stringArrayList.get(b);
                                if (!oneEmail.contains("user:")) {
                                    System.out.println("NO BUTTON BECAUSE NOT USER, ITS GOOGLE SERVICE ACCOUNT");
                                    this.setDisable(true);
                                }
//                                else {
//                                    this.setDisable(true);
//                                    System.out.println("NO BUTTON BECAUSE NOT USER, ITS GOOGLE SERVICE ACCOUNT");
//                                }
                            }
                            setItem(item);

                        }

                    }

                };
            }
        });




        process.setOnSucceeded(e -> {
            jfxcombobox.setDisable(false);
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

    Service process1 = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    try {
                        userList = userinfodb.getUserList();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

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
                    try {
                        adminList = adminDB.getAdminList();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

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
                    try {
                        userList = userinfodb.getUserList();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> {
                        userObservableList = FXCollections.observableList(userList);
                        employeeTable.setItems(userObservableList);

                        JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
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
                    try {
                        adminList = adminDB.getAdminList();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> {
                        adminObservableList = FXCollections.observableList(adminList);
                        adminTable.setItems(adminObservableList);

                        JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                        snackbar.show("Updating The Database", 3000);
                    });
                    return null;
                }

            };
        }
    };

    public void newProcess() {
        employeeButton.setDisable(true);
        process2.start();

        process2.setOnSucceeded(e -> {
            employeeButton.setDisable(false);
//            userObservableList = FXCollections.observableList(userList);
//            employeeTable.setItems(userObservableList);
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
        adminButton.setDisable(true);

        process2a.setOnSucceeded(e -> {
            spinner.setVisible(false);
            adminButton.setDisable(false);
//            adminObservableList = FXCollections.observableList(adminList);
//            adminTable.setItems(adminObservableList);
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
    void onClickAdmin(MouseEvent event) {
        process1a.start();
        secondAnchor.setVisible(false);
        employeeTable.setVisible(false);
        adminTable.setVisible(true);
        adminButton.setDisable(true);

        process1a.setOnSucceeded(e -> {
            adminButton.setDisable(false);
            process1a.reset();
        });
        process1a.setOnCancelled(e -> {
            process1a.reset();
        });
        process1a.setOnFailed(e -> {
            process1a.reset();
        });
    }

    @FXML
    void onClickEmployee(MouseEvent event) {
        secondAnchor.setVisible(false);
        adminTable.setVisible(false);
        employeeTable.setVisible(true);

        process1.start();
        employeeButton.setDisable(true);

        process1.setOnSucceeded(e -> {
            employeeButton.setDisable(false);
            process1.reset();
        });
        process1.setOnCancelled(e -> {
            process1.reset();
        });
        process1.setOnFailed(e -> {
            process1.reset();
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

            if (CreateUser.contains("@gmail.com") && !creatingRoles.getSelectionModel().isEmpty()) {
                try {
                    userinfodb.createUser(CreateUser);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                newProcess();

                if (CreateRole.equals("Owner")) {
                    CreateRole = "roles/owner";
                    System.out.println("CHOOSE " + CreateRole);
                    permissions.addPermissions(CreateUser, CreateRole);
                } else if (CreateRole == "Editor") {
                    CreateRole = "roles/editor";
                    System.out.println("CHOOSE " + CreateRole);
                    permissions.addPermissions(CreateUser, CreateRole);
                } else if (CreateRole == "Viewer") {
                    CreateRole = "roles/viewer";
                    System.out.println("CHOOSE " + CreateRole);
                    permissions.addPermissions(CreateUser, CreateRole);
                } else if (CreateRole == "CloudSQL Admin") {
                    CreateRole = "roles/cloudsql.admin";
                    System.out.println("CHOOSE " + CreateRole);
                    permissions.addPermissions(CreateUser, CreateRole);
                } else if (CreateRole == "Firebase Rules System") {
                    CreateRole = "roles/firebaserules.system";
                    System.out.println("CHOOSE " + CreateRole);
                    permissions.addPermissions(CreateUser, CreateRole);
                } else if (CreateRole == "Compute Engine Service") {
                    CreateRole = "roles/compute.serviceAgent";
                    System.out.println("CHOOSE " + CreateRole);
                    permissions.addPermissions(CreateUser, CreateRole);
                } else if (CreateRole == "Logging Admin") {
                    CreateRole = "roles/logging.admin";
                    System.out.println("CHOOSE " + CreateRole);
                    permissions.addPermissions(CreateUser, CreateRole);
                } else if (CreateRole == "Storage Admin") {
                    CreateRole = "roles/storage.admin";
                    System.out.println("CHOOSE " + CreateRole);
                    permissions.addPermissions(CreateUser, CreateRole);
                } else if (CreateRole == "Monitoring Admin") {
                    CreateRole = "roles/monitoring.admin";
                    System.out.println("CHOOSE " + CreateRole);
                    permissions.addPermissions(CreateUser, CreateRole);
                } else if (CreateRole == "API Keys Admin") {
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


        });
        no.setOnAction(__addEvent -> {
            checker2 = 0;
            System.out.println("NO IS PRESSED, CHECKER2 is " + checker2);

            alert.hideWithAnimation();
        });

        alert.show();
    }

    private void creatingAdmins(Scene scene, String buttonContent, String buttonContent2) {
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

            if (CreateAdmin.contains("@gmail.com") && !creatingAdminRoles.getSelectionModel().isEmpty()) {
                if (CreateHandphone.matches(numbers) && CreateHandphone.length()==8 && (CreateHandphone.startsWith("8") || CreateHandphone.startsWith("9"))){
                    try {
                        adminDB.createAdmin(CreateAdmin,CreateHandphone);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    newProcessA();

                    if (CreateAdminRole.equals("Owner")) {
                        CreateAdminRole = "roles/owner";
                        System.out.println("CHOOSE " + CreateAdminRole);
                        permissions.addPermissions(CreateAdmin, CreateAdminRole);
                    } else if (CreateAdminRole == "Editor") {
                        CreateAdminRole = "roles/editor";
                        System.out.println("CHOOSE " + CreateAdminRole);
                        permissions.addPermissions(CreateAdmin, CreateAdminRole);
                    } else if (CreateRole == "CloudSQL Admin") {
                        CreateRole = "roles/cloudsql.admin";
                        System.out.println("CHOOSE " + CreateAdminRole);
                        permissions.addPermissions(CreateAdmin, CreateAdminRole);
                    } else if (CreateRole == "Firebase Rules System") {
                        CreateRole = "roles/firebaserules.system";
                        System.out.println("CHOOSE " + CreateAdminRole);
                        permissions.addPermissions(CreateAdmin, CreateAdminRole);
                    } else if (CreateRole == "Compute Engine Service") {
                        CreateRole = "roles/compute.serviceAgent";
                        System.out.println("CHOOSE " + CreateAdminRole);
                        permissions.addPermissions(CreateAdmin, CreateAdminRole);
                    } else if (CreateRole == "Logging Admin") {
                        CreateRole = "roles/logging.admin";
                        System.out.println("CHOOSE " + CreateAdminRole);
                        permissions.addPermissions(CreateAdmin, CreateAdminRole);
                    } else if (CreateRole == "Storage Admin") {
                        CreateRole = "roles/storage.admin";
                        System.out.println("CHOOSE " + CreateAdminRole);
                        permissions.addPermissions(CreateAdmin, CreateAdminRole);
                    } else if (CreateRole == "Monitoring Admin") {
                        CreateRole = "roles/monitoring.admin";
                        System.out.println("CHOOSE " + CreateAdminRole);
                        permissions.addPermissions(CreateAdmin, CreateAdminRole);
                    } else if (CreateRole == "API Keys Admin") {
                        CreateRole = "roles/serviceusage.apiKeysAdmin";
                        System.out.println("CHOOSE " + CreateAdminRole);
                        permissions.addPermissions(CreateAdmin, CreateAdminRole);
                    }

                    successfulMessage = "This Administrator " + CreateAdmin + " was created and added into the cloud as Administrator.";
                    successfulMessage(anchorPane.getScene(), successfulMessage, "Close");
                    alert.hideWithAnimation();
                }
                else {
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

            permissions.revokePermissions(emailPermission,rolePermission);
            rolesTable.getItems().remove(iamExtracts);

            successfulMessage = "This user " + emailPermission + " was successfully revoked of the role, " + rolePermission +".";
            successfulMessage(anchorPane.getScene(), successfulMessage, "Close");
            alert.hideWithAnimation();
        });
        no.setOnAction(__addEvent -> {
            checker2 = 0;
            System.out.println("NO IS PRESSED, CHECKER2 is " + checker2);
            errorMessage = "This user " + emailPermission + " was not successfully revoked of the role, " + rolePermission +".";
            errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            alert.hideWithAnimation();
        });
        alert.show();
    }

    private void doubleConfirmation1(Scene scene, String doubleconfirm, String buttonContent, String buttonContent2) {
        checker2 = -1;
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
            try {
                adminDB.deleteAdmin(emailAdmin,hpAdmin);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            String sendingMessage = "You, " + emailAdmin + " have been removed from FireE's Cloud Administrator Database. Please contact Administrator if unknown.";
            System.out.println("SENDING SMS HP NUMBER " + hpAdmin);
            sendSMS.sendSMS(hpAdmin,sendingMessage);

            adminTable.getItems().remove(admins);
            successfulMessage = "This Administrator " + emailAdmin + " was successfully removed.";
            successfulMessage(anchorPane.getScene(), successfulMessage, "Close");
            alert.hideWithAnimation();
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
            try {
                userinfodb.deleteUser(email1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            String sendingMessage = "You, " + email1 + " have been removed from FireE's Cloud Database. Please contact Administrator if unknown.";
            System.out.println("SENDING SMS HP NUMBER " + hpAdmin);
            sendSMS.sendSMS(handphoneNUMBER,sendingMessage);
            employeeTable.getItems().remove(users);
            successfulMessage = "This user " + email1 + " was successfully removed.";
            successfulMessage(anchorPane.getScene(), successfulMessage, "Close");
            alert.hideWithAnimation();
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
