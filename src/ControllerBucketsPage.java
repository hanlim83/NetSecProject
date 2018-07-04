import Model.*;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//*HERE SINCE 4.52PM
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

    @FXML
    private JFXButton deletingBuckets;

    @FXML
    private JFXTextField bucketName;

    @FXML
    private JFXButton enterButton;

    @FXML
    private TableView<CloudBuckets> bucketsTable1;

    @FXML
    private TableColumn<CloudBuckets, String> tableColBucketName1;

    @FXML
    private TableColumn<CloudBuckets, Button> deleteBuckets1;

    @FXML
    private TableView<CloudBuckets> bucketsTable;

    @FXML
    private TableColumn<CloudBuckets, String> tableColBucketName;

    @FXML
    private TableColumn<CloudBuckets, Button> deleteBuckets;

    private Scene myScene;

    public static AnchorPane rootP;

    private ArrayList<CloudBuckets> objectArrayList;
    private ObservableList<CloudBuckets> objectList;
    StorageSnippets storagesnippets;
    CloudBuckets cldB2 = new CloudBuckets();


    String errorMessage = "";
    String successfulMessage = "";
    int checker = 0;
    String doubleConfirm = "";
    int checker2;
    int CHECKING;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        tableColBucketName.setCellValueFactory(new PropertyValueFactory<CloudBuckets, String>("bucketName"));
        tableColBucketName1.setCellValueFactory(new PropertyValueFactory<CloudBuckets, String>("bucketName"));
        storagesnippets = new StorageSnippets();
        storagesnippets.listBuckets();
        objectArrayList = storagesnippets.getCloudbucketsList();
