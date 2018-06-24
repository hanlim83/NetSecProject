import Model.OAuth2Login;
import com.google.api.client.auth.oauth2.Credential;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sun.nio.ch.ThreadPool;

import java.net.*;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class ControllerLoginPage implements Initializable, Runnable {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXButton LoginButton;

    @FXML
    private JFXSpinner LoadingSpinner;

    private OAuth2Login login = new OAuth2Login();
    private Scene myScene;
    private Credential credential;
//    private String threadName;
    private String email="";

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    //Thread thread;
    @FXML
    void onClickLoginButton(ActionEvent event) throws Exception {
        LoadingSpinner.setVisible(true);
        //try {
        // authorization
        //credential=login.login();
        //new Thread(task).start();
//        Platform.runLater(() -> {
//            try {
//        if (!thread.isAlive()) {
//        if( thread != null ) {
//            System.out.println("Destroying thread");
//            thread.getId();
//            thread.interrupt();
//            thread = new Thread(task);
//            thread.start();
//        }else{
//            thread = new Thread(task);
//            thread.start();
        //if(process.)
        LoginButton.setDisable(true);
        process.start();
        process.setOnSucceeded( e -> {
            if(email.equals("")){
                System.out.println("No email");
                process.reset();
                LoginButton.setDisable(false);
                LoadingSpinner.setVisible(false);
            }else{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUpPage.fxml"));
                myScene = anchorPane.getScene();
                Stage stage = (Stage) (myScene).getWindow();
                Parent nextView = null;
                try {
                    nextView = loader.load();
                    ControllerSignUpPage controller = loader.<ControllerSignUpPage>getController();
                    controller.passData(login.getEmail());
                } catch (IOException u) {
                    u.printStackTrace();
                }
                stage.setScene(new Scene(nextView));
                stage.setTitle("NSPJ");
                stage.show();
            }
        });
//            ControllerLoginPage worker = new ControllerLoginPage();
//            Thread thread = new Thread(worker);
//            thread.start();

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
    }

    //Service method
    Service process = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    try {
                        credential = login.login();
                        email=login.getEmail();
                    } catch (UnknownHostException u) {
                        Platform.runLater(() -> {
                            System.out.println("No wifi");
                            JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                            snackbar.show("Please check your internet connection", 3000);
                            //u.printStackTrace();
                        });
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    return null;
                }
            };
        }
    };

    //redundant codes
    Task<Void> task = new Task<Void>() {
        @Override
        public Void call(){
            //System.out.println("Thread running"+thread.getId());
            try {
                credential = login.login();

            } catch (UnknownHostException u) {
                Platform.runLater(() -> {
                    System.out.println("No wifi");
                    JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                    snackbar.show("Please check your internet connection", 3000);
                    //u.printStackTrace();
                });
            } catch (Exception e) {
                //e.printStackTrace();
            }
            //System.out.println("Thread running"+thread.getId());
            return null;
        }
    };

    ///Will implement email check againstDB in future updates && also show spinner when running this
    @Override
    public void run() {
            try {
                credential = login.login();
            } catch (UnknownHostException u) {
                Platform.runLater(() -> {
                    System.out.println("No wifi");
                    JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                    snackbar.show("Check your internet connection", 3000);
                    //u.printStackTrace();
                });
            } catch (Exception e) {
                //e.printStackTrace();
            }
    }
}