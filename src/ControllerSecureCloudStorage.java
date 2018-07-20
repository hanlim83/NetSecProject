import Model.*;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private JFXButton UploadButton;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private HBox FileButtonsHbox;

    @FXML
    private JFXTextField filterField;

    private Scene myScene;

    public static AnchorPane rootP;

    private Credential credential;
    private OAuth2Login login = new OAuth2Login();

    private String privateBucketName;
    private ObservableList<TableBlob> blobs;
//    private ArrayList<MyBlob> BlobList = new ArrayList<MyBlob>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        try {
            credential = login.login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void passData(ObservableList<TableBlob> blobs) {
        this.blobs = blobs;
        TableMethod();
    }

    @FXML
    void onClickUploadButton(ActionEvent event) {
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
//        downloadFile(storage,"hugochiaxyznspj","42149.py",saveFile());
//        deleteFile("hugochiaxyznspj","42149.py");
        calculateEmail();
        UploadFileTest();
        updateObservableList();
        TableMethod();
    }

    public String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat(" dd/MM/yyyy HH:mm:ss");
        return format.format(date);
    }

    private Path saveFile() {
        FileChooser fileChooser = new FileChooser();
        File filePath = fileChooser.showSaveDialog(null);
        String filePathString = filePath.getAbsolutePath();
        Path path = Paths.get(filePathString);
//        fileChooser.setTitle("Save Image");
////        System.out.println(pic.getId());
//        File file = fileChooser.showSaveDialog(null);
//        return Paths.get(file.getName());
        return path;
    }

    public void UploadFileTest() {
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
            if (bucket.toString().contains(privateBucketName)) {
                System.out.println(bucket.toString());
                File initialFile = new File(AbsolutePath);
                InputStream targetStream = new FileInputStream(initialFile);
//            InputStream content = new ByteArrayInputStream("Hello, World!".getBytes(UTF_8));
                Blob blob = bucket.create(filename, targetStream, "text/plain");
            }
        }
    }

    //To test
    private void downloadFile(Storage storage, String bucketName, String objectName, Path downloadTo) throws IOException {
        BlobId blobId = BlobId.of(bucketName, objectName);
        Blob blob = storage.get(blobId);
        if (blob == null) {
            System.out.println("No such object");
            return;
        }
        PrintStream writeTo = System.out;
        if (downloadTo != null) {
            writeTo = new PrintStream(new FileOutputStream(downloadTo.toFile()));
        }
        if (blob.getSize() < 1_000_000) {
            // Blob is small read all its content in one request
            byte[] content = blob.getContent();
            writeTo.write(content);
        } else {
            // When Blob size is big or unknown use the blob's channel reader.
            try (ReadChannel reader = blob.reader()) {
                WritableByteChannel channel = Channels.newChannel(writeTo);
                ByteBuffer bytes = ByteBuffer.allocate(64 * 1024);
                while (reader.read(bytes) > 0) {
                    bytes.flip();
                    channel.write(bytes);
                    bytes.clear();
                }
            }
        }
        if (downloadTo == null) {
            writeTo.println();
        } else {
            writeTo.close();
        }
    }

    public void deleteFile(String bucketName, String blobName) {
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
        BlobId blobId = BlobId.of(bucketName, blobName);
        boolean deleted = storage.delete(blobId);
        if (deleted)

        {
            // the blob was deleted
            System.out.println("Deleted");
        } else

        {
            // the blob was not found
            System.out.println("Not deleted not found");
        }
    }

    private int entry1;
    private String entryid;

    public void calculateEmail() {
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
    }

    private void updateObservableList(){
        blobs.clear();
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
        String privateBucketName = emailFront + "nspj";
//        String bucketname="hugochiaxyznspj";
        Page<Blob> blobList = storage.list(privateBucketName);
        for (Blob blob : blobList.iterateAll()) {
//            BlobList.add(new MyBlob(blob));
            blobs.add(new ControllerSecureCloudStorage.TableBlob(blob.getName(), convertTime(blob.getCreateTime())));
        }
    }

    private void TableMethod() {
//        blobs = FXCollections.observableArrayList();
////        users.add(new TreeTableDemo.User(COMPUTER_DEPARTMENT, "23", "CD 1"));
////        users.add(new TreeTableDemo.User(SALES_DEPARTMENT, "22", "Employee 1"));
////        users.add(new TreeTableDemo.User(SALES_DEPARTMENT, "24", "Employee 2"));
////        users.add(new TreeTableDemo.User(SALES_DEPARTMENT, "25", "Employee 4"));
////        users.add(new TreeTableDemo.User(SALES_DEPARTMENT, "27", "Employee 5"));
////        users.add(new TreeTableDemo.User(IT_DEPARTMENT, "42", "ID 2"));
////        users.add(new TreeTableDemo.User(HR_DEPARTMENT, "21", "HR 1"));
////        users.add(new TreeTableDemo.User(HR_DEPARTMENT, "28", "HR 2"));
//        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
//        String email = null;
//        try {
//            email = login.getEmail();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Scanner s = new Scanner(email).useDelimiter("@");
//        String emailFront = s.next();
//        emailFront = emailFront.replace(".", "");
//        privateBucketName = emailFront + "nspj";
////        String bucketname="hugochiaxyznspj";
//        Page<Blob> blobList = storage.list(privateBucketName);
//        for (Blob blob : blobList.iterateAll()) {
////            BlobList.add(new MyBlob(blob));
//            blobs.add(new TableBlob(blob.getName(), convertTime(blob.getCreateTime())));
//        }


        JFXTreeTableColumn<TableBlob, String> fileColumn = new JFXTreeTableColumn<>("File Name");
        fileColumn.setPrefWidth(525);
        fileColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBlob, String> param) -> {
            if (fileColumn.validateValue(param)) {
                return param.getValue().getValue().blobName;
            } else {
                return fileColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<TableBlob, String> dateColumn = new JFXTreeTableColumn<>("Date");
        dateColumn.setPrefWidth(350);
        dateColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBlob, String> param) -> {
            if (dateColumn.validateValue(param)) {
                return param.getValue().getValue().date;
            } else {
                return dateColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<TableBlob, String> settingsColumn = new JFXTreeTableColumn<>("Others");
        settingsColumn.setPrefWidth(175);
        Callback<TreeTableColumn<TableBlob, String>, TreeTableCell<TableBlob, String>> cellFactory
                =
                new Callback<TreeTableColumn<TableBlob, String>, TreeTableCell<TableBlob, String>>() {
                    @Override
                    public TreeTableCell call(final TreeTableColumn<TableBlob, String> param) {
                        final TreeTableCell<TableBlob, String> cell = new TreeTableCell<TableBlob, String>() {

                            JFXButton btn = new JFXButton("Others");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
//                                    btn.setButtonType(JFXButton.ButtonType.RAISED);
                                    btn.setOnAction(new EventHandler<ActionEvent>() {

                                        @Override
                                        public void handle(ActionEvent t) {
                                            int selectdIndex = getTreeTableRow().getIndex();
                                            System.out.println(selectdIndex);
                                            TableBlob tableBlob = JFXTreeTableView.getSelectionModel().getModelItem(selectdIndex).getValue();
                                            System.out.println(tableBlob.getBlobName());
                                            blobName=tableBlob.getBlobName();
                                            System.out.println(tableBlob.getDate());
                                            Bounds boundsInScene = btn.localToScene(btn.getBoundsInLocal());
                                            showVbox(boundsInScene.getMinX(), boundsInScene.getMaxY());
                                        }
                                    });


//                                    btn.setOnAction(event -> {
//                                        int selectdIndex = getTableRow().getIndex();
////                                        Record selectedRecord = (Record)tblView.getItems().get(selectdIndex);
//                                        if (JFXTreeTableView.getSelectionModel().getSelectedItem() != null) {
//                                            TableBlob tableBlob = JFXTreeTableView.getSelectionModel().getSelectedItem().getValue();
//                                            System.out.println(tableBlob.getBlobName());
//                                            System.out.println(tableBlob.getDate());
//                                        }
////                                        System.out.println(JFXTreeTableView.getSelectionModel().getSelectedItem().getParent().getValue().getBlobName());
////                                        System.out.println(JFXTreeTableView.getSelectionModel().getSelectedItem().getValue().getBlobName());
////                                        TableBlob person = getTableView().getItems().get(getIndex());
////                                        System.out.println(person.getFirstName()
////                                                + "   " + person.getLastName());
////                                        calculateEmail();
////                                        deleteFile(privateBucketName,);
//                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        settingsColumn.setCellFactory(cellFactory);

//        settingsColumn.setCellFactory(JFXButtonTableCell.<TableBlob>forTableColumn("More", (TableBlob tableBlob) -> {
//            //get entry id first
//            TableBlob tableBlob1=tableBlob;
////            osversion(tableBlob);
////            entry1 = OSVERSIONS.getEntryID();
////
////            doubleConfirm = "This selected OS Version \"" + OSVERSIONS.getVersionName()+ "\" will be removed from the cloud. Are you sure to delete it?";
////            doubleConfirmation(anchorPane.getScene(), doubleConfirm, "No", "Yes");
////            CHECKING=checker2;
////            System.out.println("CHECKER NOW IS " + CHECKING);
////
////            System.out.println("Entry id " + entry1);
////            entryid = Integer.toString(entry1);
//
//            return tableBlob1;
//        }));

//        TableBlob tableBlob=new TableBlob();

//        settingsColumn.setCellFactory(ActionButtonTableCell.<OSVersion>forTableColumn("Revoke", (OSVersion OSVERSIONS) -> {
//            //get entry id first
//            osversion(OSVERSIONS);
//            entry1 = OSVERSIONS.getEntryID();
//
////            doubleConfirm = "This selected OS Version \"" + OSVERSIONS.getVersionName()+ "\" will be removed from the cloud. Are you sure to delete it?";
////            doubleConfirmation(anchorPane.getScene(), doubleConfirm, "No", "Yes");
////            CHECKING=checker2;
////            System.out.println("CHECKER NOW IS " + CHECKING);
//
//            System.out.println("Entry id " + entry1);
//            entryid = Integer.toString(entry1);
//
//            return OSVERSIONS;
//        }));

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
        JFXTreeTableView.getColumns().setAll(fileColumn, dateColumn, settingsColumn);
        TableAnchorPane.getChildren().add(JFXTreeTableView);

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
            if (e.isPrimaryButtonDown()) {
                onEdit();
            }
        });
    }

    VBox vBox = new VBox();
    private JFXButton jfxDownloadButton = new JFXButton();
    private JFXButton jfxDeleteButton = new JFXButton();
    private int vBoxCounter = 0;

    double minX;
    double maxY;

    private String blobName;

    private void showVbox(double minX, double maxY) {
        double minWidth = 100;
        double minHeight = 200;
        this.minX = minX;
        this.maxY = maxY;
        if (vBoxCounter == 0) {
            vBox.setLayoutX(minX);
            vBox.setLayoutY(maxY);
            vBox.setMinSize(minWidth, minHeight);
            Background unfocusBackground = new Background(new BackgroundFill(Color.web("#F4F4F4"), CornerRadii.EMPTY, Insets.EMPTY));
            vBox.setBackground(unfocusBackground);
            jfxDownloadButton.setText("Download");
            jfxDownloadButton.setMinSize(vBox.getMinWidth(), vBox.getMinHeight() / 2);
            jfxDownloadButton.setOnAction(__ -> {
                //Download File
                Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
                System.out.println("Download File");
                calculateEmail();
                try {
                    downloadFile(storage,privateBucketName,blobName,saveFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                myScene = anchorPane.getScene();
//                Stage stage = (Stage) (myScene).getWindow();
//
//                String title = "";
//                String content = "Are you sure you want to delete this file?";
//
//                JFXButton close = new JFXButton("Ok");
//
//                close.setButtonType(JFXButton.ButtonType.RAISED);
//
//                close.setStyle("-fx-background-color: #00bfff;");
//
//                JFXDialogLayout layout = new JFXDialogLayout();
//                layout.setHeading(new Label(title));
//                layout.setBody(new Label(content));
//                layout.setActions(close);
//                JFXAlert<Void> alert = new JFXAlert<>(stage);
//                alert.setOverlayClose(true);
//                alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
//                alert.setContent(layout);
//                alert.initModality(Modality.NONE);
//                close.setOnAction(___ -> {alert.hideWithAnimation();
//                    calculateEmail();
//                    deleteFile(privateBucketName,blobName);
//                    updateObservableList();
//                    TableMethod();});
//                alert.show();
            });


            jfxDeleteButton.setText("Delete");
            jfxDeleteButton.setMinSize(minWidth, vBox.getMinHeight() / 2);

            //Update this to show confirmation pop-up
            jfxDeleteButton.setOnAction(__ -> {
                System.out.println("ONCLICK DELETE File");
                myScene = anchorPane.getScene();
                Stage stage = (Stage) (myScene).getWindow();

                String title = "";
                String content = "Are you sure you want to delete this file?";

                JFXButton close = new JFXButton("Ok");

                close.setButtonType(JFXButton.ButtonType.RAISED);

                close.setStyle("-fx-background-color: #00bfff;");

                JFXDialogLayout layout = new JFXDialogLayout();
                layout.setHeading(new Label(title));
                layout.setBody(new Label(content));
                layout.setActions(close);
                JFXAlert<Void> alert = new JFXAlert<>(stage);
                alert.setOverlayClose(true);
                alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
                alert.setContent(layout);
                alert.initModality(Modality.NONE);
                close.setOnAction(___ -> {alert.hideWithAnimation();
                calculateEmail();
                deleteFile(privateBucketName,blobName);
                updateObservableList();
                TableMethod();});
                alert.show();
            });

            vBox.getChildren().addAll(jfxDownloadButton, jfxDeleteButton);
//            vBox.getChildren().add(jfxDeleteButton);
            anchorPane.getChildren().add(vBox);
            vBoxCounter++;
//            vBox.setFocusTraversable(true);
//            vBox.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
//                if (isNowFocused) {
//                    vBox.setVisible(false);
//                }
//            });
            vBox.setVisible(true);
        } else {
            vBox.setLayoutX(minX);
            vBox.setLayoutY(maxY);
//            vBox.setMinSize(100, 200);
            Background unfocusBackground = new Background(new BackgroundFill(Color.web("#F4F4F4"), CornerRadii.EMPTY, Insets.EMPTY));
            vBox.setBackground(unfocusBackground);
            vBox.setVisible(true);
        }
        myScene = anchorPane.getScene();
        myScene.addEventFilter(MouseEvent.MOUSE_PRESSED, closeVbox);
    }

    private EventHandler<MouseEvent> closeVbox = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            System.out.println("mouse click detected! " + mouseEvent.getSource());
            System.out.println(minX + " " + maxY);
            if (mouseEvent.getX() >= minX && mouseEvent.getX() <= minX + 100 && mouseEvent.getY() >= maxY && mouseEvent.getY() <= maxY + 200) {
                System.out.println("Inside the vbox");
            } else {
                vBox.setVisible(false);
                myScene.removeEventFilter(MouseEvent.MOUSE_PRESSED, closeVbox);
            }
        }
    };

    private void onEdit() {
        FileButtonsHbox.setVisible(true);
        if (JFXTreeTableView.getSelectionModel().getSelectedItem() != null) {
            TableBlob tableBlob = JFXTreeTableView.getSelectionModel().getSelectedItem().getValue();
            System.out.println(tableBlob.getBlobName());
            System.out.println(tableBlob.getDate());
        }
    }

    public static final class TableBlob extends RecursiveTreeObject<TableBlob> {
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

//    /**
//     * A custom cell that shows a checkbox, label and button in the
//     * TreeCell.
//     */
//    class CustomCell extends TreeTableCell<String> {
//        @Override
//        protected void updateItem(String item, boolean empty) {
//            super.updateItem(item, empty);
//
//            // If the cell is empty we don't show anything.
//            if (isEmpty()) {
//                setGraphic(null);
//                setText(null);
//            } else {
//                // We only show the custom cell if it is a leaf, meaning it has
//                // no children.
//                if (this.getTreeTableItem().isLeaf()) {
//
//                    // A custom HBox that will contain your check box, label and
//                    // button.
//                    HBox cellBox = new HBox(10);
//
////                    CheckBox checkBox = new CheckBox();
////                    Label label = new Label(item);
//                    JFXButton button = new JFXButton("Press!");
//                    // Here we bind the pref height of the label to the height of the checkbox. This way the label and the checkbox will have the same size.
////                    label.prefHeightProperty().bind(checkBox.heightProperty());
//
//                    cellBox.getChildren().addAll(button);
//
//                    // We set the cellBox as the graphic of the cell.
//                    setGraphic(cellBox);
//                    setText(null);
//                } else {
//                    // If this is the root we just display the text.
//                    setGraphic(null);
//                    setText(item);
//                }
//            }
//        }
//    }

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
