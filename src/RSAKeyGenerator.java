import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Base64;

public class RSAKeyGenerator {
    private KeyPair keyPair;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    IvParameterSpec ivspec = new IvParameterSpec(iv);

    public void buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        keyPair=keyPairGenerator.genKeyPair();
        publicKey=keyPair.getPublic();
        privateKey=keyPair.getPrivate();
//        return keyPairGenerator.genKeyPair();
    }

    public String getPublicKeyString(){
        //System.out.println(publicKey);
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public String getPrivateKeyString(){
        //System.out.println(privateKey);
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public String getPrivateKeyString(String password, String EncodedPrivateKeyString) throws Exception {
        //decode String from EncryptedPrivateKeyString
        byte[] EncryptedPrivateKeyString=Base64.getDecoder().decode(EncodedPrivateKeyString);
        byte[] PrivateKeyString=decryptPrivateKey(calculateSymmetricKey(password),EncryptedPrivateKeyString);
        //return Base64.getEncoder().encodeToString(PrivateKeyString);
        return new String(PrivateKeyString);
    }

    public String getEncryptedPrivateKeyString(String password, String privateKey) throws Exception {
        //String EncryptedPrivateKeyString=null;
        //getPrivateKeyString();

        //encryption codes
        byte[] EncryptedPrivateKeyString=encryptPrivateKey(calculateSymmetricKey(password),privateKey);
        //convert to base64
        return Base64.getEncoder().encodeToString(EncryptedPrivateKeyString);
    }

    public SecretKeySpec calculateSymmetricKey(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
//        byte[] key = new byte[length];
        byte[] key = fixSecret(password, 32);
        SecretKeySpec symmetricKey=new SecretKeySpec(key, "AES");
        Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
        return symmetricKey;
    }

    private byte[] fixSecret(String s, int length) throws UnsupportedEncodingException {
        if (s.length() < length) {
            int missingLength = length - s.length();
            for (int i = 0; i < missingLength; i++) {
                s += " ";
            }
        }
        return s.substring(0, length).getBytes("UTF-8");
    }

    private byte[] encryptPrivateKey(SecretKeySpec symmetricKey, String privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, symmetricKey, ivspec);

        return cipher.doFinal(privateKey.getBytes());
    }

    private byte[] decryptPrivateKey(SecretKeySpec symmetricKey, byte [] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, symmetricKey, ivspec);

        return cipher.doFinal(encrypted);
    }
}
