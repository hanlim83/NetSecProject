import Database.User;
import Database.User_InfoDB;
import Model.GetIAM;
import Model.IAMPermissions;
import com.google.cloud.logging.Logging;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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


    private Scene myScene;

    public static AnchorPane rootP;

    User_InfoDB userinfodb = new User_InfoDB();
    User users = new User();

    private ArrayList<User> userList;
    private ObservableList<User> userObservableList;

    GetIAM getiam = new GetIAM();

    String listPermission;
    IAMPermissions permissions = new IAMPermissions();
    ArrayList<String> getiamlist = new ArrayList<>();
    ArrayList<String> memberlist1 = new ArrayList<>();
    String allInformation;

//    ArrayList<GetIAM> getIAMLists;
    public User user(User user){
        users=user;
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
//                    for (int i=0;i<getiamlist.size();i++){
//                        System.out.println("Controller trying to print permissions from getiamlist " + getiamlist.get(i));
//                    }
                    for (String s : getiamlist){
                        allInformation += s+"\n";
                    }
                    System.out.println("Controller getting from JOINER (1 STRING) : " + allInformation);
                    //do regex & delimiter to get members and roles
                    //members above of roles are the members of that role!!
                    // got members, user, role
                        Scanner sc = new Scanner(allInformation).useDelimiter("members");
                        System.out.println("HERE===" + sc.nextLine());
                        memberlist1.add(sc.nextLine());
                        while(sc.hasNextLine()){
                            memberlist1.add(sc.next());
                            System.out.println("try1"+sc.next());
                        }



                    Platform.runLater(() -> {

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
