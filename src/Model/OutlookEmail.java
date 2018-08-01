package Model;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

public class OutlookEmail {
    private static final String username = "do_not_reply_fireE@outlook.sg";
    private static final String password = "NSPJEmail247";
    private static final String host = "smtp-mail.outlook.com";
    private static final String port = "587";
    private static final String AuthTLS = "true";
    private static Properties props;
    private static Session session;
    private String receiverAddress;
    private ArrayList<String> adminEmailAddresses;

    public OutlookEmail(String receiverAddress) {
        this.receiverAddress = receiverAddress;
        this.adminEmailAddresses = new ArrayList<String>();
        props = new Properties();
        props.put("mail.smtp.auth", AuthTLS);
        props.put("mail.smtp.starttls.enable", AuthTLS);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    public OutlookEmail() {
        receiverAddress = "";
        this.adminEmailAddresses = new ArrayList<String>();
        props = new Properties();
        props.put("mail.smtp.auth", AuthTLS);
        props.put("mail.smtp.starttls.enable", AuthTLS);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    public OutlookEmail(ArrayList<String> adminEmailAddresses) {
        this.adminEmailAddresses = adminEmailAddresses;
        receiverAddress = "";
        props = new Properties();
        props.put("mail.smtp.auth", AuthTLS);
        props.put("mail.smtp.starttls.enable", AuthTLS);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public ArrayList<String> getAdminEmailAddresses() {
        return adminEmailAddresses;
    }

    public void setAdminEmailAddresses(ArrayList<String> adminEmailAddresses) {
        this.adminEmailAddresses = adminEmailAddresses;
    }

    public void sendEmail(String subject, String messageContent) {
        if (receiverAddress.isEmpty())
            throw new RuntimeException("Receiver Email Address is empty!");
        else if (subject.isEmpty() || messageContent.isEmpty())
            throw new RuntimeException("Missing Parameters!");
        else {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(receiverAddress));
                message.setSubject(subject);
                message.setText(messageContent);

                Transport.send(message);
                System.out.println("Sent message successfully....");
            } catch (MessagingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public void sendEmail(String subject, String messageContent, String attachmentPath) {
        if (receiverAddress.isEmpty())
            throw new RuntimeException("Receiver Email Address is empty!");
        else if (subject.isEmpty() || messageContent.isEmpty() || attachmentPath.isEmpty())
            throw new RuntimeException("Missing Parameters!");
        else {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(receiverAddress));
                message.setSubject(subject);
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(messageContent);
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachmentPath);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(new File(attachmentPath).getName());
                multipart.addBodyPart(messageBodyPart);
                message.setContent(multipart);
                Transport.send(message);
                System.out.println("Sent message successfully....");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendBatchEmail(String subject, String messageContent) {
        if (receiverAddress.isEmpty() || adminEmailAddresses.isEmpty())
            throw new RuntimeException("Nobody to send to!");
        else if (subject.isEmpty() || messageContent.isEmpty())
            throw new RuntimeException("Missing Parameters!");
        else {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(receiverAddress));
                for (String s : adminEmailAddresses) {
                    if (!s.equals(receiverAddress))
                        message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(s));
                }
                message.setSubject(subject);
                message.setText(messageContent);
                Transport.send(message);
                System.out.println("Sent message successfully....");
            } catch (MessagingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public void sendBatchEmail(String subject, String messageContent, String attachmentPath) {
        if (receiverAddress.isEmpty() || adminEmailAddresses.isEmpty())
            throw new RuntimeException("Receiver Email Address is empty!");
        else if (subject.isEmpty() || messageContent.isEmpty() || attachmentPath.isEmpty())
            throw new RuntimeException("Missing Parameters!");
        else {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(receiverAddress));
                for (String s : adminEmailAddresses) {
                    if (!s.equals(receiverAddress))
                        message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(s));
                }
                message.setSubject(subject);
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(messageContent);
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachmentPath);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(new File(attachmentPath).getName());
                multipart.addBodyPart(messageBodyPart);
                message.setContent(multipart);
                Transport.send(message);
                System.out.println("Sent message successfully....");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendParitalPcap(String pcapFilePath) {
        if (receiverAddress.isEmpty() && adminEmailAddresses.isEmpty())
            throw new RuntimeException("Nobody to send it to!");
        else if (pcapFilePath.isEmpty())
            throw new RuntimeException("Missing Parameters!");
        else {
            try {
                // Create a default MimeMessage object.
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(receiverAddress));
                if (!adminEmailAddresses.isEmpty()) {
                    for (String s : adminEmailAddresses) {
                        if (!s.equals(receiverAddress))
                            message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(s));
                    }
                }
                message.setSubject("Suspicious Network Event");
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText("Dear Admin,\n\nA suspicious network event has been detected on the network, for your convenience, the Pcap File Generated has been attached in this email.\n\nFireE Admin App\nThis is a system generated email, no signature is required");
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(pcapFilePath);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(new File(pcapFilePath).getName());
                multipart.addBodyPart(messageBodyPart);
                message.setContent(multipart);
                Transport.send(message);
                System.out.println("Sent message successfully....");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendFullPcap(String pcapFilePath) {
        if (receiverAddress.isEmpty())
            throw new RuntimeException("Receiver Email Address is empty!");
        else if (pcapFilePath.isEmpty())
            throw new RuntimeException("Missing Parameters!");
        else {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(receiverAddress));
                message.setSubject("Network Capture Completed");
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText("Dear Admin,\n\nA Network Capture has been recently stopped. For your convenience, the Pcap File Generated has been attached in this email.\n\nFireE Admin App\nThis is a system generated email, no signature is required");
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(pcapFilePath);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(new File(pcapFilePath).getName());
                multipart.addBodyPart(messageBodyPart);
                message.setContent(multipart);
                Transport.send(message);
                System.out.println("Sent message successfully....");

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
