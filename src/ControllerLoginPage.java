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

    private Scene myScene;

    private OAuth2Login login = new OAuth2Login();
    private WindowsUtils utils=new WindowsUtils();

    private Credential credential;
    private String email = "";
    private String AccStatus = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private Timer timer;

    public void startTimer() {
        timer = new Timer();
        TimerTask Task = new TimerTask() {
            public void run() {
                try {
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
        //TODO change timer back to 60000
        timer.schedule(Task, 60000);
    }

    public void endTimer() {
        timer.cancel();
        timer.purge();
        System.out.println("TIMER CANCELLEDDD");
    }


    private int counter = 0;

    @FXML
    void onClickLoginButton(ActionEvent event) throws Exception {

        String state = process.getState().toString();
        counter++;
        if (process.getState().toString().equals("RUNNING")) {
            process.restart();
            startTimer();
        } else if (process.getState().toString().equals("READY")) {
            process.start();
            startTimer();
        } else if (process.getState().toString().equals("CANCELLED")) {
            process.start();
            startTimer();
        }
        else{
            System.out.println(state);
            System.out.println(process.getState());
            System.out.println("Failed to run");
        }
//        process.start();
//        startTimer();

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
            endTimer();
            System.out.println("Process succeeded");
            if (email.equals("")) {
                System.out.println("No email");
                process.reset();
                LoginButton.setDisable(false);
                LoadingSpinner.setVisible(false);
            } else {
                if (AccStatus.equals("Inactive")){
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
                }else if(AccStatus.equals("Active")){
                    //Go to SMS OTP page
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("VerifyTextAuth.fxml"));
                    myScene = anchorPane.getScene();
                    Stage stage = (Stage) (myScene).getWindow();
                    Parent nextView = null;
                    try {
                        nextView = loader.load();
                        ControllerVerifyText controller = loader.<ControllerVerifyText>getController();
                        controller.sendNew("<Info sanitized>");
                    } catch (IOException u) {
                        u.printStackTrace();
                    }
                    stage.setScene(new Scene(nextView));
                    stage.setTitle("NSPJ");
                    stage.show();
                }else{
                    process.reset();
                    LoginButton.setDisable(false);
                    LoadingSpinner.setVisible(false);
                    myScene = anchorPane.getScene();
                    Stage stage = (Stage) (myScene).getWindow();

                    String title = "";
                    String content = "Permission Invalid: You are not allowed the access the app. Please contact youradministator for more information";

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
                    System.out.println("NOT INSIDE DB.REJECTED!!!");
                }
//                endTimer();

            }
        });
        process.setOnCancelled(e -> {
            if (counter < 1) {
                System.out.println("Cancelled");
                process.reset();
            } else {
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
        process.setOnFailed(e -> {
            endTimer();
            System.out.println("Failed");
            process.reset();
            LoginButton.setDisable(false);
            LoadingSpinner.setVisible(false);
        });

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
                    //After 2nd click spinner dosen't appear
                    LoadingSpinner.setVisible(true);
                    try {
                        credential = login.login();
                        System.out.println("First step done");
                        email = login.getEmail();
                        System.out.println("2nd step done" + email);
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
                    //after retrieve token, use that to cross check with the DB for active/inactive/null
                    if (!email.equals("")){
                        AccStatus=utils.getAccStatus(email);
                    }
                    return null;
                }
            };
        }
    };

    //redundant codes

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
//        endTimer();
//        Platform.runLater(() -> {
//            myScene = anchorPane.getScene();
//            Stage stage = (Stage) (myScene).getWindow();
//
//            String title = "";
//            String content = "The connection timeout. Please try again";
//
//            JFXButton close = new JFXButton("Close");
//
//            close.setButtonType(JFXButton.ButtonType.RAISED);
//
//            close.setStyle("-fx-background-color: #00bfff;");
//
//            JFXDialogLayout layout = new JFXDialogLayout();
//            layout.setHeading(new Label(title));
//            layout.setBody(new Label(content));
//            layout.setActions(close);
//            JFXAlert<Void> alert = new JFXAlert<>(stage);
//            alert.setOverlayClose(true);
//            alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
//            alert.setContent(layout);
//            alert.initModality(Modality.NONE);
//            close.setOnAction(__ -> alert.hideWithAnimation());
//            alert.show();
//        });
    }

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