import Database.admin_DB;
import Model.OAuth2LoginAdmin;
import com.google.api.client.auth.oauth2.Credential;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class ControllerAdminLoginPage implements Initializable {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXButton LoginButton;

    @FXML
    private JFXSpinner LoadingSpinner;

    @FXML
    private JFXButton RevokeCredentialsButton;

    private Scene myScene;

    private OAuth2LoginAdmin login = new OAuth2LoginAdmin();
    private admin_DB admin_db = new admin_DB();

    private Credential credential;
    private String email = "";
    private String AccStatus = "";
    private String phoneNo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private static Timer timer;
    private TimerTask Task;

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
        //TODO change timer back to 60000
        timer.schedule(Task, 60000);
    }

    public void endTimer() {
        Task.cancel();
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
        } else {
            System.out.println(state);
            System.out.println(process.getState());
            System.out.println("Failed to run");
        }

        LoginButton.setDisable(true);
        RevokeCredentialsButton.setDisable(true);

        process.setOnSucceeded(e -> {
            endTimer();
            process.reset();
            LoginButton.setDisable(false);
            RevokeCredentialsButton.setDisable(false);
            LoadingSpinner.setVisible(false);
            System.out.println("Process succeeded");
            if (email.equals("")) {
                System.out.println("No email");
            } else {
                if (AccStatus.equals("1")) {
                    if (phoneNo != null) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminVerifyTextAuth.fxml"));
                        myScene = anchorPane.getScene();
                        Stage stage = (Stage) (myScene).getWindow();
                        Parent nextView = null;
                        try {
                            nextView = loader.load();
                            ControllerAdminVerifyTextAuth controller = loader.<ControllerAdminVerifyTextAuth>getController();
                            controller.adminAuth(phoneNo);
                        } catch (IOException u) {
                            u.printStackTrace();
                        }
                        stage.setScene(new Scene(nextView));
                        stage.setTitle("NSPJ");
                        stage.show();
                    } else {
                        System.out.println("No phone number try again");
                        process.reset();
                        LoginButton.setDisable(false);
                        RevokeCredentialsButton.setDisable(false);
                        LoadingSpinner.setVisible(false);
                        myScene = anchorPane.getScene();
                        Stage stage = (Stage) (myScene).getWindow();

                        String title = "Alert";
                        String content = "An error occurred. Please try again. Based on our algorithm you probably do not have a phone number. Please contact administrator to check.";

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
                    myScene = anchorPane.getScene();
                    Stage stage = (Stage) (myScene).getWindow();

                    String title = "";
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
                }
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

    @FXML
    void onClickRevokeCredentialsButton(ActionEvent event) {
        File file = new File(System.getProperty("user.home") + "\\" + ".store\\oauth2_sampleAdmin\\StoredCredential");
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
                            //u.printStackTrace();
                        });
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    //Check with another DB
                    if (!email.equals("")) {
                        AccStatus = admin_db.getAdminAccStatus(email);
                    }
                    if (AccStatus.equals("1")) {
                        phoneNo = admin_db.getPhoneNo(email);
                    }
                    return null;
                }
            };
        }
    };
}