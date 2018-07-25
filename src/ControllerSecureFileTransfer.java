import Model.FileScanner;
import Model.OAuth2Login;
import Model.StorageSnippets;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import static com.google.common.base.Charsets.UTF_8;


public class ControllerSecureFileTransfer {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private AnchorPane TableAnchor;

    @FXML
    private JFXTextField searchBar;

    private String privateBucketName;
//    private ObservableList<ControllerSecureFileTransfer.TableBlob> blobs;
    //    private ArrayList<MyBlob> BlobList = new ArrayList<MyBlob>();
    private Storage storage;
    private String password;

    @FXML
    void transferFile(ActionEvent event) throws IOException {

        OAuth2Login creds = new OAuth2Login();

        Scanner sc = new Scanner(creds.getEmail());
        sc.useDelimiter("@");
        String name = sc.next();

        StorageSnippets storage = new StorageSnippets();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(null);

        String pathFile = file.getAbsolutePath();

        FileScanner scan = new FileScanner();
        scan.Scanner(file.getAbsolutePath());

        boolean check = scan.scannerReport();

        String inboxName = "inbox" + name;
        boolean duplicate = false; // hardcoded for now

        if (check == true && duplicate == false) {

//            Bucket bucket1 = storage.createBucketWithStorageClassAndLocation(inboxName);
//            Page<Bucket> buckets = storage.list();
//            for (Bucket bucket : buckets.iterateAll()) {
//                if (bucket.toString().contains(privateBucketName)) {
//                    System.out.println(bucket.toString());
//                    InputStream input = new ByteArrayInputStream(out);
////                InputStream targetStream = new FileInputStream(initialFile);
////            InputStream content = new ByteArrayInputStream("Hello, World!".getBytes(UTF_8));
//                    Blob blob = bucket1.create(filename, input, "text/plain");
//                }
//            }

        } else if (check == true && duplicate == true) {

//            InputStream content = new ByteArrayInputStream("Hello, World!".getBytes(UTF_8));
//            Blob blob = bucket.create(inboxName, content, "text/plain");

        }

    }
}
