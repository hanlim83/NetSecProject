package Model;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AWSSMS {
    private static final String ACCESS_KEY = "AKIAI3QOKRMRP56BPYGQ";
    private static final String SECRET_KEY = "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";
    private static final String alertMsg = "A suspicious network event has been detected! Please check the FireE Admin App for more information";
    private static final String authMsg1 = "To continue your sign in, please enter ";
    private static final String authMsg2 = " as your OTP in the FireE App";
    private static BasicAWSCredentials auth;
    private static AmazonSNS snsClient;
    private static Map<String, MessageAttributeValue> smsAttributes = new HashMap<String, MessageAttributeValue>();
    private ArrayList<String> adminPN;
    private boolean GeneralUse;

    public AWSSMS(ArrayList<String> phoneNumbers) {
        adminPN = phoneNumbers;
        auth = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        snsClient = AmazonSNSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(auth))
                .withRegion(Regions.AP_SOUTHEAST_1)
                .build();
        GeneralUse = false;
        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("FireE")
                .withDataType("String"));
        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                .withStringValue("1.00")
                .withDataType("Number"));
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue("Transactional")
                .withDataType("String"));
    }

    public AWSSMS() {
        auth = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        snsClient = AmazonSNSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(auth))
                .withRegion(Regions.AP_SOUTHEAST_1)
                .build();
        GeneralUse = true;
        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("FireE")
                .withDataType("String"));
        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                .withStringValue("1.00")
                .withDataType("Number"));
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue("Transactional")
                .withDataType("String"));
    }

    public ArrayList<String> getAdminPN() {
        return adminPN;
    }

    public void setAdminPN(ArrayList<String> adminPN) {
        this.adminPN = adminPN;
    }

    public void sendAlert() {
        if (GeneralUse == true) {
            return;
        } else if (adminPN == null)
            throw new RuntimeException("Admin Phone Numbers are not set!");
        else {
            for (String s : adminPN) {
                PublishResult result = snsClient.publish(new PublishRequest()
                        .withMessage(alertMsg)
                        .withPhoneNumber("+65" + s)
                        .withMessageAttributes(smsAttributes));
                System.out.println(result);
            }
        }
    }

    public void sendSMS(String phoneNumber, String Message) {
        if (phoneNumber.isEmpty() || Message.isEmpty())
            throw new RuntimeException("Missing Parameters");
        else {
            PublishResult result = snsClient.publish(new PublishRequest()
                    .withMessage(Message)
                    .withPhoneNumber("+65" + phoneNumber)
                    .withMessageAttributes(smsAttributes));
            System.out.println(result);
        }
    }

    public void sendCode(String phoneNumber, String code) {
        if (phoneNumber.isEmpty() || code.isEmpty())
            throw new RuntimeException("Missing Parameters");
        else {
            PublishResult result = snsClient.publish(new PublishRequest()
                    .withMessage(authMsg1 + code + authMsg2)
                    .withPhoneNumber("+65" + phoneNumber)
                    .withMessageAttributes(smsAttributes));
            System.out.println(result);
        }
    }

}
