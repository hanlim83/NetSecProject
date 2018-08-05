import Model.OAuth2Login;
import Database.User_InfoDB;
import com.google.api.client.auth.oauth2.Credential;

import java.io.File;
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

public class ControllerLoginPage implements Initializable{
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXButton LoginButton;

    @FXML
    private JFXSpinner LoadingSpinner;

    @FXML
    private JFXButton RevokeCredentialsButton;

    private Scene myScene;

    private OAuth2Login login = new OAuth2Login();
    private User_InfoDB user_infoDB = new User_InfoDB();

    private Credential credential;
    private String email = "";
    private String AccStatus = "";
    private String phoneNo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private static Timer timer;
    private TimerTask Task;

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
        } else {
            System.out.println(state);
            System.out.println(process.getState());
            System.out.println("Failed to run");
        }

        LoginButton.setDisable(true);
        RevokeCredentialsButton.setDisable(true);

        process.setOnSucceeded(e -> {
            endTimer();
            System.out.println("Process succeeded");
            if (email.equals("")) {
                System.out.println("No email");
                process.reset();
                LoginButton.setDisable(false);
                RevokeCredentialsButton.setDisable(false);
                LoadingSpinner.setVisible(false);
            } else {
                if (AccStatus.equals("Inactive")) {
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
                } else if (AccStatus.equals("Active")) {
                    if (phoneNo!=null){
                        //Go to SMS OTP page
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("VerifyTextAuth.fxml"));
                        myScene = anchorPane.getScene();
                        Stage stage = (Stage) (myScene).getWindow();
                        Parent nextView = null;
                        try {
                            nextView = loader.load();
                            ControllerVerifyText controller = loader.<ControllerVerifyText>getController();
                            controller.sendNew(phoneNo);
                        } catch (IOException u) {
                            u.printStackTrace();
                        }
                        stage.setScene(new Scene(nextView));
                        stage.setTitle("NSPJ");
                        stage.show();
                    } else{
                        //show alert
                        System.out.println("No phone number try again");
                        process.reset();
                        LoginButton.setDisable(false);
                        RevokeCredentialsButton.setDisable(false);
                        LoadingSpinner.setVisible(false);
                        myScene = anchorPane.getScene();
                        Stage stage = (Stage) (myScene).getWindow();

                        String title = "";
                        String content = "An error occurred please try again";

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
                    }
                } else {
                    process.reset();
                    LoginButton.setDisable(false);
                    RevokeCredentialsButton.setDisable(false);
                    LoadingSpinner.setVisible(false);
                    myScene = anchorPane.getScene();
                    Stage stage = (Stage) (myScene).getWindow();

                    String title = "Warning";
                    String content = "Permission Invalid: You are not allowed the access the app. Please contact your administrator for more information";

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
            endTimer();
            if (counter < 1) {
                System.out.println("Cancelled");
                process.reset();
            } else {
                System.out.println("Process succeeded");
                if (email.equals("")) {
                    System.out.println("No email");
                    process.reset();
                    LoginButton.setDisable(false);
                    RevokeCredentialsButton.setDisable(false);
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

            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();
            String title = "";
            String content = "An error occured. Please try again later";

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
            process.reset();
            LoginButton.setDisable(false);
            RevokeCredentialsButton.setDisable(false);
            LoadingSpinner.setVisible(false);
        });
    }

    private void startTimer() {
        timer = new Timer();
        Task = new TimerTask() {
            public void run() {
                try {
                    login.stopLocalServerReciver();
                    LoadingSpinner.setVisible(false);
                    LoginButton.setDisable(false);
                    RevokeCredentialsButton.setDisable(false);
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

    private void endTimer() {
        Task.cancel();
        timer.cancel();
        timer.purge();
        System.out.println("TIMER CANCELLEDDD");
    }

    private int counter = 0;

    @FXML
    void onClickRevokeCredentialsButton(ActionEvent event) {
        File file = new File(System.getProperty("user.home") + "\\" + ".store\\oauth2_sample\\StoredCredential");
        file.delete();

        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();

        String title = "";
        String content = "Credentials successfully revoked";

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
    }

    //Service method
    Service process = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    LoadingSpinner.setVisible(true);
                    try {
                        credential = login.login();
                        System.out.println("First step done");
                        email = login.getEmail();
                        System.out.println("2nd step done" + email);
                    } catch (UnknownHostException u) {
                        Platform.runLater(() -> {
                            System.out.println("No wifi");
                            JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                            snackbar.show("Please check your internet connection", 3000);
                            snackbar.getStylesheets().add("Style.css");
                            //u.printStackTrace();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //after retrieve token, use that to cross check with the DB for active/inactive/null
                    if (!email.equals("")) {
                        AccStatus = user_infoDB.getAccStatus(email);
                    }
                    if (AccStatus.equals("Active")) {
                        phoneNo = user_infoDB.getPhoneNumber(email);
                    }
                    return null;
                }
            };
        }
    };
}