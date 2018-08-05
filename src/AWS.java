import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import java.util.HashMap;
import java.util.Map;

public class AWS {
    public static void main(String[] args) {
//        AWSCredentials awsCredentials = new BasicAWSCredentials("AKIAJRZLNDHLS7ZLN2IA", "sOZzRmDKUMYyR0Sws2sDH5NyVlib9ofrlgYGFRwq");
        AWSCredentials awsCredentials = new BasicAWSCredentials("AKIAI3QOKRMRP56BPYGQ", "Sk1fwykYbJE6AxOMsayLJtlNerp0DDNTbBdTnGH+");
        final AmazonSNSClient client = new AmazonSNSClient(awsCredentials);
        client.setRegion(Region.getRegion(Regions.DEFAULT_REGION));

        AmazonSNSClient snsClient = new AmazonSNSClient(awsCredentials);
        String message = "A network violation has been detected";
        String phoneNumber = "+65<Masked>";
        Map<String, MessageAttributeValue> smsAttributes =
                new HashMap<String, MessageAttributeValue>();
        //<set SMS attributes>
        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("JGesus")
                .withDataType("String"));
        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                .withStringValue("1.00")
                .withDataType("Number"));
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue("Transactional")
                .withDataType("String"));
        sendSMSMessage(snsClient, message, phoneNumber, smsAttributes);
    }

    public static void sendSMSMessage(AmazonSNSClient snsClient, String message,
                                      String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
        System.out.println(result); // Prints the message ID.
    }

//    public void setAttributes() {
//        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
//                .withStringValue("SingHealth")
//                .withDataType("String"));
//        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
//                .withStringValue("1.00")
//                .withDataType("Number"));
//        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
//                .withStringValue("Transactional")
//                .withDataType("String"));
//    }
//    public static void main(String[] args) {
//        AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
//        String message = "A network violation has been detected!";
//        String phoneNumber = "+6596588071";
//        Map<String, MessageAttributeValue> smsAttributes =
//                new HashMap<String, MessageAttributeValue>();
//        //<set SMS attributes>
//        sendSMSMessage((AmazonSNSClient) snsClient, message, phoneNumber, smsAttributes);
//    }
//
//    public static void sendSMSMessage(AmazonSNSClient snsClient, String message,
//                                      String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {
//        PublishResult result = snsClient.publish(new PublishRequest()
//                .withMessage(message)
//                .withPhoneNumber(phoneNumber)
//                .withMessageAttributes(smsAttributes));
//        System.out.println(result); // Prints the message ID.
//    }
}
