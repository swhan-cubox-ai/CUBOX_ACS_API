package aero.cubox.api.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DigitalTwinUtil {

    private static String txtKey = "Yk1g690tRe1bMk12";
    private static String txtIv = "H21nBU97L19Vc122";

    public static String strEncode(String planeText) throws Exception {

        byte[] key = txtKey.getBytes( "UTF-8" );
        byte[] iv = txtIv.getBytes( "UTF-8" );
        SecretKeySpec secretkey = new SecretKeySpec( key, "AES" );
        IvParameterSpec param = new IvParameterSpec( iv );
        Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
        cipher.init( Cipher.ENCRYPT_MODE, secretkey, param );
        byte[] encrypted = cipher.doFinal( planeText.getBytes( "UTF-8" ) );
        byte[] encode = Base64.encodeBase64( encrypted );
        String encodeText = new String( encode, "UTF-8" );

        return encodeText;
    }

    public static String strDecode(String encodeText) throws Exception {

        byte[] key = txtKey.getBytes( "UTF-8" );
        byte[] iv = txtIv.getBytes( "UTF-8" );
        SecretKeySpec secretkey = new SecretKeySpec( key, "AES" );
        IvParameterSpec param = new IvParameterSpec( iv );
        Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
        cipher.init( Cipher.DECRYPT_MODE, secretkey, param );

        byte[] decrypt = cipher.doFinal( Base64.decodeBase64( encodeText.getBytes( "UTF-8" ) ) );
        String decryptText = new String( decrypt, "UTF-8");

        return decryptText;
    }

}
