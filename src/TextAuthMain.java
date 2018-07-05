import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.auth.AuthMethod;
import com.nexmo.client.auth.TokenAuthMethod;
import com.nexmo.client.verify.CheckResult;
import com.nexmo.client.verify.VerifyResult;
import java.io.IOException;
import java.util.Scanner;

public class TextAuthMain {

    public static void main (String [] args) {

        try {

            AuthMethod auth = new TokenAuthMethod("bf186834", "ZMmLKV2HNEBiphpA");
            NexmoClient client = new NexmoClient(auth);

            String TO_NUMBER = "65" + "87170501"; // Temporarily hardcoded

            VerifyResult ongoingVerify = client.getVerifyClient().verify(TO_NUMBER, "FireE");


                    System.out.println("Message sent!");
                    System.out.print("Request ID: " + ongoingVerify.getRequestId());
//
//                Scanner sc = new Scanner(System.in);
//                System.out.print("Enter the OTP: ");
//                String CODE = sc.next();
//
//                try {
//
//                    CheckResult result = client.getVerifyClient().check(ongoingVerify.getRequestId(), CODE);
//                    if (result.getStatus() == CheckResult.STATUS_OK) {
//                        // Verification was successful
//                    } else {
//                        // Verification unsuccessful
//                        System.out.println("Unable to verify, status: " + result.getStatus());
//                    }
//
//                } catch (IOException e) {
//
//                    System.out.println("Wrong pin!");
//                }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NexmoClientException e) {
            e.printStackTrace();
        }


    }
}
