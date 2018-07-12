import Model.MyBlob;
import Model.OAuth2Login;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ControllerSecureCloudStorage implements Initializable {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private com.jfoenix.controls.JFXTreeTableView<TableBlob> JFXTreeTableView;

    @FXML
    private AnchorPane TableAnchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXTextField filterField;

    private Scene myScene;

    public static AnchorPane rootP;

    private Credential credential;
    private OAuth2Login login = new OAuth2Login();

    private String privateBucketName;
    private ObservableList<TableBlob> blobs;
    private ArrayList<MyBlob> BlobList = new ArrayList<MyBlob>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        try {
            credential = login.login();
        } catch (Exception e) {
            e.printStackTrace();
        }

        TableMethod();
    }

    @FXML
    private JFXButton TestButton;


    @FXML
    void onClickTestButton(ActionEvent event) throws IOException {
//        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
//        String email=login.getEmail();
//        Scanner s = new Scanner(email).useDelimiter("@");
//        String emailFront=s.next();
//        String bucketname=emailFront+"nspj";
////        String bucketname="hugochiaxyznspj";
//        Page<Blob> blobs = storage.list(bucketname);
//        for (Blob blob : blobs.iterateAll()) {
//            // do something with the blob
//            System.out.println("FROM METHOD" + blob);
//            System.out.println(convertTime(blob.getCreateTime()));
//            System.out.println("FROM METHOD" + blob.getName());
//
////            if (fileName.equals(blob.getName())) {
////                System.out.println("Choose Different NAME!");
////                return true;
////            }
//        }
        UploadFileTest();
        TableMethod();
    }

    public String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat(" dd/MM/yyyy HH:mm:ss");
        return format.format(date);
    }

    public void UploadFileTest(){
        try {
            // authorization
            credential = login.login();
            // set up global Oauth2 instance
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            //FEATURE: Add ownerWindow to block screen
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                String pathsInfo = "";
                pathsInfo += "getPath(): " + file.getPath() + "\n";
                pathsInfo += "getAbsolutePath(): " + file.getAbsolutePath() + "\n";

                pathsInfo += (new File(file.getPath())).isAbsolute();

                try {
                    pathsInfo += "getCanonicalPath(): " +
                            file.getCanonicalPath() + "\n";
                } catch (IOException ex) {

                }
                System.out.println(pathsInfo);
                // authorization + Get Buckets
                Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
                //Testing for storage
                Page<Bucket> buckets = storage.list();
                for (Bucket bucket : buckets.iterateAll()) {
                    System.out.println(bucket.toString());
                }

                for (Bucket bucket : buckets.iterateAll()) {
                    Page<Blob> blobs = bucket.list();
                    for (Blob blob : blobs.iterateAll()) {
                        // do something with the blob
                        System.out.println(blob);
                        System.out.println(blob.getName());
                    }
                }
                //String filename= "TestFILENEW1";
                //Actual Codes
                if (checkNameTaken(file.getName()) == true) {
                    System.out.println("Change NAME!!!!");
                } else {
                    uploadFile(file.getName(), file.getAbsolutePath());
                }
            } else {
                System.out.println("No file selected");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public boolean checkNameTaken(String fileName) {
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
        Page<Blob> blobs = storage.list("hr_dept");
        for (Blob blob : blobs.iterateAll()) {
            // do something with the blob
            System.out.println("FROM METHOD" + blob);
            System.out.println("FROM METHOD" + blob.getName());
            if (fileName.equals(blob.getName())) {
                System.out.println("Choose Different NAME!");
                return true;
            }
        }
        return false;
    }

    public void uploadFile(String filename, String AbsolutePath) throws FileNotFoundException {
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
        Page<Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll()) {
            if (bucket.toString().contains(privateBucketName)){
                System.out.println(bucket.toString());
                File initialFile = new File(AbsolutePath);
                InputStream targetStream = new FileInputStream(initialFile);
//            InputStream content = new ByteArrayInputStream("Hello, World!".getBytes(UTF_8));
                Blob blob = bucket.create(filename, targetStream, "text/plain");
            }
        }
    }


    private void TableMethod() {
        blobs = FXCollections.observableArrayList();
//        users.add(new TreeTableDemo.User(COMPUTER_DEPARTMENT, "23", "CD 1"));
//        users.add(new TreeTableDemo.User(SALES_DEPARTMENT, "22", "Employee 1"));
//        users.add(new TreeTableDemo.User(SALES_DEPARTMENT, "24", "Employee 2"));
//        users.add(new TreeTableDemo.User(SALES_DEPARTMENT, "25", "Employee 4"));
//        users.add(new TreeTableDemo.User(SALES_DEPARTMENT, "27", "Employee 5"));
//        users.add(new TreeTableDemo.User(IT_DEPARTMENT, "42", "ID 2"));
//        users.add(new TreeTableDemo.User(HR_DEPARTMENT, "21", "HR 1"));
//        users.add(new TreeTableDemo.User(HR_DEPARTMENT, "28", "HR 2"));
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
        String email = null;
        try {
            email = login.getEmail();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scanner s = new Scanner(email).useDelimiter("@");
        String emailFront = s.next();
        emailFront = emailFront.replace(".", "");
        privateBucketName = emailFront + "nspj";
//        String bucketname="hugochiaxyznspj";
        Page<Blob> blobList = storage.list(privateBucketName);
        for (Blob blob : blobList.iterateAll()) {
//            BlobList.add(new MyBlob(blob));
            blobs.add(new TableBlob(blob.getName(),convertTime(blob.getCreateTime())));
        }


        JFXTreeTableColumn<TableBlob, String> fileColumn = new JFXTreeTableColumn<>("File Name");
        fileColumn.setPrefWidth(522);
        fileColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBlob, String> param) -> {
            if (fileColumn.validateValue(param)) {
                return param.getValue().getValue().blobName;
            } else {
                return fileColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<TableBlob, String> dateColumn = new JFXTreeTableColumn<>("Date");
        dateColumn.setPrefWidth(522);
        dateColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBlob, String> param) -> {
            if (dateColumn.validateValue(param)) {
                return param.getValue().getValue().date;
            } else {
                return dateColumn.getComputedValue(param);
            }
        });

        dateColumn.setCellFactory((TreeTableColumn<TableBlob, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        dateColumn.setOnEditCommit((CellEditEvent<TableBlob, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                .getValue().date.set(t.getNewValue()));

        fileColumn.setCellFactory((TreeTableColumn<TableBlob, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        fileColumn.setOnEditCommit((CellEditEvent<TableBlob, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                .getValue().blobName.set(t.getNewValue()));

        final TreeItem<TableBlob> root = new RecursiveTreeItem<>(blobs, RecursiveTreeObject::getChildren);

        JFXTreeTableView = new JFXTreeTableView<>(root);
        JFXTreeTableView.setShowRoot(false);
        JFXTreeTableView.setEditable(true);
        JFXTreeTableView.getColumns().setAll(fileColumn, dateColumn);
        TableAnchorPane.getChildren().add(JFXTreeTableView);

//        JFXTreeTableView<TableBlob> treeView = new JFXTreeTableView<>(root);
//        treeView.setShowRoot(false);
//        treeView.setEditable(true);
//        treeView.getColumns().setAll(fileColumn, dateColumn);
//        JFXTreeTableView.getChildren().add(JFXTreeTableView);

//        FlowPane main = new FlowPane();
//        main.setPadding(new Insets(10));
//        anchorPane.getChildren().add(JFXTreeTableView);

        filterField.textProperty().addListener((o, oldVal, newVal) -> {
            JFXTreeTableView.setPredicate(userProp -> {
                final TableBlob blob = userProp.getValue();
                return blob.blobName.get().toLowerCase().contains(newVal.toLowerCase())
                        || blob.date.get().toLowerCase().contains(newVal.toLowerCase())
//                        || blob.blobName.get().toUpperCase().contains(newVal)
//                        || blob.date.get().toUpperCase().contains(newVal)
                        || blob.blobName.get().contains(newVal)
                        || blob.date.get().contains(newVal);
            });
        });

        JFXTreeTableView.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (e.isSecondaryButtonDown()){
                onEdit();
            }
        });
    }

    private void onEdit(){
        if(JFXTreeTableView.getSelectionModel().getSelectedItem() != null){
            TableBlob tableBlob=JFXTreeTableView.getSelectionModel().getSelectedItem().getValue();
            System.out.println(tableBlob.getBlobName());
            System.out.println(tableBlob.getDate());
        }
    }

    private static final class TableBlob extends RecursiveTreeObject<TableBlob> {
        final StringProperty blobName;
        final StringProperty date;

        TableBlob(String blobName, String date) {
            this.blobName = new SimpleStringProperty(blobName);
            this.date = new SimpleStringProperty(date);
        }

        public String getBlobName() {
            return blobName.get();
        }

        public StringProperty blobNameProperty() {
            return blobName;
        }

        public String getDate() {
            return date.get();
        }

        public StringProperty dateProperty() {
            return date;
        }
    }

    public void hamburgerBar() {
        rootP = anchorPane;

        try {
            VBox box = FXMLLoader.load(getClass().getResource("UserSideTab.fxml"));
            drawer.setSidePane(box);
            drawer.setVisible(false);
            drawer.setDefaultDrawerSize(219);
        } catch (IOException ex) {
            Logger.getLogger(ControllerSecureCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
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
