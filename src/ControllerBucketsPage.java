import Model.*;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;
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
    private TableView<CloudBuckets> bucketsTable1;

    @FXML
    private TableColumn<CloudBuckets, String> tableColBucketName1;

    @FXML
    private TableColumn<CloudBuckets, Button> editBuckets1;

    @FXML
    private TableColumn<CloudBuckets, Button> deleteBuckets1;

    @FXML
    private JFXSpinner spinner;

    private Scene myScene;

    public static AnchorPane rootP;

    private String myIPAddress;
    private boolean ipChecker;

    private ArrayList<CloudBuckets> objectArrayList;
    private ObservableList<CloudBuckets> objectList;
    StorageSnippets storagesnippets;
    CloudBuckets cldB2 = new CloudBuckets();

    String inputBUCKETNAME;
    String inputMEMBER;

    String errorMessage = "";
    String successfulMessage = "";
    int checker = 0;
    String doubleConfirm = "";
    int checker2;
    int CHECKING;

    BucketIamSnippets bucketiam = new BucketIamSnippets();

    Service initializeProcess = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    tableColBucketName1.setCellValueFactory(new PropertyValueFactory<CloudBuckets, String>("bucketName"));
                    storagesnippets = new StorageSnippets();
                    storagesnippets.listBuckets();
                    objectArrayList = storagesnippets.getCloudbucketsList();


                    //DELETION OF BUCKETS
                    deleteBuckets1.setCellFactory(ActionButtonTableCell.<CloudBuckets>forTableColumn("Remove", (CloudBuckets cldB) -> {
                        cldBucket(cldB);
                        //DO A ARE U SURE TO DELETE THIS BUCKET CONFIRMATION
                        doubleConfirm = "This selected bucket " + cldB.getBucketName().substring(12, cldB.getBucketName().length() - 1) + " will be permanently deleted from the cloud. Are you sure to delete it?";
                        doubleConfirmation(anchorPane.getScene(), doubleConfirm, "No", "Yes");
                        CHECKING = checker2;
                        System.out.println("CHECKER NOW IS " + CHECKING);
                        return cldB;
                    }));

                    //EDIT BUCKETS
                    editBuckets1.setCellFactory(ActionButtonTableCell.<CloudBuckets>forTableColumn("Edit", (CloudBuckets cldB1) -> {
                        cldBucket(cldB1);
                        editMember(anchorPane.getScene(), "No", "Yes");
                        return cldB1;
                    }));



                    Platform.runLater(() -> {
                        objectList = FXCollections.observableList(objectArrayList);
                        bucketsTable1.setItems(objectList);
                    });
                    return null;
                }

            };
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        initializeProcess.start();
        spinner.setVisible(true);
        initializeProcess.setOnSucceeded(e -> {
            spinner.setVisible(false);
            initializeProcess.reset();
        });
        initializeProcess.setOnCancelled(e -> {
            initializeProcess.reset();
        });
        initializeProcess.setOnFailed(e -> {
            initializeProcess.reset();
        });
    }

    @FXML
    void handleCreateBuckets(MouseEvent event) {
        creatingBUCKETS(anchorPane.getScene(), "No", "Yes");
    }

    public CloudBuckets cldBucket(CloudBuckets cldB) {
        cldB2 = cldB;
        return cldB2;
    }

//    @FXML
//    void handleDeleteBuckets(MouseEvent event) {
//        CHECKING = -1;
//        //TABLE OF BUCKETS
//        objectList = FXCollections.observableList(objectArrayList);
//        bucketsTable1.setItems(objectList);
//
////        //DELETION OF BUCKETS
////        deleteBuckets1.setCellFactory(ActionButtonTableCell.<CloudBuckets>forTableColumn("Remove", (CloudBuckets cldB) -> {
////            cldBucket(cldB);
////            //DO A ARE U SURE TO DELETE THIS BUCKET CONFIRMATION
////            doubleConfirm = "This selected bucket " + cldB.getBucketName().substring(12, cldB.getBucketName().length() - 1) + " will be permanently deleted from the cloud. Are you sure to delete it?";
////            doubleConfirmation(anchorPane.getScene(), doubleConfirm, "No", "Yes");
////            CHECKING = checker2;
////            System.out.println("CHECKER NOW IS " + CHECKING);
////
////            return cldB;
////
////        }));
//
//
//        System.out.println("After success msg");
//    }

