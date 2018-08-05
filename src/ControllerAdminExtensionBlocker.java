import Database.extension_DB;
import Model.IPAddressPolicy;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private Scene myScene;

    public static AnchorPane rootP;

    private String txt;
    private String docx;
    private String exe;
    private String dmg;
    private String mp4;
    private String wav;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        hamburgerBar();
        

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

            ArrayList<String>array = add.getExtensionList();

            for (int i=0; i<array.size()-1;i++) {

                String hello = array.get(i);
                System.out.println(hello);

                if (hello.equalsIgnoreCase("")) {

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @FXML
    void applyAll(ActionEvent event) {

        extension_DB ext = new extension_DB();


        if (txtSetter.isSelected()==true){

            ext.createExtension(".txt");

        } else if (docxSetter.isSelected()==false) {

            ext.createExtension(".docx");

        } else if (exeSetter.isSelected()==false) {

            ext.createExtension(".exe");

        } else if (dmgSetter.isSelected()==false) {

            ext.createExtension(".dmg");

        } else if (mp4Setter.isSelected()==false) {

            ext.createExtension(".mp4");

        } else if (wavSetter.isSelected()==false) {

            ext.createExtension(".wav");

        } else {
            System.out.print("do nothing");
        }

    }

    public void hamburgerBar() {
        rootP = anchorPane;

        try {
            VBox box = FXMLLoader.load(getClass().getResource("AdminSideTab.fxml"));
            drawer.setSidePane(box);
            drawer.setVisible(false);
            drawer.setDefaultDrawerSize(219);
        } catch (IOException ex) {
            Logger.getLogger(ControllerSecureFileTransfer.class.getName()).log(Level.SEVERE, null, ex);
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


}
