import Model.Report;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerScanReport implements Initializable {


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


    private ObservableList<Report>hello;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

}