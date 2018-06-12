import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeScreenController implements Initializable {

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    private Scene myScene;

    public void initialize(URL url, ResourceBundle rb) {
        hamburgerBar();
    }

    public void hamburgerBar() {
        HamburgerSlideCloseTransition transition = new HamburgerSlideCloseTransition(hamburger);
        transition.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) ->{
            transition.setRate(transition.getRate()*-1);
            transition.play();
            });

//        VBox box = null;
//        try {
//            box = FXMLLoader.load(getClass().getResource("view/SideTab.fxml"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        drawer.setSidePane(box);
//            drawer.setVisible(false);
//
//
//        HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
//        transition.setRate(-1);
//        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
//            transition.setRate(transition.getRate() * -1);
//            transition.play();
//
//            if (drawer.isOpened()) {
//                drawer.close();
//                drawer.setDisable(true);
//            } else {
//                drawer.open();
//                drawer.setVisible(true);
//                drawer.setDisable(false);
//            }
//        });
    }
}
