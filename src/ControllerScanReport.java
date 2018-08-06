import Model.Report;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerScanReport implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private TableView scanView;

    @FXML
    private TableColumn<Report, String> index;

    @FXML
    private TableColumn<Report, String> antiVirus;

    @FXML
    private TableColumn<Report, String> version;

    @FXML
    private TableColumn<Report, String> scanResult;

    @FXML
    private JFXButton homeButton;

    Scene myScene;


    public static AnchorPane rootP;


    private ObservableList<Report>hello;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        hamburgerBar();

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

        Report ok = new Report();
        ok.assignment();
        index.setCellValueFactory(new PropertyValueFactory<Report, String>("index"));
        antiVirus.setCellValueFactory(new PropertyValueFactory<Report, String>("software"));
        version.setCellValueFactory(new PropertyValueFactory<Report, String>("version"));
        scanResult.setCellValueFactory(new PropertyValueFactory<Report, String>("result"));

        hello = ok.tryThis();
        for (Report r : hello){
            r.toString();
        }
        scanView.setItems(hello);


    }

    public void hamburgerBar() {
        rootP = anchorPane;

        try {
            VBox box = FXMLLoader.load(getClass().getResource("UserSideTab.fxml"));
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