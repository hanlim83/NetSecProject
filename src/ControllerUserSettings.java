import Database.User_InfoDB;
import Model.OAuth2Login;
import com.google.api.client.auth.oauth2.Credential;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class ControllerUserSettings implements Initializable {


    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private GridPane changeNumber;

    @FXML
    private JFXTextField oldNumber;

    @FXML
    private JFXTextField newNumber;

    @FXML
    private JFXButton executeChange;

    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXButton homeButton;

    Scene myScene;


    public static AnchorPane rootP;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Path path1 = FileSystems.getDefault().getPath("src/View/baseline_home_white_18dp.png");
        File file1 = new File(path1.toUri());
        Image imageForFile1;
        try {
            imageForFile1 = new Image(file1.toURI().toURL().toExternalForm());
            ImageView imageView1 = new ImageView(imageForFile1);
//            imageView.setFitHeight(24.5);
//            imageView.setFitWidth(35);
            homeButton.setGraphic(imageView1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        hamburgerBar();
    }

    public void hamburgerBar() {
        rootP = anchorPane;

        try {
            VBox box = FXMLLoader.load(getClass().getResource("UserSideTab.fxml"));
            drawer.setSidePane(box);
            drawer.setVisible(false);
            drawer.setDefaultDrawerSize(219);
        } catch (IOException ex) {

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

    @FXML
    void onClickHomeButton (ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("UserHome.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();

        ControllerUserHome controller = loader.<ControllerUserHome>getController();
        //controller.passData(admin);

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();

    }

    @FXML
    void newNumber(ActionEvent event) {
        if (changeNumber.isVisible()==false) {
            executeChange.setVisible(true);
            changeNumber.setVisible(true);
        } else if (changeNumber.isVisible()==true){
            changeNumber.setVisible(false);
            executeChange.setVisible(false);
        }
    }

    @FXML
    void newChange(ActionEvent event) throws Exception {

        User_InfoDB user = new User_InfoDB();
        OAuth2Login auth = new OAuth2Login();
        Credential credential = auth.login();

        String mail = auth.getEmail();
        String old = oldNumber.getText();
        String number = newNumber.getText();
        String exist = user.getPhoneNumber(mail);
        String pass = password.getText();

        // Need to add method to check password with the database


        if (old.isEmpty()==true || number.isEmpty()==true || pass.isEmpty()==true) {

            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();

            String title = "Input null";
            String content = "Please enter all data in the field";

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


        } else if (!user.checkPassword(pass, mail) || !old.equals(exist)) {


            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();

            String title = "Invalid";
            String content = "Invalid password or old number!";

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

        } else if (old.equals(exist) && user.checkPassword(pass, mail)==true) {

            user.setPhoneNo(number, mail);
            System.out.print("Change Successful!");

            changeNumber.setVisible(false);
            executeChange.setVisible(false);

        } else {

            System.out.println("Old phone number incorrect!");

        }

    }


}
