package cubl.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;


/*
	참고 : http://www.imcore.net/encrypt-decrypt-aes256-c-objective-ios-iphone-ipad-php-java-android-perl-javascript-python/

	jcd patch 적용 되어있어야 하며, commons-codec-1.10.jar 필요
*/

public class AES256Util {

    public static byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
    public static final String AES256KEY = "s8LiEwT3if89Yq3i90hIo3HepqPfOhVd";


    /**
     * 일반 문자열을 지정된 키를 이용하여 AES256 으로 암호화
     * @param  String - 암호화 대상 문자열
     * @param  String - 문자열 암호화에 사용될 키
     * @return String - key 로 암호화된  문자열
     * @exception
     */
    public String strEncode(String str, String key)	throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,	IllegalBlockSizeException, BadPaddingException {

        byte[] textBytes = str.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = null;

        // 선언부 다음 줄부터는 2 line 개행
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
        return Base64.encodeBase64String(cipher.doFinal(textBytes));
    }

    public byte[] aesEncode(String str, String key)	throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,	IllegalBlockSizeException, BadPaddingException {

        byte[] textBytes = str.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = null;

        // 선언부 다음 줄부터는 2 line 개행
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(textBytes);
    }



    /**
     * 암호화된 문자열을 지정된 키를 이용하여 AES256 으로 복호화
     * @param  String - 복호화 대상 문자열
     * @param  String - 문자열 복호화에 사용될 키
     * @return String - key 로 복호화된  문자열
     * @exception
     */
    public String strDecode(String str, String key)	throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        byte[] textBytes = Base64.decodeBase64(str);
        //byte[] textBytes = str.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // 개행
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
        return new String(cipher.doFinal(textBytes), "UTF-8");
    }





    /**
     * byte[] 데이터에 AES256 암호화 후, BASE64 처리 한 String 형 반환
     * @param  String - 암호화 대상 문자열
     * @param  String - 문자열 암호화에 사용될 키
     * @return String - key 로 암호화된  문자열
     * @exception
     */
    public String byteArrEncode(byte[] byteData, String key)	throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,	IllegalBlockSizeException, BadPaddingException {

//		byte[] textBytes = str.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
        return Base64.encodeBase64String(cipher.doFinal(byteData));
    }




    /**
     * byte[] 데이터에 BASE64 처리 한 String 형 반환
     * @param  byte[] - BASE64 처리할 바이트 데이터
     * @return String - BASE64 처리된 문자열
     * @exception
     */
    public String byteArrToBase64(byte[] byteData)	throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,	IllegalBlockSizeException, BadPaddingException {
        return Base64.encodeBase64String(byteData);
    }

    public byte[] byteArrToBase64Deoce(byte[] byteData)	throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,	IllegalBlockSizeException, BadPaddingException {
        return Base64.decodeBase64(byteData);
    }


    /**
     * 압호화된 데이터를 복호화 하여 byte[] 로 반환. 파일로 쓰기 위해 사용.
     * @param  String - 복호화 대상 문자열
     * @param  String - 문자열 복호화에 사용될 키
     * @return byte[] - key 로 복호화된 byte[]
     * @exception
     */
    public byte[] byteArrDecode(String str, String key)	throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        byte[] textBytes = Base64.decodeBase64(str);
        //byte[] textBytes = str.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // 개행
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(textBytes);
    }

    public byte[] byteArrDecodenopadding(String str, String key)	throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        byte[] textBytes = Base64.decodeBase64(str);
        //byte[] textBytes = str.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");

        // 개행
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(textBytes);
    }


    public byte[] byteArrDecodenopaddingImg(String str, String key)	throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        //System.out.println("str length : " + str.length());
        byte[] textBytes = Base64.decodeBase64(str);
        //byte[] textBytes = str.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        //System.out.println("textBytes length : " + textBytes.length);
        int leng = (textBytes.length % 16);
        //System.out.println("length : " + leng);
        if(leng != 0)
        {
            int pleng =  16 - leng;
            byte[] tBytes = new byte[(textBytes.length+ pleng)];
            //tBytes = textBytes;
            System.arraycopy(textBytes, 0, tBytes, 0, textBytes.length);
            for(int i = (pleng - 1) ; i >= 0 ; i--)
                tBytes[textBytes.length - pleng] = '=';
            System.out.println("tBytes length : " + tBytes.length);
            cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
            return cipher.doFinal(tBytes);
        }
        else{

            // 개행
            cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
            return cipher.doFinal(textBytes);
        }
    }






    /* S&ID 의 암호화 해제 키 */
    char[] szKey = { 0x69, 0x6d, 0x69, 0x73, 0x73, 0x79, 0x6f, 0x75, 0x68, 0x61, 0x6e, 0x67, 0x65, 0x65, 0x6e, 0x61 }; //  imissyouhangeena

    /**
     *
     * S&ID 의 암호화된 이미지 데이터 암호화 해제.
     * @param byte[] - 파일 바이트 데이터
     * @exception
     */
    public byte[] snIdByteArrDecode(byte[] byteStr)	throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(new String(szKey).getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(byteStr);
    }



    /**
     *
     * S&ID 의 암호화된 이미지 데이터 암호화 해제.
     * @param byte[] - 파일 바이트 데이터
     * @exception
     */
    public String snIdStringDecode(String imgStr)	throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(new String(szKey).getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] _temp = cipher.doFinal(imgStr.getBytes());
        return new String(_temp, 0, _temp.length);
    }


}