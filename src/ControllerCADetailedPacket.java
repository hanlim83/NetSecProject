import Model.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.packet.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerCADetailedPacket implements Initializable {

    public static AnchorPane rootP;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private TreeTableView<String> packetDetailsTable;
    @FXML
    private TreeTableColumn<String, String> packetDetailsCol;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private JFXButton returnCaptureBtn;
    @FXML
    private Label ipAddr;
    @FXML
    private JFXButton homeButton;

    private ScheduledThreadPoolExecutor handler;
    private NetworkCapture capture;
    private PcapNetworkInterface device;
    private CapturedPacket packet;
    private Scene myScene;
    private boolean ARPDetection;
    private Integer threshold;
    private AWSSMS SMSHandler;
    private OutlookEmail EmailHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        try {
            String whatismyIP = IPAddressPolicy.getIp();
            ipAddr.setText(whatismyIP);
            Boolean validityIP = IPAddressPolicy.isValidRange(whatismyIP);
            if (validityIP == true) {
                ipAddr.setTextFill(Color.rgb(1, 0, 199));
            } else {
                ipAddr.setTextFill(Color.rgb(255, 0, 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Path path2 = FileSystems.getDefault().getPath("src/View/baseline_home_white_18dp.png");
        File file2 = new File(path2.toUri());
        Image imageForFile2;
        try {
            imageForFile2 = new Image(file2.toURI().toURL().toExternalForm());
            ImageView imageView1 = new ImageView(imageForFile2);
//            imageView.setFitHeight(24.5);
//            imageView.setFitWidth(35);
            homeButton.setGraphic(imageView1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void passVariables(PcapNetworkInterface nif, ScheduledThreadPoolExecutor handler, NetworkCapture capture, CapturedPacket packet, boolean ARPDetection, Integer threshold, AWSSMS SMSHandler, OutlookEmail EmailHandler) {
        this.device = nif;
        this.handler = handler;
        this.capture = capture;
        this.packet = packet;
        this.ARPDetection = ARPDetection;
        this.threshold = threshold;
        this.SMSHandler = SMSHandler;
        this.EmailHandler = EmailHandler;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, this.capture, this.ARPDetection, this.threshold, this.SMSHandler, this.EmailHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        populatePacketDetails();
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

    public void populatePacketDetails() {
        Packet orignalPacket = packet.getOriginalPacket();
        TreeItem<String> dummyRoot = new TreeItem<String>();
        TreeItem<String> PacketDetails = new TreeItem<String>("Packet Details");
        TreeItem<String> PacketNumber = new TreeItem<String>("Packet Number: " + packet.getNumber());
        TreeItem<String> CapturedInterface = new TreeItem<String>("Captured on: " + device.getDescription());
        TreeItem<String> InterfaceID = new TreeItem<String>("Interface ID: " + device.getName());
        CapturedInterface.getChildren().add(InterfaceID);
        TreeItem<String> PacketTimeStamp = new TreeItem<String>("TimeStamp: " + packet.getOrignalTimeStamp().toString());
        TreeItem<String> PacketLength = new TreeItem<String>("Length: " + packet.getLength());
        PacketDetails.getChildren().add(PacketNumber);
        PacketDetails.getChildren().add(CapturedInterface);
        PacketDetails.getChildren().add(PacketTimeStamp);
        PacketDetails.getChildren().add(PacketLength);
        dummyRoot.getChildren().add(PacketDetails);
        if (orignalPacket.contains(IpPacket.class)) {
            IpPacket ip = orignalPacket.get(IpPacket.class);
            TreeItem<String> IPHeader = new TreeItem<String>("Internet Protocol");
            TreeItem<String> IPVersion = new TreeItem<String>("Version: " + ip.getHeader().getVersion());
            TreeItem<String> IPSrcAddr = new TreeItem<String>("Source Address: " + ip.getHeader().getSrcAddr().getHostAddress());
            TreeItem<String> IPSDstAddr = new TreeItem<String>("Source Address: " + ip.getHeader().getDstAddr().getHostAddress());
            TreeItem<String> IPRawData = new TreeItem<String>("Raw Data: " + ip.getHeader().getRawData());
            IPHeader.getChildren().add(IPVersion);
            IPHeader.getChildren().add(IPSrcAddr);
            IPHeader.getChildren().add(IPSDstAddr);
            IPHeader.getChildren().add(IPRawData);
            dummyRoot.getChildren().add(IPHeader);
        } else if (orignalPacket.contains(ArpPacket.class)) {
            ArpPacket arp = orignalPacket.get(ArpPacket.class);
            TreeItem<String> ARPHeader = new TreeItem<String>("Address Resolution Protocol");
            TreeItem<String> ARPType = new TreeItem<String>("Operation: " + arp.getHeader().getOperation().name());
            TreeItem<String> ARPSrcIP = new TreeItem<String>("Source IP Address: " + arp.getHeader().getSrcProtocolAddr().getHostAddress());
            TreeItem<String> ARPSrcMac = new TreeItem<String>("Source MAC Address: " + arp.getHeader().getSrcHardwareAddr());
            TreeItem<String> ARPDstIP = new TreeItem<String>("Destination IP Address: " + arp.getHeader().getDstProtocolAddr().getHostAddress());
            TreeItem<String> ARPDstMac = new TreeItem<String>("Destination MAC Address: " + arp.getHeader().getDstHardwareAddr());
            ARPHeader.getChildren().add(ARPType);
            ARPHeader.getChildren().add(ARPSrcIP);
            ARPHeader.getChildren().add(ARPSrcMac);
            ARPHeader.getChildren().add(ARPDstIP);
            ARPHeader.getChildren().add(ARPDstMac);
            dummyRoot.getChildren().add(ARPHeader);
        }
        if (orignalPacket.contains(TcpPacket.class)) {
            TcpPacket tcp = orignalPacket.get(TcpPacket.class);
            TreeItem<String> TCPHeader = new TreeItem<String>("Transmission Control Protocol");
            TreeItem<String> TCPSrcPort = new TreeItem<String>("Source Port: " + tcp.getHeader().getSrcPort().valueAsString());
            TreeItem<String> TCPDstPort = new TreeItem<String>("Destination Port: " + tcp.getHeader().getDstPort().valueAsString());
            TreeItem<String> TCPSequence = new TreeItem<String>("Sequence Number: " + tcp.getHeader().getSequenceNumber());
            TreeItem<String> TCPAcknowledgement = new TreeItem<String>("Acknowledgement Number: " + tcp.getHeader().getAcknowledgmentNumber());
            TreeItem<String> TCPFlag;
            if (tcp.getHeader().getAck())
                TCPFlag = new TreeItem<String>("Flag: Acknowledgement");
            else if (tcp.getHeader().getFin())
                TCPFlag = new TreeItem<String>("Flag: Finished");
            else if (tcp.getHeader().getPsh())
                TCPFlag = new TreeItem<String>("Flag: Push");
            else if (tcp.getHeader().getRst())
                TCPFlag = new TreeItem<String>("Flag: Reset");
            else if (tcp.getHeader().getSyn())
                TCPFlag = new TreeItem<String>("Flag: Synchronisation");
            else if (tcp.getHeader().getUrg())
                TCPFlag = new TreeItem<String>("Flag: Urgent");
            else {
                TCPFlag = new TreeItem<String>("");
            }
            TreeItem<String> TCPChecksum = new TreeItem<String>("Checksum: " + tcp.getHeader().getChecksum());
            TCPHeader.getChildren().add(TCPSrcPort);
            TCPHeader.getChildren().add(TCPDstPort);
            TCPHeader.getChildren().add(TCPSequence);
            TCPHeader.getChildren().add(TCPAcknowledgement);
            TCPHeader.getChildren().add(TCPChecksum);
            TCPHeader.getChildren().add(TCPFlag);
            dummyRoot.getChildren().add(TCPHeader);
        } else if (orignalPacket.contains(UdpPacket.class)) {
            UdpPacket udp = orignalPacket.get(UdpPacket.class);
            TreeItem<String> UDPHeader = new TreeItem<String>("User Datagram Protocol");
            TreeItem<String> UDPSrcPort = new TreeItem<String>("Source Port: " + udp.getHeader().getSrcPort());
            TreeItem<String> UDPDstPort = new TreeItem<String>("Destination Port: " + udp.getHeader().getDstPort());
            TreeItem<String> UDPLength = new TreeItem<String>("Length: " + udp.getHeader().getLengthAsInt());
            TreeItem<String> UDPChecksum = new TreeItem<String>("Checksum: " + udp.getHeader().getChecksum());
            UDPHeader.getChildren().add(UDPSrcPort);
            UDPHeader.getChildren().add(UDPDstPort);
            UDPHeader.getChildren().add(UDPLength);
            UDPHeader.getChildren().add(UDPChecksum);
            dummyRoot.getChildren().add(UDPHeader);
        } else if (orignalPacket.contains(IcmpV4CommonPacket.class)) {
            IcmpV4CommonPacket icmp4 = orignalPacket.get(IcmpV4CommonPacket.class);
            TreeItem<String> ICMP4Header = new TreeItem<String>("Internet Control Message Protocol v4");
            TreeItem<String> ICMP4Code = new TreeItem<String>("Code: " + icmp4.getHeader().getCode().name());
            TreeItem<String> ICMP4Type = new TreeItem<String>("Type: " + icmp4.getHeader().getType().name());
            TreeItem<String> ICMP4length = new TreeItem<String>("Length: " + icmp4.getHeader().length());
            TreeItem<String> ICMP4Checksum = new TreeItem<String>("Checksum: " + icmp4.getHeader().getChecksum());
            ICMP4Header.getChildren().add(ICMP4Code);
            ICMP4Header.getChildren().add(ICMP4Type);
            ICMP4Header.getChildren().add(ICMP4length);
            ICMP4Header.getChildren().add(ICMP4Checksum);
            dummyRoot.getChildren().add(ICMP4Header);
        } else if (orignalPacket.contains(IcmpV6CommonPacket.class)) {
            IcmpV6CommonPacket icmp6 = orignalPacket.get(IcmpV6CommonPacket.class);
            TreeItem<String> ICMP6Header = new TreeItem<String>("Internet Control Message Protocol v6");
            TreeItem<String> ICMP6Code = new TreeItem<String>("Code: " + icmp6.getHeader().getCode().name());
            TreeItem<String> ICMP6Type = new TreeItem<String>("Type: " + icmp6.getHeader().getType().name());
            TreeItem<String> ICMP6length = new TreeItem<String>("Length: " + icmp6.getHeader().length());
            TreeItem<String> ICMP6Checksum = new TreeItem<String>("Checksum: " + icmp6.getHeader().getChecksum());
            ICMP6Header.getChildren().add(ICMP6Code);
            ICMP6Header.getChildren().add(ICMP6Type);
            ICMP6Header.getChildren().add(ICMP6length);
            ICMP6Header.getChildren().add(ICMP6Checksum);
            dummyRoot.getChildren().add(ICMP6Header);
        } else if (orignalPacket.contains(EncryptedPacket.class)) {
            EncryptedPacket encryptedPacket = orignalPacket.get(EncryptedPacket.class);
            TreeItem<String> EncyptedHeader = new TreeItem<String>("Encrypted Packet");
            TreeItem<String> EncryptedLength = new TreeItem<String>("Length: " + encryptedPacket.getHeader().length());
            TreeItem<String> EncryptedRawData = new TreeItem<String>("Raw Data: " + encryptedPacket.getHeader().getRawData());
            EncyptedHeader.getChildren().add(EncryptedLength);
            EncyptedHeader.getChildren().add(EncryptedRawData);
            dummyRoot.getChildren().add(EncyptedHeader);
        }
//        TreeItem<String>  = new TreeItem<String>();
        packetDetailsTable.setRoot(dummyRoot);
        packetDetailsTable.setShowRoot(false);
        packetDetailsCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<String, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<String, String> param) {
                return new SimpleStringProperty(param.getValue().getValue());
            }
        });
    }

    @FXML
    public void returntoCapture(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainPackets.fxml"));
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
            ControllerCAMainPackets controller = loader.getController();
            controller.passVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, EmailHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(nextView));
        stage.setTitle("Capture - Packets View");
        stage.show();
    }

    @FXML
    void onClickHomeButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("AdminHome.fxml"));
        myScene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();

        ControllerAdminHome controller = loader.getController();
        //controller.passData(admin);

        stage.setScene(new Scene(nextView));
        stage.setTitle("Home Page");
        stage.show();
    }

}