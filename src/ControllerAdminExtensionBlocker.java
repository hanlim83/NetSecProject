import Database.extension_DB;
import Model.IPAddressPolicy;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerAdminExtensionBlocker implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXButton homeButton;

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

    private ArrayList<String>array;

    private String txt = "txt";
    private String docx = "docx";
    private String exe = "exe";
    private String dmg = "dmg";
    private String mp4 = "mp4";
    private String wav = "wav";

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

        Path path2 = FileSystems.getDefault().getPath("src/View/baseline_home_white_18dp.png");
        File file2 = new File(path2.toUri());
        Image imageForFile2;
        try {
            imageForFile2 = new Image(file2.toURI().toURL().toExternalForm());
            ImageView imageView1 = new ImageView(imageForFile2);
//            imageView.setFitHeight(24.5);
//            imageView.setFitWidth(35);
            homeButton.setGraphic(imageView1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        extension_DB add = new extension_DB();

            this.array = add.getExtensionList();
        System.out.println(array);

//            for (int i=0; i<array.size()-1;i++) {
//
//                String hello = array.get(i);
//                System.out.println(hello);



                for (int i=0;i<array.size();i++) {

                    if (array.get(i).equals(txt)) {
                        txtSetter.setSelected(true);
                    } else if (array.get(i).equals(docx)) {
                        docxSetter.setSelected(true);
                    } else if (array.get(i).equals(exe)) {
                        exeSetter.setSelected(true);
                    } else if (array.get(i).equals(dmg)) {
                        dmgSetter.setSelected(true);
                    } else if (array.get(i).equals(mp4)) {
                        mp4Setter.setSelected(true);
                        System.out.println( mp4Setter.getText());
                    } else if (array.get(i).equals(mp4)) {
                        wavSetter.setSelected(true);
                    }


                }


    }

    @FXML
    void onClickHomeButton (ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("AdminHome.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();

        ControllerAdminHome controller = loader.<ControllerAdminHome>getController();
        //controller.passData(admin);

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();
    }


    @FXML
    void applyAll(ActionEvent event) {

        extension_DB ext = new extension_DB();


        for (int i=0; i<6; i++) {
            array.clear();
            this.array = ext.getExtensionList();

            if (txtSetter.isSelected() && !array.get(i).equals(txt)) {
                ext.createExtension(txt);
                System.out.println(txt + " extension added!");
            } else if (!txtSetter.isSelected() && array.get(i).equals(txt)) {
                ext.deleteExtension(txt);
                System.out.println(txt + " extension removed!");


            } else if (docxSetter.isSelected() && !array.get(i).equals(docx)) {
                ext.createExtension("docx");
                System.out.println(docx + " extension added!");
            } else if (!docxSetter.isSelected() && array.get(i).equals(docx)) {
                ext.deleteExtension(docx);
                System.out.println(docx + " extension removed!");


            } else if (exeSetter.isSelected() && !array.get(i).equals(exe)) {
                ext.createExtension("exe");
                System.out.println(exe + " extension added!");
            } else if (!exeSetter.isSelected() && array.get(i).equals(exe)) {
                ext.deleteExtension(exe);
                System.out.println(exe + " extension removed!");


            } else if (dmgSetter.isSelected() && !array.get(i).equals(dmg)) {
                ext.createExtension("dmg");
                System.out.println(dmg + " extension added!");
            } else if (!dmgSetter.isSelected() && array.get(i).equals(dmg)) {
                ext.deleteExtension(dmg);
                System.out.println(dmg + " extension removed!");


            } else if (mp4Setter.isSelected() && !array.get(i).equals(mp4)) {
                ext.createExtension("mp4");
                System.out.println(mp4 + " extension added!");
            } else if (!mp4Setter.isSelected() && array.get(i).equals(mp4)) {
                ext.deleteExtension(mp4);
                System.out.println(mp4 + " extension removed!");


            } else if (wavSetter.isSelected() && !array.get(i).equals(wav)) {
                ext.createExtension("wav");
                System.out.println(wav + " extension added!");
            } else if (!wavSetter.isSelected() && array.get(i).equals(wav)) {
                ext.deleteExtension(wav);
                System.out.println(wav + " extension removed!");
            }
        }

        JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
        snackbar.getStylesheets().add("Style.css");
        snackbar.show("Extensions updated", 3000);
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
