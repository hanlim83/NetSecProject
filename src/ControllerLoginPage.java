import Model.OAuth2Login;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.gax.paging.Page;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.api.client.util.Charsets.UTF_8;

public class ControllerLoginPage implements Initializable, Runnable {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXButton LoginButton;

    private OAuth2Login login = new OAuth2Login();
    private Scene myScene;
    private Credential credential;
    private Thread thread;
    private String threadName;
    static Timer t1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        t1=new Timer();
    }

    @FXML
    void onClickLoginButton(ActionEvent event) throws Exception {
        try {
            // authorization
            //credential=login.login();
            //new Thread(task).start();
            ControllerLoginPage worker = new ControllerLoginPage();
            Thread thread = new Thread(worker);
            thread.start();

            //run();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        timerUpdateInfo();
//        Platform.runLater(new Runnable() {
//            public void run() {
//                new Thread(task).start();
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUpPage.fxml"));
//                System.out.println(anchorPane.getScene());
//                myScene = anchorPane.getScene();
//                Stage stage = (Stage) (myScene).getWindow();
//                Parent nextView = null;
//                try {
//                    nextView = loader.load();
//                    ControllerSignUpPage controller = loader.<ControllerSignUpPage>getController();
//                    System.out.println("Email: " + login.getEmail());
//                    controller.passData(login.getEmail());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                stage.setScene(new Scene(nextView));
//                stage.setTitle("NSPJ");
//                stage.show();
//            }
//        });
//        //else goToDeviceCheck

    }

    private void timerUpdateInfo() {
        t1.schedule(tt1, 0, 1000);
    }

    TimerTask tt1 = new TimerTask() {
        @Override
        public void run() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!login.getEmail().equals(null)) {
                            t1.cancel();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUpPage.fxml"));
                            System.out.println(anchorPane.getScene());
                            myScene = anchorPane.getScene();
                            Stage stage = (Stage) (myScene).getWindow();
                            Parent nextView = null;
                            try {
                                nextView = loader.load();
                                ControllerSignUpPage controller = loader.<ControllerSignUpPage>getController();
                                System.out.println("Email: " + login.getEmail());
                                controller.passData(login.getEmail());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            stage.setScene(new Scene(nextView));
                            stage.setTitle("NSPJ");
                            stage.show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

//    Task task = new Task<Void>() {
//        @Override
//        public Void call() {
////                static final int max = 1000000;
////                for (int i = 1; i <= max; i++) {
////                    updateProgress(i, max);
////                }
////                return null;
//            try {
//                credential = login.login();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    };

    ///Will implement email check againstDB in future updates && also show spinner when running this
    @Override
    public void run() {
        try {
            credential = login.login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}