import Model.MyBlob;
import Model.OAuth2Login;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
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
import javafx.geometry.Insets;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerSecureCloudStorage implements Initializable {
    @FXML
    private AnchorPane anchorPane;

//    @FXML
//    private JFXTreeView<?> JFXTreeView;

    @FXML
    private com.jfoenix.controls.JFXTreeTableView<TableBlob> JFXTreeTableView;

    @FXML
    private AnchorPane TableAnchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    private Scene myScene;

    public static AnchorPane rootP;

    private Credential credential;
    private OAuth2Login login = new OAuth2Login();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        try {
            credential = login.login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private JFXButton TestButton;

    ObservableList<TableBlob> blobs;
    ArrayList<MyBlob> BlobList = new ArrayList<MyBlob>();
    @FXML
    void onClickTestButton(ActionEvent event) throws IOException {
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
        String email = login.getEmail();
        Scanner s = new Scanner(email).useDelimiter("@");
        String emailFront = s.next();
        // Extra algo
        emailFront = emailFront.replace(".", "");
        // Extra algo
        String bucketname = emailFront + "nspj";
//        String bucketname="hugochiaxyznspj";
        Page<Blob> blobList = storage.list(bucketname);
        for (Blob blob : blobList.iterateAll()) {
//            BlobList.add(new MyBlob(blob));
            blobs.add(new TableBlob(blob.getName(),blob.getCreateTime().toString()));
        }

        blobs.add(new TableBlob("Testing 1", "7/8/2018"));
        blobs.add(new TableBlob("Testing 2", "7/7/2018"));
        TableMethod();

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
    }

    private void TableMethod() {
        JFXTreeTableColumn<TableBlob, String> fileColumn = new JFXTreeTableColumn<>("File Name");
        fileColumn.setPrefWidth(301);
        fileColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBlob, String> param) -> {
            if (fileColumn.validateValue(param)) {
                return param.getValue().getValue().blobName;
            } else {
                return fileColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<TableBlob, String> dateColumn = new JFXTreeTableColumn<>("Date");
        dateColumn.setPrefWidth(301);
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
    }

    private static final class TableBlob extends RecursiveTreeObject<TableBlob> {
        final StringProperty blobName;
        final StringProperty date;

        TableBlob(String blobName, String date) {
            this.blobName = new SimpleStringProperty(blobName);
            this.date = new SimpleStringProperty(date);
        }
    }

    public String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
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
