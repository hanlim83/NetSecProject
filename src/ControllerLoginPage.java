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
//    private static Timer t1;
    private String email="";

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //t1 = new Timer();
    }

    //Thread thread;
    @FXML
    void onClickLoginButton(ActionEvent event) throws Exception {
        LoadingSpinner.setVisible(true);
        //timerUpdateInfo();
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
        process.start();
        process.setOnSucceeded( e -> {
            // TODO, . . .
            // You can modify any GUI element from here...
            // ...with the values you got from the service
            //process.reset();
            System.out.println("Success");
            if(email.equals("")){
                process.reset();
            }else{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUpPage.fxml"));
                System.out.println(anchorPane.getScene());
                myScene = anchorPane.getScene();
                Stage stage = (Stage) (myScene).getWindow();
                Parent nextView = null;
                try {
                    nextView = loader.load();
                    ControllerSignUpPage controller = loader.<ControllerSignUpPage>getController();
                    //System.out.println("Email: " + login.getEmail());
                    controller.passData(login.getEmail());
                } catch (IOException u) {
                    u.printStackTrace();
                }
                stage.setScene(new Scene(nextView));
                stage.setTitle("NSPJ");
                stage.show();
            }
        });
//        process.start();
//        }
//        } else{
//            System.out.println(thread.getId());
//            thread.interrupt();
//        }

//            ControllerLoginPage worker = new ControllerLoginPage();
//            Thread thread = new Thread(worker);
//            thread.start();

        //run();
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }

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

//    private void timerUpdateInfo() {
//        t1.schedule(tt1, 0, 1000);
//    }
//
//    TimerTask tt1 = new TimerTask() {
//        @Override
//        public void run() {
//            Platform.runLater(new Runnable() {
//                @Override
//                public void run() {
//                        if (!email.equals(null)) {
//                                t1.cancel();
//                                FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUpPage.fxml"));
//                                System.out.println(anchorPane.getScene());
//                                myScene = anchorPane.getScene();
//                                Stage stage = (Stage) (myScene).getWindow();
//                                Parent nextView = null;
//                                try {
//                                    nextView = loader.load();
//                                    ControllerSignUpPage controller = loader.<ControllerSignUpPage>getController();
//                                    //System.out.println("Email: " + login.getEmail());
//                                    controller.passData(login.getEmail());
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                                stage.setScene(new Scene(nextView));
//                            stage.setTitle("NSPJ");
//                            stage.show();
//                        }
//                        //System.out.println("Check your codes!!! Commented out stacktrace to prevent spam when device is offline!!!!");
//                        //e.printStackTrace();
//                }
//            });
//        }
//    };


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