import Database.admin_DB;
import Model.IPAddressPolicy;
import Model.OAuth2LoginAdmin;
import com.google.api.client.auth.oauth2.Credential;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public class ControllerAdminHome implements Initializable {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private Label LastLoginLabel;

    @FXML
    private Label TimeLabel;

    @FXML
    private Label GreetingsLabel;

    @FXML
    private Label ipAddr;

    private Scene myScene;

    public static AnchorPane rootP;

    private Credential credential;
    private OAuth2LoginAdmin login = new OAuth2LoginAdmin();

    private static String lastLogin;
    private static int loginCounter = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        admin_DB admin_db=new admin_DB();
//        String email = null;
//        try {
//            credential=login.login();
//            email=login.getEmail();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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

        hamburgerBar();
        updateTimer();
//        timerprocess.start();
        try {
            InfoUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Timer timer;
    private static int counter = 0;

    public static void StopTimer() {
        if (counter != 0) {
            timer.purge();
            timer.cancel();
        }
    }


    private void updateTimer() {
        counter++;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String string = new SimpleDateFormat("HH:mm:ss").format(new Date());
                Platform.runLater(() -> {
                    TimeLabel.setText(string);
                });
            }
        }, 0, 100);
    }


    private Service timerprocess = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    updateTimer();
                    return null;
                }
            };
        }
    };

    private static String email;
    private static String name;

    //set as global var
    public void passData(String name, String email, String lastLogin) {
        this.name = name;
        this.email = email;
        this.lastLogin = lastLogin;
        process.start();
        process.setOnSucceeded(e -> {
            process.reset();
        });
        process.setOnCancelled(e -> {
            process.reset();
        });
        process.setOnFailed(e -> {
            process.reset();
        });LocalDateTime now = LocalDateTime.now();
        String ActivationTime = now.toString();
        admin_DB admin_db = new admin_DB();
        admin_db.setLastLoginTime(ActivationTime, email);

    }

    private Service process = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    InfoUpdate();
                    return null;
                }
            };
        }
    };

    private static boolean firstTime;

    private void InfoUpdate(){
//        credential = login.login();
//        String email;
//        email=login.getEmail();
        if (loginCounter == 0) {
//                if (admin_db.getLastLoginTime(email) == null) {
//                    LastLoginLabel.setText("Welcome this is your first time logging in");
//                    firstTime = true;
//                } else {
//                    lastLogin = convertTime(admin_db.getLastLoginTime(email));
//                    LastLoginLabel.setText("Your last login was on " + lastLogin);
//                    //else this^
//                    //also make this reference the static timing instead
//                    firstTime = false;
//                }
            Platform.runLater(() -> {
                GreetingsLabel.setText(getGreetings() + name);
                LastLoginLabel.setText(lastLogin);
                JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                snackbar.show("Information Updated", 3000);
                snackbar.getStylesheets().add("Style.css");
                loginCounter++;
            });
//                GreetingsLabel.setText(getGreetings() + name);
        } else {
//                if (firstTime == true) {
//                    LastLoginLabel.setText("Welcome this is your first time logging in");
//                } else {
//                    LastLoginLabel.setText("Your last login was on " + lastLogin);
//                }
//                GreetingsLabel.setText(getGreetings() + name);
            Platform.runLater(() -> {
                GreetingsLabel.setText(getGreetings() + name);
                LastLoginLabel.setText(lastLogin);
//                JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
//                snackbar.show("Information Updated", 3000);
//                snackbar.getStylesheets().add("Style.css");
            });
        }
    }

    private String convertTime(String time) {
        String dateDisplay;
        String timeDisplay;
        Scanner s = new Scanner(time).useDelimiter("T");
        String dateGeneral = s.next();
        timeDisplay = s.next();
        Scanner s1 = new Scanner(dateGeneral).useDelimiter("-");
        String year = s1.next();
        String month = s1.next();
        String date = s1.next();
        dateDisplay = date + "/" + month + "/" + year;

        return dateDisplay + " " + timeDisplay.substring(0, 8);
    }

    private String getGreetings() {
        String greetings = null;
        int hours = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
        if (hours >= 0 && hours <= 12) {
            greetings = "Good morning ";
        } else if (hours >= 12 && hours <= 16) {
            greetings = "Good afternoon ";
        } else if (hours >= 16 && hours <= 21) {
            greetings = "Good evening ";
        } else if (hours >= 21 && hours <= 24) {
            greetings = "Good night ";
        }
        return greetings;
    }

    public void hamburgerBar() {
        rootP = anchorPane;

        try {
            VBox box = FXMLLoader.load(getClass().getResource("AdminSideTab.fxml"));
            drawer.setSidePane(box);
            drawer.setVisible(false);
            drawer.setDefaultDrawerSize(219);
        } catch (IOException ex) {

        }

        HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
        transition.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            transition.setRate(transition.getRate() * -1);
            transition.play();

            if (drawer.isOpened()) {
                drawer.close();
                drawer.setDisable(true);
            } else {
                drawer.open();
                drawer.setVisible(true);
                drawer.setDisable(false);
            }
        });
    }
}
