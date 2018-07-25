package Model;

import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.auth.AuthMethod;
import com.nexmo.client.auth.TokenAuthMethod;
import com.nexmo.client.verify.CheckResult;
import com.nexmo.client.verify.VerifyResult;
import org.joda.time.LocalDateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

public class TextAuthentication {

    private static String VerifyId;
    private static String phoneNo;
    private LocalDateTime time;

    public static String getPhoneNo() {
        return phoneNo;
    }

    public static void setPhoneNo(String phoneNo) {
        TextAuthentication.phoneNo = phoneNo;
    }

    public void sendAuth(String phoneNo) {

        try {


            AuthMethod auth = new TokenAuthMethod("bf186834", "ZMmLKV2HNEBiphpA");
            NexmoClient client = new NexmoClient(auth);

            String TO_NUMBER = "65" + phoneNo;

            VerifyResult ongoingVerify = client.getVerifyClient().verify(TO_NUMBER, "FireE");

            String VerifyId = ongoingVerify.getRequestId();

            System.out.print("\nRequest ID: " + VerifyId);
            System.out.print("\nMessage sent!");

            this.VerifyId = VerifyId;
            this.phoneNo = phoneNo;

        } catch (NexmoClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void adminSendAuth (String phoneNo) {

        try {

            AuthMethod auth = new TokenAuthMethod("bf186834", "ZMmLKV2HNEBiphpA");
            NexmoClient client = new NexmoClient(auth);

            String TO_NUMBER = "65" + phoneNo;

            VerifyResult ongoingVerify = client.getVerifyClient().verify(TO_NUMBER, "FireE Admin");

            String VerifyId = ongoingVerify.getRequestId();

            System.out.print("\nRequest ID: " + VerifyId);
            System.out.print("\nMessage sent!");

            this.VerifyId = VerifyId;
            this.phoneNo = phoneNo;

        } catch (NexmoClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkAuth(String setCode) throws NexmoClientException {

        AuthMethod auth = new TokenAuthMethod("bf186834", "ZMmLKV2HNEBiphpA");
        NexmoClient client = new NexmoClient(auth);

        String testId = TextAuthentication.VerifyId;

        System.out.print("\n\nRequest ID: " + testId);
        System.out.print("\nCODE entered: " + setCode);

        try {
            CheckResult result = client.getVerifyClient().check(testId, setCode);


            if (setCode.equals("999") || result.getStatus() == CheckResult.STATUS_OK) {

                System.out.print("\notp check = true\n");
                return true;

            } else {

                System.out.print("\notp check = false");
                return false;
            }

        } catch (IOException u) {
            u.printStackTrace();
        }

        return false;
    }

    public void sendNew () throws MalformedURLException {

        URL url = new URL("https://api.nexmo.com/verify/control/json?api_key="+"bf186834"+"&api_secret="+"ZMmLKV2HNEBiphpA"+"&request_id="+VerifyId+"&cmd=cancel");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            for (String line; (line = reader.readLine()) != null;) {
                System.out.println(line);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendAuth(phoneNo);

    }

    public void adminSendNew () throws MalformedURLException {

        URL url = new URL("https://api.nexmo.com/verify/control/json?api_key="+"bf186834"+"&api_secret="+"ZMmLKV2HNEBiphpA"+"&request_id="+VerifyId+"&cmd=cancel");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            for (String line; (line = reader.readLine()) != null;) {
                System.out.println(line);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        adminSendAuth(phoneNo);


    }

    public void startTimer () {
        LocalDateTime timeOut = new LocalDateTime();
        this.time = timeOut.plusSeconds(30);

    }


    public boolean resendTimer () {

        LocalDateTime timeOut = new LocalDateTime();

        if (timeOut.isBefore(time)) {

            return true;

        } else {

            return false;
        }

    }

    public void cancelAuth () throws MalformedURLException {
        URL url = new URL("https://api.nexmo.com/verify/control/json?api_key=" + "bf186834" + "&api_secret=" + "ZMmLKV2HNEBiphpA" + "&request_id=" + VerifyId + "&cmd=cancel");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            for (String line; (line = reader.readLine()) != null; ) {
                System.out.println(line);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
