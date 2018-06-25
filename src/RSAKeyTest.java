import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAKeyTest {
    public static void main(String [] args) throws Exception {
        // generate public and private keys
        KeyPair keyPair = buildKeyPair();
        PublicKey pubKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String pubKeyString=Base64.getEncoder().encodeToString(pubKey.getEncoded());
        String privateKeyString=Base64.getEncoder().encodeToString(privateKey.getEncoded());

        byte[] publicBytes = Base64.getDecoder().decode(pubKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey2 = keyFactory.generatePublic(keySpec);

        byte[] privateBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec2 = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory keyFactory2 = KeyFactory.getInstance("RSA");
        PrivateKey privateKey2 = keyFactory2.generatePrivate(keySpec2);

        System.out.println(pubKey.equals(pubKey2));
        System.out.println(privateKey.equals(privateKey2));

        // encrypt the message
        byte [] encrypted = encrypt(privateKey2, "This is a secret message");
        System.out.println(new String(encrypted));  // <<encrypted message>>

        // decrypt the message
        byte[] secret = decrypt(pubKey, encrypted);
        System.out.println(new String(secret));     // This is a secret message
    }

    public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.genKeyPair();
    }

    public static byte[] encrypt(PrivateKey privateKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return cipher.doFinal(message.getBytes());
    }

    public static byte[] decrypt(PublicKey publicKey, byte [] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return cipher.doFinal(encrypted);
    }
}