//    @FXML
//    void handleListBuckets(MouseEvent event) {
////        String allBucketNames = "";
////        for (CloudBuckets b : objectArrayList) {
////            allBucketNames = allBucketNames + "\n" + b.getBucketName();
////        }
////        listedBuckets.setText(allBucketNames);
//        objectList = FXCollections.observableList(objectArrayList);
//        bucketsTable1.setItems(objectList);
//    }


    private boolean checkEligible(String bucketname) {
        System.out.println("Checking if bucket name is accepted...");
        boolean hasUppercase = !bucketname.equals(bucketname.toLowerCase());
        final String SPECIAL_CHARACTERS = " .[,~,!,@,#,$,%,^,&,,(,),-,_,/,=,+,[,{,],},|,;,:,<,>,/,?].*$)\"\'\\ ";
        final String FIRST_SPECIAL = "goog";
        Pattern pattern = Pattern.compile("^[^<>{}\"/|;:,~!?@#$%^=&*\\]\\\\()\\[¿§«»ω⊙¤°℃℉€¥£¢¡®©+]*$");
        Matcher matcher = pattern.matcher(bucketname);


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


//    Service process = new Service() {
//        @Override
//        protected Task createTask() {
//            return new Task() {
//                @Override
//                protected Void call() throws Exception {
//
//
//                    Platform.runLater(() -> {
//
//                    });
//                    return null;
//                }
//            };
//        }
//    };

    @FXML
    public void clickItem(MouseEvent event) {
        try {
            if (event.getClickCount() == 2) //Checking double click
            {
                expandedDetails(anchorPane.getScene(), "Close");
            }
        } catch (com.google.cloud.logging.LoggingException e1) {
            e1.printStackTrace();
        } catch (io.grpc.StatusRuntimeException e2) {
            e2.printStackTrace();
        }
    }

    private void expandedDetails(Scene scene, String buttonContent) {
        myScene = scene;
        Stage stage = (Stage) (myScene).getWindow();
        String title = "Full-Details Bucket Information";

        JFXButton close = new JFXButton(buttonContent);
        close.setButtonType(JFXButton.ButtonType.RAISED);
        close.setStyle("-fx-background-color: #00bfff;");

        JFXDialogLayout layout = new JFXDialogLayout();
//        layout.setMaxSize(670, 350);
        layout.setPrefSize(670, 350);
//        layout.resize(670, 350);

        VBox vbox = new VBox();

        GridPane grid = new GridPane();
        grid.setPrefSize(650,330);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 100, 10, 0));
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(23);
        col1.setHalignment(HPos.LEFT);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(77);
        grid.getColumnConstraints().addAll(col1, col2);

        String BucketName = bucketsTable1.getSelectionModel().getSelectedItem().getBucketName();
        Label LABEL0 = new Label();
        String BUCKETname = BucketName.substring(12,BucketName.length()-1);
        LABEL0.setText(String.valueOf(BUCKETname));

        String string1 = "Bucket Name :";
        Label label1 = new Label();
        label1.setTextFill(Color.rgb(1, 0, 199));
        label1.setText(string1);

        grid.add(label1, 0, 0);
        grid.add(LABEL0, 1, 0);

        TitledPane firstTitledPane = new TitledPane();
        firstTitledPane.setText("Bucket Owners");

        // roles/storage.legacyBucketOwner , roles/storage.legacyBucketReader , roles/storage.legacyBucketWriter
        String bucketinfo = String.valueOf(bucketiam.listBucketIamMembers(BUCKETname));
        System.out.println(bucketinfo);
        Pattern p1 = Pattern.compile("[//]");
//        Pattern p2 = Pattern.compile("roles/storage.legacyBucketReader");
//        Pattern p3 = Pattern.compile("roles/storage.legacyBucketWriter");

        Matcher m1 = p1.matcher(bucketinfo);
//        Matcher m2 = p2.matcher(bucketinfo);
//        Matcher m3 = p3.matcher(bucketinfo);

//        if(m1.find()) {
//
//        }
//            if(m2.find()){
//                String[] first2 = bucketinfo.split(String.valueOf(p2));
//                System.out.println(first2[0]);
//            }

//        else if(m2.find()){
//            String[] second = bucketinfo.split(String.valueOf(p2));
//            for(int i=0;i<second.length;i++){
//                System.out.println(second[i]);
//            }
  //      }
//        else if(m3.find()) {
//            String[] third = bucketinfo.split(String.valueOf(p3));
//            for (int i = 0; i < third.length; i++) {
//                System.out.println(third[i]);
//            }
//        }

        VBox content1 = new VBox();
        content1.getChildren().add(new Label("INPUT 1"));
        content1.getChildren().add(new Label("INPUT 2"));
        content1.getChildren().add(new Label("INPUT 3"));
        firstTitledPane.setContent(content1);

        TitledPane secondTitledPane = new TitledPane();
        secondTitledPane.setText("Bucket Writer");

        VBox content2 = new VBox();
        content2.getChildren().add(new Label("INPUT 1"));
        content2.getChildren().add(new Label("INPUT 2"));
        secondTitledPane.setContent(content2);

        TitledPane thirdTiltedPane = new TitledPane();
        thirdTiltedPane.setText("Bucket Reader");

        VBox content3 = new VBox();
        content3.getChildren().add(new Label("INPUT 1"));
        content3.getChildren().add(new Label("INPUT 2"));
        thirdTiltedPane.setContent(content3);

        Accordion root= new Accordion();
        root.getPanes().addAll(firstTitledPane, secondTitledPane,thirdTiltedPane);
        root.setPadding(new Insets(0, 0, 70, 0));

        vbox.getChildren().addAll(new Label(title),grid);

        VBox vbox1 = new VBox();

