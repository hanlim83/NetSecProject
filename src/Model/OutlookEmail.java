package Model;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
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

    public OutlookEmail(String receiverAddress) {
        this.receiverAddress = receiverAddress;
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

    public void sendEmail(String subject, String messageContent) {
        if (receiverAddress.isEmpty())
            throw new RuntimeException("Receiver Email Address is empty!");
        else if (subject.isEmpty() || messageContent.isEmpty())
            throw new RuntimeException("Missing Parameters!");
        else {
            try {
                // Create a default MimeMessage object.
                Message message = new MimeMessage(session);

                // Set From: header field of the header.
                message.setFrom(new InternetAddress(username));

                // Set To: header field of the header.
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(receiverAddress));

                // Set Subject: header field
                message.setSubject(subject);

                // Send the actual HTML message, as big as you like
                message.setText(messageContent);

                // Send message
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
                // Create a default MimeMessage object.
                Message message = new MimeMessage(session);

                // Set From: header field of the header.
                message.setFrom(new InternetAddress(host));

                // Set To: header field of the header.
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(receiverAddress));

                // Set Subject: header field
                message.setSubject(subject);

                // Create the message part
                BodyPart messageBodyPart = new MimeBodyPart();

                // Now set the actual message
                messageBodyPart.setText(messageContent);

                // Create a multipar message
                Multipart multipart = new MimeMultipart();

                // Set text message part
                multipart.addBodyPart(messageBodyPart);

                // Part two is attachment
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachmentPath);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(attachmentPath);
                multipart.addBodyPart(messageBodyPart);

                // Send the complete message parts
                message.setContent(multipart);

                // Send message
                Transport.send(message);

                System.out.println("Sent message successfully....");

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendPcap(String pcapFilePath) {
        if (receiverAddress.isEmpty())
            throw new RuntimeException("Receiver Email Address is empty!");
        else if (pcapFilePath.isEmpty())
            throw new RuntimeException("Missing Parameters!");
        else {
            try {
                // Create a default MimeMessage object.
                Message message = new MimeMessage(session);

                // Set From: header field of the header.
                message.setFrom(new InternetAddress(host));

                // Set To: header field of the header.
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(receiverAddress));

                // Set Subject: header field
                message.setSubject("Suspicious Network Event");

                // Create the message part
                BodyPart messageBodyPart = new MimeBodyPart();

                // Now set the actual message
                messageBodyPart.setText("Dear Admin,\n\nA suspicious network event has been detected on the network, for your convenience, the Pcap File Generated has been atatched in this email.\n\nFireE Admin App\nThis is a system generated email, no signature is required");

                // Create a multipar message
                Multipart multipart = new MimeMultipart();

                // Set text message part
                multipart.addBodyPart(messageBodyPart);

                // Part two is attachment
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(pcapFilePath);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(pcapFilePath);
                multipart.addBodyPart(messageBodyPart);

                // Send the complete message parts
                message.setContent(multipart);

                // Send message
                Transport.send(message);

                System.out.println("Sent message successfully....");

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
