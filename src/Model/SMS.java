package Model;

import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.auth.AuthMethod;
import com.nexmo.client.auth.TokenAuthMethod;
import com.nexmo.client.sms.messages.TextMessage;

import java.io.IOException;
import java.util.ArrayList;

public class SMS {
    private static final String NEXMO_API_KEY = "6198ebad";
    private static final String NEXMO_API_SECRET = "hdke38E3m";
    private AuthMethod auth;
    private NexmoClient client;
    private ArrayList<String> adminPN;
    private static final boolean permitUse = true;
    private boolean GeneralUse;

    //PermitUse Boolean is used to prevent accidental charges to NEXMO balance. Please set as necessary

    public SMS(ArrayList<String> phoneNumbers) {
        adminPN = phoneNumbers;
        auth = new TokenAuthMethod(NEXMO_API_KEY, NEXMO_API_SECRET);
        client = new NexmoClient(auth);
        GeneralUse = false;
    }

    public SMS() {
        auth = new TokenAuthMethod(NEXMO_API_KEY, NEXMO_API_SECRET);
        client = new NexmoClient(auth);
        GeneralUse = true;
    }

    public ArrayList<String> getAdminPN() {
        if (GeneralUse == false)
            return adminPN;
        else
            throw new RuntimeException("Not configured for specific use!");
    }

    public void setAdminPN(ArrayList<String> adminPN) {
        if (GeneralUse == false)
            this.adminPN = adminPN;
        else
            throw new RuntimeException("Not configured for specific use!");
    }

    public void sendAlert() {
        if (GeneralUse == true) {
            return;
        } else if (permitUse == false)
            throw new RuntimeException("Please enable PermitUse Boolean first!");
        else if (adminPN == null)
            throw new RuntimeException("Set Admin Phone Number(s) first!");
        else {
            for (String s : adminPN) {
                try {
                    client.getSmsClient().submitMessage(new TextMessage(
                            "FireE",
                            "65" + s,
                            "A suspicious network event has been detected! Please check the FireE Admin App for more information"));
                    System.out.println("Sent");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NexmoClientException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendSMS(String phoneNumber, String Message) {
        if (permitUse == false)
            throw new RuntimeException("Please enable PermitUse Boolean first!");
        else if (phoneNumber.isEmpty() || Message.isEmpty())
            throw new RuntimeException("Missing Parameters!");
        else {
            try {
                client.getSmsClient().submitMessage(new TextMessage(
                        "FireE",
                        "65" + phoneNumber,
                        Message));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NexmoClientException e) {
                e.printStackTrace();
            }
        }
    }
}
