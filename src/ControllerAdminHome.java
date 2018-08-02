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
    private JFXButton randomButton;

    @FXML
    private JFXButton CloudStorageTestButton;

    @FXML
    private JFXButton RSAButton;

    @FXML
    private Label LastFileModifiedLabel;

    @FXML
    private Label TimeLabel;

    @FXML
    private Label GreetingsLabel;

    private Scene myScene;

    public static AnchorPane rootP;

    private Credential credential;
    private OAuth2LoginAdmin login = new OAuth2LoginAdmin();
    WindowsUtils utils = new WindowsUtils();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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


    ArrayList<MyBlob> BlobList = new ArrayList<MyBlob>();

    private void getLatestFile() throws IOException {
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
        String email = login.getEmail();
        Scanner s = new Scanner(email).useDelimiter("@");
        String emailFront = s.next();
        emailFront = emailFront.replace(".", "");
        String bucketname = emailFront + "nspj";
//        String bucketname="hugochiaxyznspj";
        Page<Blob> blobs = storage.list(bucketname);
        for (Blob blob : blobs.iterateAll()) {
            // do something with the blob
            BlobList.add(new MyBlob(blob));
            System.out.println("FROM METHOD" + blob);
            System.out.println(convertTime(blob.getCreateTime()));
            System.out.println("FROM METHOD" + blob.getName());
//            if (fileName.equals(blob.getName())) {
//                System.out.println("Choose Different NAME!");
//                return true;
//            }
        }
    }

    public String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(date);
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

    private void InfoUpdate() throws Exception {
        credential = login.login();
        getLatestFile();
        Collections.sort(BlobList);
        System.out.println("=======================LATEST FILE/TIME===============================");
        System.out.println(BlobList.get(0).toString());
        System.out.println(convertTime(BlobList.get(0).getTime()));
        LastFileModifiedLabel.setText("Your last file was modified on " + convertTime(BlobList.get(0).getTime()));
        GreetingsLabel.setText(getGreetings() + login.getName());
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
