import Model.StorageSnippets;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerBucketsPage implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXButton ListBuckets;

    @FXML
    private Label listedBuckets;

    @FXML
    private JFXButton CreateBuckets;

    @FXML
    private AnchorPane CreatingBuckets;

    private Scene myScene;

    public static AnchorPane rootP;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    hamburgerBar();
    }

    StorageSnippets storagesnippets = new StorageSnippets();

    @FXML
    void handleCreateBuckets(MouseEvent event) {
        CreatingBuckets.setVisible(true);
    }

    @FXML
    void handleListBuckets(MouseEvent event) {
        ArrayList<String> listedbuckets = new ArrayList();
        listedbuckets = storagesnippets.listBuckets();

        String BUCKETS = String.join("\n",listedbuckets);
        listedBuckets.setText(BUCKETS);

    }

    @FXML
    void handleExit(MouseEvent event) {
        CreatingBuckets.setVisible(false);
    }

    public void hamburgerBar() {
        rootP = anchorPane;

        try {
            VBox box = FXMLLoader.load(getClass().getResource("SideTab.fxml"));
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


}
