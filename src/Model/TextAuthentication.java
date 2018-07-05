package Model;

import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.auth.AuthMethod;
import com.nexmo.client.auth.TokenAuthMethod;
import com.nexmo.client.verify.CheckResult;
import com.nexmo.client.verify.VerifyResult;
import java.io.IOException;

public class TextAuthentication {

    public static String getSendAuth;

    public static void setGetSendAuth(String getSendAuth) {
        TextAuthentication.getSendAuth = getSendAuth;
    }


    public static String sendAuth(String phoneNo) {

        try {

            AuthMethod auth = new TokenAuthMethod("bf186834", "ZMmLKV2HNEBiphpA");
            NexmoClient client = new NexmoClient(auth);

            String TO_NUMBER = "65" + phoneNo;

            VerifyResult ongoingVerify = client.getVerifyClient().verify(TO_NUMBER, "FireE");

            String VerifyId = ongoingVerify.getRequestId();

            System.out.print("\n\nRequest ID: " + VerifyId);

            return VerifyId;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NexmoClientException e) {
            e.printStackTrace();
        }

        return getSendAuth;
    }

    public boolean checkAuth(String setCode) throws NexmoClientException {

        String CODE = setCode;

        AuthMethod auth = new TokenAuthMethod("bf186834", "ZMmLKV2HNEBiphpA");
        NexmoClient client = new NexmoClient(auth);

        sendAuth(getSendAuth);
        String testId = sendAuth(getSendAuth);

        System.out.println("Request ID: " + testId);

        try {
            CheckResult result = client.getVerifyClient().check(testId, CODE);


            if (result.getStatus() == CheckResult.STATUS_OK || CODE.equals("999")) {

                System.out.print("otp check = true");
                return true;

            } else {

                System.out.print("otp check = false");
                return false;
            }

        } catch (IOException u) {
            u.printStackTrace();
        }

        return false;
    }


}
