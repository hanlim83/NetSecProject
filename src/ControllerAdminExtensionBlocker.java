import Database.extension_DB;
import Model.IPAddressPolicy;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControllerAdminExtensionBlocker implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXCheckBox txtSetter;

    @FXML
    private JFXCheckBox exeSetter;

    @FXML
    private JFXCheckBox docxSetter;

    @FXML
    private JFXCheckBox dmgSetter;

    @FXML
    private JFXCheckBox mp4Setter;

    @FXML
    private JFXCheckBox wavSetter;

    @FXML
    private Label ipAddr;

    private String txt;
    private String docx;
    private String exe;
    private String dmg;
    private String mp4;
    private String wav;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            String whatismyIP = IPAddressPolicy.getIp();
            ipAddr.setText(whatismyIP);
            Boolean validityIP = IPAddressPolicy.isValidRange(whatismyIP);
            if(validityIP==true){
                ipAddr.setTextFill(Color.rgb(1, 0, 199));
            }
            else{
                ipAddr.setTextFill(Color.rgb(255, 0, 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        extension_DB add = new extension_DB();
        try {

            ArrayList array = add.getExtensionList();

            for (int i=0; i<array.size()-1;i++) {

                String hello = (String) array.get(0);

                if (hello.equalsIgnoreCase("")) {

                    boolean test = txtSetter.isSelected();
                    test = true;

                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    @FXML
    void applyAll(ActionEvent event) {


        if (txtSetter.isSelected()==false){

        } else if (docxSetter.isSelected()==false) {

        } else if (exeSetter.isSelected()==false) {

        } else if (dmgSetter.isSelected()==false) {

        } else if (mp4Setter.isSelected()==false) {

        } else if (wavSetter.isSelected()==false) {

        } else {
            System.out.print("do nothing");
        }

    }


}
