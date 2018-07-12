package Model;

import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.auth.AuthMethod;
import com.nexmo.client.auth.TokenAuthMethod;
import com.nexmo.client.sms.messages.TextMessage;

import java.io.IOException;
import java.util.ArrayList;

public class Alerts {
    private static final String NEXMO_API_KEY = "6198ebad";
    private static final String NEXMO_API_SECRET = "UcBivigsOmInyu5";
    private AuthMethod auth;
    private NexmoClient client;
    private ArrayList<String> adminPN;

    public Alerts(ArrayList<String> phoneNumbers) {
        adminPN = phoneNumbers;
        auth = new TokenAuthMethod(NEXMO_API_KEY, NEXMO_API_SECRET);
        client = new NexmoClient(auth);
    }

    public ArrayList<String> getAdminPN() {
        return adminPN;
    }

    public void setAdminPN(ArrayList<String> adminPN) {
        this.adminPN = adminPN;
    }

    public void sendAlert() {
        for (String s : adminPN) {
            try {
                client.getSmsClient().submitMessage(new TextMessage(
                        "FireE",
                        s,
                        "A suspicious network event has been detected! Please check the FireE Admin App for more information"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NexmoClientException e) {
                e.printStackTrace();
            }
        }
    }

}
