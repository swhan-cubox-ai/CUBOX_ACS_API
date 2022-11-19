package aero.cubox.api.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;


@Slf4j
@Service
public class DemoService {

    //@Scheduled(cron = "0/1 * * * * *")
    public void test001() throws Exception
    {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");
        Date begDt = new Date();
        log.debug("test001 [BEG] " + sdf.format(begDt));
        Thread.sleep(1000);
        log.debug("test001 [END] " + sdf.format(begDt) + " ~ " + sdf.format(new Date()));
    }

    //@Scheduled(cron = "0/5 * * * * *")
    public void test005() throws Exception
    {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");
        Date begDt = new Date();
        log.debug("test003 [BEG] " + sdf.format(begDt));
        Thread.sleep(1000);
        log.debug("test001 [END] " + sdf.format(begDt) + " ~ " + sdf.format(new Date()));
    }

    public String EncodeForDigitalTwin(String planeText) throws Exception
    {
        String txtKey = "Yk1g690tRe1bMk12";
        String txtIv = "H21nBU97L19Vc122";

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


    public String DecodeForDigitalTwin(String encodeText) throws Exception
    {
        String txtKey = "Yk1g690tRe1bMk12";
        String txtIv = "H21nBU97L19Vc122";

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


    public String EncodeForDevice(String planeText) throws Exception
    {
        String txtKey = "s8LiEwT3if89Yq3i90hIo3HepqPfOhVd";
        String txtIv = "0000000000000000";

        byte[] key = txtKey.getBytes( "UTF-8" );
        byte[] iv = txtIv.getBytes( "UTF-8" );
        SecretKeySpec secretkey = new SecretKeySpec( key, "AES" );
        IvParameterSpec param = new IvParameterSpec( iv );
        Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding");
        cipher.init( Cipher.ENCRYPT_MODE, secretkey, param );
        byte[] encrypted = cipher.doFinal( planeText.getBytes( "UTF-8" ) );
        byte[] encode = Base64.encodeBase64( encrypted );
        String encodeText = new String( encode, "UTF-8" );

        return encodeText;
    }


    public String DecodeForDevice(String encodeText) throws Exception
    {
        String txtKey = "s8LiEwT3if89Yq3i90hIo3HepqPfOhVd";
        String txtIv = "0000000000000000";

        byte[] key = txtKey.getBytes( "UTF-8" );
        byte[] iv = txtIv.getBytes( "UTF-8" );
        SecretKeySpec secretkey = new SecretKeySpec( key, "AES" );
        IvParameterSpec param = new IvParameterSpec( iv );
        Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding");
        cipher.init( Cipher.DECRYPT_MODE, secretkey, param );

        byte[] decrypt = cipher.doFinal( Base64.decodeBase64( encodeText.getBytes( "UTF-8" ) ) );
        String decryptText = new String( decrypt, "UTF-8");

        return decryptText;
    }

}
