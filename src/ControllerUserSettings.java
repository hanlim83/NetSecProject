import Database.User_InfoDB;
import Model.OAuth2Login;
import com.google.api.client.auth.oauth2.Credential;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public static AnchorPane rootP;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        if (old.equals(exist)) {

            user.setPhoneNo(number, mail);
            System.out.print("Change Successful!");

            changeNumber.setVisible(false);
            executeChange.setVisible(false);

        } else {

            System.out.println("Old phone number incorrect!");

        }

    }


}
