package aero.cubox.api;

import aero.cubox.api.common.Constants;
import aero.cubox.api.demo.service.DemoService;
import aero.cubox.api.security.TokenAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import cubl.util.AES256Util;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class FeatureCovertTest {


    @Test
    public void ConvertTest() throws Exception {



        float[] fa1 = { 0.1F, 0.2F };
        byte[] bytes = FeatureCovertTest.floatArrayToByteArray(fa1);
//        float[] fa2 = FeatureCovertTest.byteArrayToFloatArray(bytes);
//
//        System.out.println("--------------------------------------------");
//        for(int i=0;i<fa2.length;i++)
//        {
//            System.out.println(fa2[i]);
//        }
//        System.out.println("--------------------------------------------");
//
//
//        assertEquals(String.valueOf(fa1[0]), String.valueOf(fa2[0]));



        String txtKey = "s8LiEwT3if89Yq3i90hIo3HepqPfOhVd";
        String txtIv = "0000000000000000";

//        byte[] key = txtKey.getBytes( "UTF-8" );
//        byte[] iv = txtIv.getBytes( "UTF-8" );
//        SecretKeySpec secretkey = new SecretKeySpec( key, "AES" );
//        IvParameterSpec param = new IvParameterSpec( iv );
//        Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
//        cipher.init( Cipher.ENCRYPT_MODE, secretkey, param );
//        byte[] encrypted = cipher.doFinal( bytes );

//        System.out.println(encodeText);

        String key = "s8LiEwT3if89Yq3i90hIo3HepqPfOhVd";
        AES256Util aES256Util = new AES256Util();
        String result =  aES256Util.byteArrEncode(bytes, key);
        System.out.println(result);

//        byte[] encode = Base64.encodeBase64( encrypted );
//        String encodeText = new String( encode, "UTF-8" );



    }

    public static byte[] floatArrayToByteArray(float[] floats)
    {
        byte[] result = new byte[floats.length * 4];

        for(int i=0;i<floats.length;i++)
        {
            int intBits =  Float.floatToIntBits(floats[i]);
//            result[i * 4] = (byte) (intBits >> 24);
//            result[i * 4 + 1] = (byte) (intBits >> 16);
//            result[i * 4 + 2] = (byte) (intBits >> 8);
//            result[i * 4 + 3] = (byte) (intBits);
            result[i * 4] = (byte) (intBits);
            result[i * 4 + 1] = (byte) (intBits >> 8);
            result[i * 4 + 2] = (byte) (intBits >> 16);
            result[i * 4 + 3] = (byte) (intBits >> 24);
        }

        return result;
    }

    public static float[] byteArrayToFloatArray(byte[] bytes)
    {
        float[] result = new float[bytes.length / 4];

        for(int i=0;i<result.length;i++)
        {
            int intBits = bytes[i * 4]
                    | (bytes[i * 4 + 1] & 0xFF) << 8
                    | (bytes[i * 4 + 2] & 0xFF) << 16
                    | (bytes[i * 4 + 3] & 0xFF) << 24;

            result[i] = Float.intBitsToFloat(intBits);
        }

        return result;
    }

    public static byte[] floatToByteArray(float value) {
        int intBits =  Float.floatToIntBits(value);
        return new byte[] {
                (byte) (intBits >> 24),
                (byte) (intBits >> 16),
                (byte) (intBits >> 8),
                (byte) (intBits) };
    }

    public static float byteArrayToFloat(byte[] bytes) {
        int intBits = bytes[0] << 24
                | (bytes[1] & 0xFF) << 16
                | (bytes[2] & 0xFF) << 8
                | (bytes[3] & 0xFF);
        return Float.intBitsToFloat(intBits);
    }
}
