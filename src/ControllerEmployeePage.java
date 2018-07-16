import Database.User;
import Database.User_InfoDB;
import Model.ActionButtonTableCell;
import Model.GetIAM;
import Model.IAMExtract;
import Model.IAMPermissions;
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
    private AnchorPane secondAnchor;

    private Scene myScene;

    public static AnchorPane rootP;

    String errorMessage = "";
    String successfulMessage = "";
    String doubleConfirm = "";
    int CHECKING;
    int checker2;
    String CreateUser;

    static String email1;

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

        deletingEmployees.setCellFactory(ActionButtonTableCell.<User>forTableColumn("Revoke", (User users) -> {
            user(users);
            email1 = users.getEmail();
            System.out.println("THIS IS THE EMAIL!!" + email1);

            doubleConfirm = "This selected user \"" + users.getEmail() + "\" will be removed from the cloud database. Are you sure to delete it?";
            doubleConfirmation(anchorPane.getScene(), doubleConfirm, "No", "Yes");
            CHECKING = checker2;
            System.out.println("CHECKER NOW IS " + CHECKING);

            return users;
        }));

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
        process.start();

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

    public void newProcess(){
        process2.start();
        employeeButton.setDisable(true);

        process2.setOnSucceeded(e -> {
            employeeButton.setDisable(false);
            userObservableList = FXCollections.observableList(userList);
            employeeTable.setItems(userObservableList);
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

        process1.start();
        employeeButton.setDisable(true);

        process1.setOnSucceeded(e -> {
            employeeButton.setDisable(false);
//            userObservableList = FXCollections.observableList(userList);
//            employeeTable.setItems(userObservableList);
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
    void onClickCreate(MouseEvent event) {
        creatingUSERS(anchorPane.getScene(),"No", "Yes");
        CHECKING = checker2;
        System.out.println("CHECKER NOW IS " + CHECKING);
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
            employeeTable.getItems().remove(users);
            successfulMessage = "This user " + email1 +" was successfully removed.";
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

    private void creatingUSERS(Scene scene, String buttonContent, String buttonContent2) {
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
        layout.setPrefSize(546,299);
        layout.setHeading(new Label(title));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 100, 10, 0));

        JFXTextField inputUser = new JFXTextField();
        inputUser.setPrefWidth(250);
        inputUser.setPromptText("Type Email Here");

        grid.add(new Label(message1),0,0);
        grid.add(inputUser,1,0);
        layout.setBody(grid);

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
            if(CreateUser.contains("@gmail.com")) {
                try {
                    userinfodb.createUser(CreateUser);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                newProcess();
                successfulMessage="This user "+CreateUser+ " was created and added into the cloud.";
                successfulMessage(anchorPane.getScene(),successfulMessage, "Close");
                alert.hideWithAnimation();
            }else{
                System.out.println("NOT ACCEPTED AS NOT A VALID EMAIL");
                errorMessage="Please try again. Email was not accepted.";
                errorMessagePopOut(anchorPane.getScene(),errorMessage, "Close");
            }
        });
        no.setOnAction(__addEvent -> {
            checker2 = 0;
            System.out.println("NO IS PRESSED, CHECKER2 is " + checker2);

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
