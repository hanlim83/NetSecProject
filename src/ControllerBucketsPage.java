import Model.StorageSnippets;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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
    private JFXTextField bucketName;

    @FXML
    private JFXButton enterButton;

    @FXML
    private TableView bucketsTable;

    @FXML
    private TableColumn tableColBucketName;

//    @FXML
//    private TableColumn<StorageSnippets, Integer> deleteBucket;

    private Scene myScene;

    public static AnchorPane rootP;

    ArrayList<String> listedbuckets;
    StorageSnippets storagesnippets = new StorageSnippets();
    private ObservableList bucketList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
//        tableColBucketName.setCellValueFactory(new PropertyValueFactory<StorageSnippets, String>("BUCKETS"));
    }


    @FXML
    void handleCreateBuckets(MouseEvent event) {
        CreatingBuckets.setVisible(true);
    }

    @FXML
    void handleListBuckets(MouseEvent event) {
        listedbuckets = new ArrayList();
        listedbuckets = storagesnippets.listBuckets();

        String BUCKETS = String.join("\n", listedbuckets);
        listedBuckets.setText(BUCKETS);

    }

    String errorMessage = "";
    String successfulMessage = "";

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
        //reupdate the arraylist of buckets
        storagesnippets.listBuckets();
        //reupdate tableview
        bucketList = FXCollections.observableArrayList(listedbuckets);
        bucketsTable.setItems(bucketList);

        if (errorMessage.equals("")) {
            successfulMessage = "Successful Creation - Bucket has been created";
            errorMessagePopOut(anchorPane.getScene(), successfulMessage, "Close");
        }
        //else SUCCESSFUL POP UP
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