//        CHECKING=-1;
//        deleteBuckets1.setCellFactory(ActionButtonTableCell.<CloudBuckets>forTableColumn("Remove", (CloudBuckets cldB) -> {//DO A ARE U SURE TO DELETE THIS BUCKET CONFIRMATION
//            CHECKING=checker2;
//            bucketsTable1.getItems().remove(cldB);
//            String allBucketNames = "";
//            System.out.println("DELETING THIS BUCKET: " + cldB.getBucketName());
//            String NAMEBUCKET = cldB.getBucketName().substring(12, cldB.getBucketName().length() - 1);
//            System.out.println(NAMEBUCKET);
//            checker = storagesnippets.deleteGcsBucket(NAMEBUCKET);
//        return cldB;
//        }));
        }

    @FXML
    void handleCreateBuckets(MouseEvent event) {
        CreatingBuckets.setVisible(true);
    }

    public CloudBuckets cldBucket(CloudBuckets cldB){
        cldB2 = cldB;
        return cldB2;
    }

    @FXML
    void handleDeleteBuckets(MouseEvent event) {
        CHECKING=-1;
        //TABLE OF BUCKETS
        objectList = FXCollections.observableList(objectArrayList);
        bucketsTable1.setItems(objectList);

        //DELETION OF BUCKETS
        deleteBuckets1.setCellFactory(ActionButtonTableCell.<CloudBuckets>forTableColumn("Remove", (CloudBuckets cldB) -> {
            cldBucket(cldB);
            //DO A ARE U SURE TO DELETE THIS BUCKET CONFIRMATION
            doubleConfirm = "This selected bucket " + cldB.getBucketName().substring(12, cldB.getBucketName().length() - 1) + " will be permanently deleted from the cloud. Are you sure to delete it?";
            doubleConfirmation(anchorPane.getScene(), doubleConfirm, "No", "Yes");
            CHECKING=checker2;
            System.out.println("CHECKER NOW IS " + CHECKING);

//            if (CHECKING == 1) {
//                bucketsTable1.getItems().remove(cldB);
//                String allBucketNames = "";
//                System.out.println("DELETING THIS BUCKET: " + cldB.getBucketName());
//                String NAMEBUCKET = cldB.getBucketName().substring(12, cldB.getBucketName().length() - 1);
//                System.out.println(NAMEBUCKET);
//                checker = storagesnippets.deleteGcsBucket(NAMEBUCKET);
//                System.out.println("CHECKER IS : " + checker);
//                System.out.println("Deleted :" + NAMEBUCKET);
//                System.out.println("THE CHECKER IS NOW : " + storagesnippets.getChecker());
//            }



            return cldB;

        }));


        System.out.println("After success msg");
    }

    @FXML
    void handleListBuckets(MouseEvent event) {
        String allBucketNames = "";
        for (CloudBuckets b : objectArrayList) {
            allBucketNames = allBucketNames + "\n" + b.getBucketName();
        }
        listedBuckets.setText(allBucketNames);
    }


    private boolean checkEligible(String bucketname) {
        System.out.println("Checking if bucket name is accepted...");
        //The bucket name should at least be 3 to 63 characters (DONE)
        //start and end with a number or letter. (DONE)
        //Bucket names must contain only lowercase letters (DONE)
        // numbers, dashes (-), underscores (_), and dots (.). (DONE)
        //Bucket names cannot begin with the "goog" prefix. (DONE)
        //Bucket names cannot contain "google" or close misspellings, such as "g00gle". (DONE)
        boolean hasUppercase = !bucketname.equals(bucketname.toLowerCase());
        final String SPECIAL_CHARACTERS = " .[,~,!,@,#,$,%,^,&,,(,),-,_,/,=,+,[,{,],},|,;,:,<,>,/,?].*$)\"\'\\ ";
        final String FIRST_SPECIAL = "goog";
        Pattern pattern = Pattern.compile("^[^<>{}\"/|;:,~!?@#$%^=&*\\]\\\\()\\[¿§«»ω⊙¤°℃℉€¥£¢¡®©+]*$");
        Matcher matcher = pattern.matcher(bucketname);
//        Pattern pattern1 = Pattern.compile("google,g00gle,g00g1e,goog1e,g0ogle,go0gle,g0og1e,go0g1e,g00g,g0og,go0g");
//        Matcher matcher1 = pattern1.matcher(bucketname);

        if ((bucketname.length() < 3) || (bucketname.length() > 63)) {
            errorMessage = "Invalid bucket name - Too short / Too Long";
            System.out.println(errorMessage);
            return false;
        } else if (hasUppercase) {
            errorMessage = "Invalid bucket name - Only lowercase is accepted ";
            System.out.println(errorMessage);
            return false;
        } else if (SPECIAL_CHARACTERS.indexOf(bucketname.charAt(0)) >= 0 || SPECIAL_CHARACTERS.indexOf(bucketname.charAt(bucketname.length() - 1)) >= 0) {
            errorMessage = "Invalid bucket name - Must start or end with a number / character";
            System.out.println(errorMessage);
            return false;
        } else if (!matcher.matches()) {
            errorMessage = "Invalid bucket name - Name should only contains letters, number, - , _ and . ";
            System.out.println(errorMessage);
            return false;
        } else if (bucketname.substring(0, 4).matches("goog")) {
            errorMessage = "Name cannot begin with goog or have any reference to google";
            System.out.println(errorMessage);
            return false;
        } else if (bucketname.substring(0, 6).matches("g00gle")) {
            errorMessage = "Name cannot contain google, g00gle, or other prefixes";
            System.out.println(errorMessage);
            return false;
        } else {
            return true;
        }
    }


    @FXML
    void handleEnter(MouseEvent event) {
        String bucketname = bucketName.getText();
        System.out.println(bucketname);

        //Checking for eligibilty - NOT ACCEPTED
        try {
            storagesnippets.createBucketWithStorageClassAndLocation(bucketname);
        } catch (com.google.cloud.storage.StorageException e) {
            errorMessage = "-";
            checkEligible(bucketname);
            if (errorMessage.equals("-")) {
                errorMessage = "Bucket with this name already exist";
                System.out.println(errorMessage);
                errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            } else {
                System.out.println(errorMessage);
                JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                snackbar.show(errorMessage, 3000);
                errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            }
        }

        if (errorMessage.equals("")) {
            objectArrayList = storagesnippets.getCloudbucketsList();
            successfulMessage = "Successful Creation - Bucket has been created";
            successfulMessage(anchorPane.getScene(), successfulMessage, "Close");
        }

        //TABLE OF BUCKETS
        objectList = FXCollections.observableList(objectArrayList);
        bucketsTable.setItems(objectList);

//        //DELETION OF BUCKETS
//        deleteBuckets.setCellFactory(ActionButtonTableCell.<CloudBuckets>forTableColumn("Remove", (CloudBuckets cldB) -> {
//            bucketsTable.getItems().remove(cldB);
//            String allBucketNames = "";
//            System.out.println("DELETING THIS BUCKET: " + cldB.getBucketName());
//            String NAMEBUCKET = cldB.getBucketName().substring(12, cldB.getBucketName().length() - 1);
//            System.out.println(NAMEBUCKET);
//            checker = storagesnippets.deleteGcsBucket(NAMEBUCKET);
//            System.out.println("CHECKER IS : " + checker);
//            System.out.println("Deleted :" + NAMEBUCKET);
//            return cldB;
//        }));

//        if (checker == 1) {
//            successfulMessage = "Bucket Deleted Successfully";
//            successfulMessage(anchorPane.getScene(), successfulMessage, "Close");
//        }


    }


    private void doubleConfirmation(Scene scene, String doubleconfirm, String buttonContent, String buttonContent2) {
        checker2=-1;
        myScene = scene;
        Stage stage = (Stage) (myScene).getWindow();

        String message = doubleconfirm;
        String title = "Are you sure?";

        JFXButton no = new JFXButton(buttonContent);
        no.setButtonType(JFXButton.ButtonType.RAISED);
        no.setStyle("-fx-background-color: #00bfff;");

        JFXButton yes = new JFXButton(buttonContent2);
        yes.setButtonType(JFXButton.ButtonType.RAISED);
        yes.setStyle("-fx-background-color: #ff2828");


        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(title));
        layout.setBody(new Label(message));

        layout.setActions(no, yes);

        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);

        //GET WHETHER PRESS YES or NO
        yes.setOnAction(__addEvent -> {
            checker2 = 1;
            CHECKING = -1;
            System.out.println("YES IS PRESSED, CHECKER2 is " + checker2);

//        deleteBuckets1.setCellFactory(ActionButtonTableCell.<CloudBuckets>forTableColumn("Remove", (CloudBuckets cldB) -> {//DO A ARE U SURE TO DELETE THIS BUCKET CONFIRMATION

            CHECKING=checker2;
            bucketsTable1.getItems().remove(cldB2);
            String allBucketNames = "";
            System.out.println("DELETING THIS BUCKET: " + cldB2.getBucketName());
            String NAMEBUCKET = cldB2.getBucketName().substring(12, cldB2.getBucketName().length() - 1);
            System.out.println(NAMEBUCKET);
            checker = storagesnippets.deleteGcsBucket(NAMEBUCKET);
//        return cldB2;
//        }));

            successfulMessage = "Bucket Deleted Successfully";
            successfulMessage(anchorPane.getScene(), successfulMessage, "Close");
            alert.hideWithAnimation();
        });
        no.setOnAction(__addEvent -> {
            checker2 = 0;
            System.out.println("NO IS PRESSED, CHECKER2 is " + checker2);
            errorMessage = "Bucket was not deleted successfully";
            errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            alert.hideWithAnimation();
        });
        alert.show();


    }

    private void successfulMessage(Scene scene, String successfulMessage, String buttonContent) {
        myScene = scene;
        Stage stage = (Stage) (myScene).getWindow();

        String message = successfulMessage;
        String title = "Success";
        JFXButton close = new JFXButton(buttonContent);

        close.setButtonType(JFXButton.ButtonType.RAISED);

        close.setStyle("-fx-background-color: #00bfff;");

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(title));
        layout.setBody(new Label(message));
        layout.setActions(close);
        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);
        close.setOnAction(__ -> alert.hideWithAnimation());
        alert.show();
    }

    private void errorMessagePopOut(Scene scene, String errorMessage, String buttonContent) {
        myScene = scene;
        Stage stage = (Stage) (myScene).getWindow();

        String message = errorMessage;
        String title = "ERROR";
        JFXButton close = new JFXButton(buttonContent);

        close.setButtonType(JFXButton.ButtonType.RAISED);

        close.setStyle("-fx-background-color: #00bfff;");

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(title));
        layout.setBody(new Label(message));
        layout.setActions(close);
        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);
        close.setOnAction(__ -> alert.hideWithAnimation());
        alert.show();
    }


    @FXML
    void handleExit(MouseEvent event) {
        CreatingBuckets.setVisible(false);
    }

    public void hamburgerBar() {
        rootP = anchorPane;

        try {
            VBox box = FXMLLoader.load(getClass().getResource("AdminSideTab.fxml"));
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
