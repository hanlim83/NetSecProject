import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import java.util.HashMap;
import java.util.Map;

public class AWS {
    public static void main(String[] args) {
        AWSCredentials awsCredentials = new BasicAWSCredentials("AKIAJRZLNDHLS7ZLN2IA", "sOZzRmDKUMYyR0Sws2sDH5NyVlib9ofrlgYGFRwq");
        final AmazonSNSClient client = new AmazonSNSClient(awsCredentials);
        client.setRegion(Region.getRegion(Regions.DEFAULT_REGION));

        AmazonSNSClient snsClient = new AmazonSNSClient(awsCredentials);
        String message = "A network violation has been detected!";
        String phoneNumber = "+6596588071";
        Map<String, MessageAttributeValue> smsAttributes =
                new HashMap<String, MessageAttributeValue>();
        //<set SMS attributes>
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