//        String string2 = "All Information of this bucket should and must be kept private and confidential.\nDisclosure of Information may lead to legal procedures.";
//        Label label2 = new Label();
//        label2.setTextFill(Color.rgb(1, 0, 199));
//        label2.setText(string2);
//        label2.setWrapText(true);

        vbox1.getChildren().addAll(root);

        layout.setHeading(vbox);
        layout.setBody(vbox1);


        layout.setActions(close);

        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);

        close.setOnAction(__ -> alert.hideWithAnimation());

        alert.show();
    }

    private void editMember(Scene scene, String buttonContent, String buttonContent2) {
        checker2 = -1;
        myScene = scene;
        Stage stage = (Stage) (myScene).getWindow();

        String message = "\nPlease input the follow required data to edit bucket.";
        String title = "Edit Bucket Permission";

        JFXButton no = new JFXButton(buttonContent);
        no.setButtonType(JFXButton.ButtonType.RAISED);
        no.setStyle("-fx-background-color: #00bfff;");

        JFXButton yes = new JFXButton(buttonContent2);
        yes.setButtonType(JFXButton.ButtonType.RAISED);
        yes.setStyle("-fx-background-color: #ff2828");

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setPrefSize(670, 350);
        layout.setHeading(new Label(title));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 100, 10, 0));
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(15);
        col1.setHalignment(HPos.LEFT);

        Label addMembers = new Label();
        addMembers.setText("Add Members: ");

        JFXTextField inputMember = new JFXTextField();
        inputMember.setPrefWidth(250);
        inputMember.setPromptText("Enter Member's Email Here");

        grid.add(addMembers, 0, 0);
        grid.add(inputMember, 1, 0);

        String chooserole = "Choose Role: ";
        JFXComboBox<String> bucketroles = new JFXComboBox<>();
        bucketroles.getItems().addAll("Reader", "Writer", "Owner");
        grid.add(new Label(chooserole), 0, 1);
        grid.add(bucketroles, 1, 1);

//        JFXCheckBox checkbox1 = new JFXCheckBox("Grant read permission for all viewers role and above.");
//        checkbox1.setWrapText(true);
//        grid.add(checkbox1, 0, 2);

        layout.setBody(grid);

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
            CHECKING = checker2;

            inputMEMBER = inputMember.getText();
            String bucketNAME = bucketsTable1.getSelectionModel().getSelectedItem().getBucketName();
            String finalBucketName = bucketNAME.substring(12,bucketNAME.length()-1);

            String bucketROLE = bucketroles.getSelectionModel().getSelectedItem();
            String finalBucketRole = null;

            if (bucketROLE == "Reader") {
                finalBucketRole = "roles/storage.legacyBucketReader";
            } else if (bucketROLE == "Writer") {
                finalBucketRole = "roles/storage.legacyBucketWriter";
            } else if (bucketROLE == "Owner") {
                finalBucketRole = "roles/storage.legacyBucketOwner";
            }

            System.out.println("Final Bucket Role: " + finalBucketRole);
            String member="user:"+inputMEMBER;
            System.out.println("Member is " + member);
            System.out.println("BUCKET NAME IS THIS " + finalBucketName);
            bucketiam.addBucketIamMember(finalBucketName, finalBucketRole, member);

            successfulMessage = "This member, " +inputMEMBER+ " was added successfully with the role of, " +bucketROLE+ ".";
            successfulMessage(anchorPane.getScene(), successfulMessage, "Close");

            alert.hideWithAnimation();
        });
        no.setOnAction(__addEvent -> {
            checker2 = 0;
            System.out.println("NO IS PRESSED, CHECKER2 is " + checker2);
            errorMessage = "Member was not added successfully.";
            errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            alert.hideWithAnimation();
        });
        alert.show();


    }

    private void creatingBUCKETS(Scene scene, String buttonContent, String buttonContent2) {
        myScene = scene;
        Stage stage = (Stage) (myScene).getWindow();

        String message = "\nPlease input the follow required data to create buckets." + "\n" + "Viewers of project will have access to this bucket. Please indicate at the bottom";

        String title = "Creating Buckets" + "\n" + message;

        JFXButton no = new JFXButton(buttonContent);
        no.setButtonType(JFXButton.ButtonType.RAISED);
        no.setStyle("-fx-background-color: #00bfff;");

        JFXButton yes = new JFXButton(buttonContent2);
        yes.setButtonType(JFXButton.ButtonType.RAISED);
        yes.setStyle("-fx-background-color: #ff2828");

        String message1 = "Enter Bucket Name: ";


        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setPrefSize(670, 350);
        layout.setHeading(new Label(title));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 100, 10, 0));
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(15);
        col1.setHalignment(HPos.LEFT);


        JFXTextField inputBucketName = new JFXTextField();
        inputBucketName.setPrefWidth(250);
        inputBucketName.setPromptText("Type Bucket Name Here");

        grid.add(new Label(message1), 0, 0);
        grid.add(inputBucketName, 1, 0);

        JFXCheckBox checkbox1 = new JFXCheckBox("Grant read permission for all viewers role and above.");
        checkbox1.setWrapText(true);
        grid.add(checkbox1, 0, 2);

