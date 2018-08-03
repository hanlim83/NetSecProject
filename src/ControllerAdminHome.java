import Database.admin_DB;
import Model.MyBlob;
import Model.OAuth2LoginAdmin;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
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

import java.io.IOException;
import java.net.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private Scene myScene;

    public static AnchorPane rootP;

    private Credential credential;
    private OAuth2LoginAdmin login = new OAuth2LoginAdmin();

    private static String lastLogin;
    private static int loginCounter=0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        admin_DB admin_db=new admin_DB();
        String email = null;
        try {
            credential=login.login();
            email=login.getEmail();
        } catch (Exception e) {
            e.printStackTrace();
        }
        hamburgerBar();
        timerprocess.start();
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


//    ArrayList<MyBlob> BlobList = new ArrayList<MyBlob>();

//    private void getLatestFile() throws IOException {
//        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
//        String email = login.getEmail();
//        Scanner s = new Scanner(email).useDelimiter("@");
//        String emailFront = s.next();
//        emailFront = emailFront.replace(".", "");
//        String bucketname = emailFront + "nspj";
////        String bucketname="hugochiaxyznspj";
//        Page<Blob> blobs = storage.list(bucketname);
//        for (Blob blob : blobs.iterateAll()) {
//            // do something with the blob
//            BlobList.add(new MyBlob(blob));
//            System.out.println("FROM METHOD" + blob);
//            System.out.println(convertTime(blob.getCreateTime()));
//            System.out.println("FROM METHOD" + blob.getName());
////            if (fileName.equals(blob.getName())) {
////                System.out.println("Choose Different NAME!");
////                return true;
////            }
//        }
//    }

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

    private void InfoUpdate() throws Exception {
        credential = login.login();
        String email;
        email=login.getEmail();
        admin_DB admin_db=new admin_DB();
        if (loginCounter == 0) {
            lastLogin=convertTime(admin_db.getLastLoginTime(email));
            LocalDateTime now=LocalDateTime.now();
            String ActivationTime=now.toString();
            admin_db.setLastLoginTime(ActivationTime,email);
            loginCounter++;
            LastLoginLabel.setText("Your last login was on " + lastLogin);
            //else this^
            //also make this reference the static timing instead
            GreetingsLabel.setText(getGreetings() + login.getName());
        }else{
            LastLoginLabel.setText("Your last login was on " + lastLogin);
            GreetingsLabel.setText(getGreetings() + login.getName());
        }
//        System.out.println(lastLogin);

        //if last login null show another msg

        //set timing DB here
        //set static timing
        //increase counter

//        //contact DB to store last login time
//        if (loginCounter==0){
//            lastLogin=admin_db.getLastLoginTime(email);
//            loginCounter++;
//        }
//        //set new login time
    }

    private String convertTime(String time) {
        //Upgrade to string builder next time
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
//        Scanner s2 = new Scanner(timeGeneral).useDelimiter(".");
//        timeDisplay = s2.next();
//        String minute = s2.next();
//        String second = s2.next();
//        String time =

        return dateDisplay + " " + timeDisplay.substring(0,8);
    }

    private String getGreetings() {
        String greetings = null;
        int hours = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
        if(hours>=0 && hours<=12){
            greetings="Good morning ";
        }else if(hours>=12 && hours<=16){
            greetings="Good afternoon ";
        }else if(hours>=16 && hours<=21){
            greetings="Good evening ";
        }else if(hours>=21 && hours<=24){
            greetings="Good night ";
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
