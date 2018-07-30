import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.InputStream;

public class TrayIconExample {

    String TOOL_TIP = "FireE Tool Tip";
    String MESSAGE_HEADER = "FireE";
    java.awt.TrayIcon processTrayIcon = null;

    public static void main(String[] args) {
        try {
            TrayIconExample systemTrayExample = new TrayIconExample();
            systemTrayExample.createAndAddApplicationToSystemTray();
            systemTrayExample.startProcess();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method creates the AWT items and add it to the System tray.
     *
     * @throws IOException
     */
    private void createAndAddApplicationToSystemTray() throws IOException {
        // Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }

        final PopupMenu popup = new PopupMenu();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("FireIcon.png");
        Image img = ImageIO.read(inputStream);

        final java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(img, TOOL_TIP);
        this.processTrayIcon = trayIcon;
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a popup menu components
        MenuItem aboutItem = new MenuItem("About");

        CheckboxMenuItem autoSizeCheckBox = new CheckboxMenuItem("Set auto size");
        CheckboxMenuItem toolTipCheckBox = new CheckboxMenuItem("Set tooltip");

        Menu displayMenu = new Menu("Display");

        MenuItem errorItem = new MenuItem("Error");
        MenuItem warningItem = new MenuItem("Warning");
        MenuItem infoItem = new MenuItem("Info");
        MenuItem noneItem = new MenuItem("None");

        MenuItem exitItem = new MenuItem("Exit");

        // Add components to popup menu
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(autoSizeCheckBox);
        popup.add(toolTipCheckBox);
        popup.addSeparator();
        popup.add(displayMenu);
        displayMenu.add(errorItem);
        displayMenu.add(warningItem);
        displayMenu.add(infoItem);
        displayMenu.add(noneItem);
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        // Setting toolTipCheck and autoSizeCheckBox state as true
        toolTipCheckBox.setState(true);
        autoSizeCheckBox.setState(true);
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }

        // Add listener to trayIcon.
        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "This dialog box is run from System Tray");
            }
        });

        // Add listener to aboutItem.
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "This dialog box is run from the About menu item");
            }
        });

        // Add listener to autoSizeCheckBox.
        autoSizeCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int autoSizeCheckBoxId = e.getStateChange();
                if (autoSizeCheckBoxId == ItemEvent.SELECTED) {
                    trayIcon.setImageAutoSize(true);
                } else {
                    trayIcon.setImageAutoSize(false);
                }
            }
        });

        // Add listener to toolTipCheckBox.
        toolTipCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int toolTipCheckBoxId = e.getStateChange();
                if (toolTipCheckBoxId == ItemEvent.SELECTED) {
                    trayIcon.setToolTip(TOOL_TIP);
                } else {
                    trayIcon.setToolTip(null);
                }
            }
        });

        // Create listener for Display menu items.
        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MenuItem item = (MenuItem) e.getSource();
                System.out.println(item.getLabel());
                if ("Error".equals(item.getLabel())) {
                    trayIcon.displayMessage(MESSAGE_HEADER, "This is an error message", java.awt.TrayIcon.MessageType.ERROR);
                } else if ("Warning".equals(item.getLabel())) {
                    trayIcon.displayMessage(MESSAGE_HEADER, "This is a warning message", java.awt.TrayIcon.MessageType.WARNING);
                } else if ("Info".equals(item.getLabel())) {
                    trayIcon.displayMessage(MESSAGE_HEADER, "This is an info message", java.awt.TrayIcon.MessageType.INFO);
                } else if ("None".equals(item.getLabel())) {
                    trayIcon.displayMessage(MESSAGE_HEADER, "This is an ordinary message", java.awt.TrayIcon.MessageType.NONE);
                }
            }
        };

        // Add listeners to Display menu items.
        errorItem.addActionListener(listener);
        warningItem.addActionListener(listener);
        infoItem.addActionListener(listener);
        noneItem.addActionListener(listener);

        // Add listener to exitItem.
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
    }

    /**
     * This method will start a thread that will
     * show a popup message from the system tray after every 10 secs.
     */

    private void startProcess() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                int i = 0;
                while (true) {
                    i++;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    processTrayIcon.displayMessage("FireE Process Message",
                            "This is message number " + i,
                            java.awt.TrayIcon.MessageType.INFO);
                }

            }
        });

        thread.start();
    }
}