//        Label addMembers = new Label();
//        addMembers.setText("Add Members: ");
//
//        JFXTextField inputMember = new JFXTextField();
//        inputMember.setPrefWidth(250);
//        inputMember.setPromptText("Type Member Here");
//
//        grid.add(addMembers,0,2);
//        grid.add(inputMember,1,2);

//        String chooserole = "Choose Role: ";
//        JFXComboBox<String> bucketroles = new JFXComboBox<>();
//        bucketroles.getItems().addAll("Reader","Writer","Owner");
//        grid.add(new Label(chooserole), 0, 3);
//        grid.add(bucketroles, 1, 3);

        layout.setBody(grid);

        layout.setActions(no, yes);

        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);

        yes.setOnAction(__addEvent -> {
            checker2 = 1;
            CHECKING = -1;
            System.out.println("YES IS PRESSED, CHECKER2 is " + checker2);
            CHECKING = checker2;
            inputBUCKETNAME = inputBucketName.getText();
//            inputMEMBER = inputMember.getText();

            System.out.println("Bucket name is : " + inputBUCKETNAME);

            //Checking for eligibilty - NOT ACCEPTED
            try {
                //if checkbox is selected, create bucket normally/usually as all viewers allowed to view
                if (checkbox1.isSelected()) {
                    storagesnippets.createBucketWithStorageClassAndLocation(inputBUCKETNAME);

                    JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                    snackbar.getStylesheets().add("Style.css");
                    snackbar.show("Updating The Database", 3000);
                } else {
                    storagesnippets.createBucketWithStorageClassAndLocation(inputBUCKETNAME);
                    //Remove all viewers from the bucket
                    bucketiam.removeBucketIamMember(inputBUCKETNAME, "roles/storage.legacyBucketReader", "projectViewer:netsecpj");

//                    bucketiam.addBucketIamMember(inputBUCKETNAME,"roles/storage.legacyBucketReader",inputMEMBER);
                    JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                    snackbar.getStylesheets().add("Style.css");
                    snackbar.show("Updating The Database", 3000);
                }
            } catch (com.google.cloud.storage.StorageException e) {
                errorMessage = "-";
                checkEligible(inputBUCKETNAME);
                if (errorMessage.equals("-")) {
                    errorMessage = "Bucket with this name already exist";
                    System.out.println(errorMessage);
                    errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
                } else {
                    System.out.println(errorMessage);
                    errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
                }
            }

            if (errorMessage.equals("")) {
                objectArrayList = storagesnippets.getCloudbucketsList();
                successfulMessage = "Successful Creation - Bucket has been created";
                successfulMessage(anchorPane.getScene(), successfulMessage, "Close");

                objectList = FXCollections.observableList(objectArrayList);
                bucketsTable1.setItems(objectList);

                alert.hideWithAnimation();
            }


        });
        no.setOnAction(__addEvent -> {
            checker2 = 0;
            System.out.println("NO IS PRESSED, CHECKER2 is " + checker2);
            alert.hideWithAnimation();
        });

        alert.show();
    }

    private void doubleConfirmation(Scene scene, String doubleconfirm, String buttonContent, String buttonContent2) {
        checker2 = -1;
        try {
            myIPAddress=IPAddressPolicy.getIp();
            System.out.println(myIPAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            CHECKING = checker2;

            ipChecker=IPAddressPolicy.isValidRange(myIPAddress);
            System.out.println("IS IT WITHIN IP RANGE? = "+ipChecker);
            if(ipChecker==false) {
                //Display not within ip range error message
                errorMessage = "You are not within the company's premises to perform this function.";
                errorMessagePopOut(anchorPane.getScene(), errorMessage, "Close");
            }
            else {
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
            }
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
