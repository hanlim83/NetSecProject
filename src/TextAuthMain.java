import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.auth.AuthMethod;
import com.nexmo.client.auth.TokenAuthMethod;
import com.nexmo.client.verify.VerifyResult;
import java.io.IOException;
import java.util.Scanner;

public class TextAuthMain {

    public static void main (String [] args) {

        try {

            AuthMethod auth = new TokenAuthMethod("bf186834", "ZMmLKV2HNEBiphpA");
            NexmoClient client = new NexmoClient(auth);

            String TO_NUMBER = "6587170501"; // Temporarily hardcoded

            VerifyResult ongoingVerify = client.getVerifyClient().verify(TO_NUMBER, "FireE");

                    System.out.println("Message sent!");

                Scanner sc = new Scanner(System.in);
                System.out.print("Enter the OTP: ");
                  String CODE = sc.next();

            client.getVerifyClient().check(ongoingVerify.getRequestId(), CODE);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NexmoClientException e) {
            e.printStackTrace();
        }


    }
}
