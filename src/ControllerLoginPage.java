import Model.OAuth2Login;
import com.google.api.client.auth.oauth2.Credential;

import java.io.IOException;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    @FXML
    private JFXButton TestButton;

    private OAuth2Login login = new OAuth2Login();
    private Scene myScene;
    private Credential credential;
    private String email = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    //TODO To be removed soon
    @FXML
    void onClickTestButton(ActionEvent event) throws Exception {
        //login.l.stop();
//        login.stopLocalServerReciver();
//        LoadingSpinner.setVisible(false);
//        LoginButton.setDisable(false);
        try {
            //login.l.stop();
            login.stopLocalServerReciver();
            LoadingSpinner.setVisible(false);
            LoginButton.setDisable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();

            String title = "";
            String content = "The connection timeout. Please try again";

            JFXButton close = new JFXButton("Close");

            close.setButtonType(JFXButton.ButtonType.RAISED);

            close.setStyle("-fx-background-color: #00bfff;");

            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Label(title));
            layout.setBody(new Label(content));
            layout.setActions(close);
            JFXAlert<Void> alert = new JFXAlert<>(stage);
            alert.setOverlayClose(true);
            alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
            alert.setContent(layout);
            alert.initModality(Modality.NONE);
            close.setOnAction(__ -> alert.hideWithAnimation());
            alert.show();
        });
    }

    private Timer timer;

    public void startTimer() {
        timer = new Timer();
        TimerTask Task = new TimerTask() {
            public void run() {
                try {
                    //login.l.stop();
                    login.stopLocalServerReciver();
                    LoadingSpinner.setVisible(false);
                    LoginButton.setDisable(false);
                    endTimer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    myScene = anchorPane.getScene();
                    Stage stage = (Stage) (myScene).getWindow();

                    String title = "";
                    String content = "The connection timeout. Please try again";

                    JFXButton close = new JFXButton("Close");

                    close.setButtonType(JFXButton.ButtonType.RAISED);

                    close.setStyle("-fx-background-color: #00bfff;");

                    JFXDialogLayout layout = new JFXDialogLayout();
                    layout.setHeading(new Label(title));
                    layout.setBody(new Label(content));
                    layout.setActions(close);
                    JFXAlert<Void> alert = new JFXAlert<>(stage);
                    alert.setOverlayClose(true);
                    alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
                    alert.setContent(layout);
                    alert.initModality(Modality.NONE);
                    close.setOnAction(__ -> alert.hideWithAnimation());
                    alert.show();
                });

            }
        };
        timer.schedule(Task, 60000);
    }

    public void endTimer() {
        timer.cancel();
        timer.purge();
    }


    int counter=0;
    //Thread thread;
//    private ScheduledFuture tableviewRunnable;
    //2nd time process does not complete bug
    @FXML
    void onClickLoginButton(ActionEvent event) throws Exception {
        LoadingSpinner.setVisible(true);
        String state=process.getState().toString();
        counter++;
        if (process.getState().toString().equals("RUNNING")){
//            process.cancel();
//            process.start();

            process.restart();
            //process.restart();
            startTimer();
        }else if(process.getState().toString().equals("READY")){
            process.start();
            startTimer();
        }else if(process.getState().toString().equals("CANCELLED")){
            process.start();
            startTimer();
        }
//        else{
//            System.out.println(state);
//            System.out.println(process.getState());
//            System.out.println("Failed to run");
//        }
//        process.start();
//        startTimer();

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

        process.setOnSucceeded(e -> {
            System.out.println("Process succeeded");
            if (email.equals("")) {
                System.out.println("No email");
                process.reset();
                LoginButton.setDisable(false);
                LoadingSpinner.setVisible(false);
            } else {
                endTimer();
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
        process.setOnCancelled(e -> {
            if (counter<1){
            System.out.println("Cancelled");
            process.reset();
            }else{
                System.out.println("Process succeeded");
                if (email.equals("")) {
                    System.out.println("No email");
                    process.reset();
                    LoginButton.setDisable(false);
                    LoadingSpinner.setVisible(false);
                } else {
                    endTimer();
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
            }
        });
        process.setOnFailed(e ->{
            System.out.println("Failed");
            process.reset();
        });

//        service.schedule(new Runnable() {
//            @Override
//            public void run() {
//                Platform.runLater(new Runnable() {
//                    @Override public void run() {
//                        try {
//                            login.l.stop();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        process.reset();
//                        LoadingSpinner.setVisible(false);
//                        LoginButton.setDisable(false);
//                    }
//                });
//            }
//        }, 5, TimeUnit.SECONDS);

//        ScheduledExecutorService scheduler
//                = Executors.newSingleThreadScheduledExecutor();
//
//        Runnable task = new Runnable() {
//            public void run() {
//                try {
//                    login.l.stop();
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
//                LoadingSpinner.setVisible(false);
//                LoginButton.setDisable(false);
//                //endTimer();
//                process.reset();
//                System.out.println("DONE");
//            }
//        };
//        int delay = 5;
//        scheduler.schedule(task, delay, TimeUnit.SECONDS);
//        scheduler.shutdown();


//            ControllerLoginPage worker = new ControllerLoginPage();
//            Thread thread = new Thread(worker);
//            thread.start();

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
                        System.out.println("First step done");
                        email = login.getEmail();
                        System.out.println("2nd step done"+email);
//                        if(counter>1){
//                            process.cancel();
//                            System.out.println("Restarting process");
//                        }
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
        public Void call() {
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