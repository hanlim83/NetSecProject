import Model.CapturedPacket;
import Model.NetworkCapture;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.packet.Packet;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerCAMainPackets implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private TableView packetstable;

    @FXML
    private TableColumn<CapturedPacket, Integer> tableColPN;

    @FXML
    private TableColumn<CapturedPacket, String> tableColSrcIP;

    @FXML
    private TableColumn<CapturedPacket, Integer> tableColSrcPort;

    @FXML
    private TableColumn<CapturedPacket, String> tableColDstIP;

    @FXML
    private TableColumn<CapturedPacket, Integer> tableColDstPort;

    @FXML
    private TableColumn<CapturedPacket, String> tableColProtocol;

    @FXML
    private TableColumn<CapturedPacket, Integer> tableColLength;

    @FXML
    private TableColumn<CapturedPacket, String> tableColInfo;

    @FXML
    private JFXToggleButton captureToggle;

    private PcapNetworkInterface device;
    private Scene myScene;
    public static AnchorPane rootP;
    private ArrayList<CapturedPacket>packets;
    private ObservableList<CapturedPacket> OLpackets;
    private NetworkCapture capture;
    private Thread captureThread;
    private ScheduledExecutorService service;
    private ScheduledFuture tableviewRunnable;
    private boolean FirstRun = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        tableColPN.setCellValueFactory(new PropertyValueFactory<CapturedPacket,Integer>("number"));
        tableColSrcIP.setCellValueFactory(new PropertyValueFactory<CapturedPacket,String>("srcIP"));
        tableColSrcPort.setCellValueFactory(new PropertyValueFactory<CapturedPacket,Integer>("srcPort"));
        tableColDstIP.setCellValueFactory(new PropertyValueFactory<CapturedPacket,String>("destIP"));
        tableColDstPort.setCellValueFactory(new PropertyValueFactory<CapturedPacket,Integer>("dstPort"));
        tableColProtocol.setCellValueFactory(new PropertyValueFactory<CapturedPacket,String>("Protocol"));
        tableColLength.setCellValueFactory(new PropertyValueFactory<CapturedPacket,Integer>("length"));
        tableColInfo.setCellValueFactory(new PropertyValueFactory<CapturedPacket,String>("information"));
        captureToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (captureToggle.isSelected()){
                    startCapturing();
                }
                else {
                    stopCapturing();
                }
            }
        });
    }
    public void passVariables(PcapNetworkInterface nif, ScheduledExecutorService service, NetworkCapture Capture){
        this.device = nif;
        this.service = service;
        this.capture = Capture;
        if (capture != null){
            packets = capture.packets;
            OLpackets = FXCollections.observableArrayList(packets);
            packetstable.setItems(OLpackets);
            packetstable.refresh();
        }
    }

    public void startCapturing(){
        if (capture == null)
            capture = new NetworkCapture(device);
//        if (FirstRun == true){
            tableviewRunnable = service.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            packets = capture.packets;
                            OLpackets = FXCollections.observableArrayList(packets);
                            packetstable.setItems(OLpackets);
                            packetstable.refresh();
                        }
                    });
                }
            }, 2, 1, TimeUnit.SECONDS);
            /*FirstRun = false;
        }*/
        Runnable task = () -> {
                capture.startSniffing();
                packets = capture.packets;
                for (CapturedPacket p : packets) {
                    System.out.println(p.toString());
                }
        };
        captureThread = new Thread(task);
        captureThread.setDaemon(true);
        captureThread.start();
    }
    public void stopCapturing(){
        capture.stopSniffing();
        tableviewRunnable.cancel(true);
        System.out.println("Passwords do not match!");
        //Alert below
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        String title = "Packet Capturing Summary";
        String content;
        if (com.sun.jna.Platform.isWindows())
            content = "Packets Received By Interface: "+capture.getPacketsReceived()+"\nPackets Dropped: "+capture.getPacketsDropped()+"\nPackets Dropped By Interface: "+capture.getPacketsDroppedByInt()+"\nTotal Packets Captured: "+capture.getPacketsCaptured();
        else
            content = "Packets Received By Interface: "+capture.getPacketsReceived()+"\nPackets Dropped: "+capture.getPacketsDropped()+"\nPackets Dropped By Interface: "+capture.getPacketsDroppedByInt()+"\nTotal Packets Captured: "+capture.getPktCount();
//
//            JFXDialogLayout dialogContent = new JFXDialogLayout();
//
//            dialogContent.setHeading(new Text(title));
//
//            dialogContent.setBody(new Text(content));
//
        JFXButton close = new JFXButton("Close");

        close.setButtonType(JFXButton.ButtonType.RAISED);

        close.setStyle("-fx-background-color: #00bfff;");
//
//            dialogContent.setActions(close);
//
//            JFXDialog dialog = new JFXDialog(StackPane, dialogContent, JFXDialog.DialogTransition.BOTTOM);
//
//            dialog.show();

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(title));
        layout.setBody(new Label(content));
        layout.setActions(close);
        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                alert.hideWithAnimation();
            }
        });
        alert.show();
       /* Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Capture Summary");
        alert.setHeaderText("Capture Summary Details");
        if (com.sun.jna.Platform.isWindows())
            alert.setContentText("Packets Received By Interface: "+capture.getPacketsReceived()+"\nPackets Dropped: "+capture.getPacketsDropped()+"\nPackets Dropped By Interface: "+capture.getPacketsDroppedByInt()+"\nTotal Packets Captured: "+capture.getPacketsCaptured());
        else
            alert.setContentText("Packets Received By Interface: "+capture.getPacketsReceived()+"\nPackets Dropped: "+capture.getPacketsDropped()+"\nPackets Dropped By Interface: "+capture.getPacketsDroppedByInt()+"\nTotal Packets Captured: "+capture.getPktCount());
        alert.showAndWait();*/
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
    @FXML
    public void ShowpacketDetails(MouseEvent event) {
        CapturedPacket selected = (CapturedPacket) packetstable.getSelectionModel().getSelectedItem();
        if (selected.getInformation() != null && selected.getInformation().equals("Not a Layer 2 (IP) Packet")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unsupported Packet");
            alert.setHeaderText("Unsupported Packet");
            alert.setContentText("The packet you have selected does not have an IP Header and therefore details cannot be seen!");
            alert.showAndWait();
        }
        else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CADetailedPacket.fxml"));
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCADetailedPacket controller = loader.<ControllerCADetailedPacket>getController();
                controller.passVariables(device,service,capture,selected);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("NSPJ");
            stage.show();
        }
    }
    @FXML
    public void ClearPackets(ActionEvent event) {
        capture = null;
        OLpackets = null;
        packetstable.setItems(null);
        packetstable.refresh();

    }

    @FXML
    public void launchPcapExport(ActionEvent event) {

    }
}
