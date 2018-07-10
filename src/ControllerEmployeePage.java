import Model.IAMPermissions;
import com.google.cloud.logging.Logging;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;
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

    private Scene myScene;

    public static AnchorPane rootP;

    String listPermission;
    IAMPermissions permissions = new IAMPermissions();
//    Pattern p = Pattern.compile("members");
    ArrayList<String> memberlist1 = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
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
                    System.out.println(listPermission);

                    //do regex & delimiter to get members and roles
                    //members above of roles are the members of that role!!
                    // got members, user, role
                    Scanner sc;
                    sc = new Scanner(listPermission).useDelimiter("members");
                    System.out.println(sc.nextLine());
                    memberlist1.add("try"+sc.nextLine());
                    while(sc.hasNextLine()){
                        memberlist1.add(sc.nextLine());
                        System.out.println("try1"+sc.nextLine());
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
