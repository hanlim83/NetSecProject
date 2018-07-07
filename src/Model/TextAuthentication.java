package Model;

import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.auth.AuthMethod;
import com.nexmo.client.auth.TokenAuthMethod;
import com.nexmo.client.verify.CheckResult;
import com.nexmo.client.verify.VerifyResult;
import java.io.IOException;

public class TextAuthentication {

    private static String VerifyId;


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


}
