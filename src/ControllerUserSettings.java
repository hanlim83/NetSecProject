import Database.User_InfoDB;
import Model.OAuth2Login;
import com.google.api.client.auth.oauth2.Credential;
import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.SQLException;

public class ControllerUserSettings {


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
