import Model.OAuth2Login;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    private JFXButton TestButton;

    private Scene myScene;

    private OAuth2Login login = new OAuth2Login();
    private WindowsUtils utils = new WindowsUtils();

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

    public void Test(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("SecureCloudStorage.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();

        ControllerSecureCloudStorage controller = loader.<ControllerSecureCloudStorage>getController();
        //controller.passData(admin);

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();
    }

    @FXML
    void onClickLoginButton(ActionEvent event) throws Exception {
//        myScene = anchorPane.getScene();
//        Stage stage = (Stage) (myScene).getWindow();
//
//        String title = "";
//        String content = "The connection timeout. Please try again";
//
//        JFXButton close = new JFXButton("Close");
//
//        close.setButtonType(JFXButton.ButtonType.RAISED);
//
//        close.setStyle("-fx-background-color: #00bfff;");
//
////        JFXDialogLayout layout = new JFXDialogLayout();
////        layout.setHeading(new Label(title));
////        layout.setBody(new Label(content));
////        layout.setActions(close);
//        VBox box = FXMLLoader.load(getClass().getResource("UserSideTab.fxml"));
//        JFXAlert<Void> alert = new JFXAlert<>(stage);
//        alert.setOverlayClose(true);
//        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
//        alert.setContent(box);
//        alert.initModality(Modality.NONE);
////        close.setOnAction(__ -> {
////            try {
////                Test(event);
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        });
//        alert.show();

//
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
//                        controller.passData(login.getEmail());
                    } catch (IOException u) {
                        u.printStackTrace();
                    }
                    stage.setScene(new Scene(nextView));
                    stage.setTitle("NSPJ");
                    stage.show();
                }else{
                    //Not part of DB
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
                    //Check with another DB
                    if (!email.equals("")) {
                        AccStatus = utils.getAccStatus(email);
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
}