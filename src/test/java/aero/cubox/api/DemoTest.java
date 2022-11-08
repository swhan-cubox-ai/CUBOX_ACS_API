package aero.cubox.api;

import aero.cubox.api.common.Constants;
import aero.cubox.api.demo.controller.DemoController;
import aero.cubox.api.demo.service.DemoService;
import aero.cubox.api.security.TokenAuthenticationFilter;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = DemoController.class)
public class DemoTest {

    String message = "";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenAuthenticationFilter tokenAuthenticationFilter;


    @Test
    public void AesTestForDigitalTwin() {

        try
        {
            String planeText = "한글테스트";
            String encodeText = this.EncodeForDigitalTwin(planeText);
            String decryptText = this.DecodeForDigitalTwin(encodeText);

            System.out.println("planeText : " + planeText);
            System.out.println("encodeText : " + encodeText);
            System.out.println("decryptText : " + decryptText);

            assertEquals(planeText, decryptText);
        }
        catch (Exception e)
        {
            this.message = String.format("test error : %s",  e.getMessage());
            System.out.println(this.message);
        }
    }

    @Test
    public void AesTestForDevice() {

        try
        {
            String planeText = "한글테스트";
            String encodeText = this.EncodeForDevice(planeText);
            String decryptText = this.DecodeForDevice(encodeText);

            System.out.println("planeText : " + planeText);
            System.out.println("encodeText : " + encodeText);
            System.out.println("decryptText : " + decryptText);

            assertEquals(planeText, decryptText);
        }
        catch (Exception e)
        {
            this.message = String.format("test error : %s",  e.getMessage());
            System.out.println(this.message);
        }

    }

    @Test
    public void demoTest() throws Exception {

        var mvcResult =
            mockMvc
                .perform(
                    get(Constants.API.API_ACS_PREFIX + Constants.API.API_DEMO + "/device/add")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(mvcResult.getResponse());
    }

    public static String EncodeForDigitalTwin(String planeText) throws Exception
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


    public static String DecodeForDigitalTwin(String encodeText) throws Exception
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


    public static String EncodeForDevice(String planeText) throws Exception
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


    public static String DecodeForDevice(String encodeText) throws Exception
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


    public void RestAesTest_Proc(String jsonString) throws Exception {

        //유종화
        //String password = "6yr5bAFviXc1i0TT"; // CHB 운영자에게 받은 API 계정 Password (설정 파일로 관리 필요)
        //String apiKey = "KoDMllJYsQwSGiGh"; // CHB 운영자에게 받은 API 계정 API Key (설정 파일로 관리 필요)

        //신희태
        //String password = "QwuKzBRfuuYzXdKV"; // CHB 운영자에게 받은 API 계정 Password (설정 파일로 관리 필요)
        //String apiKey = "TdwWLkfwhxzfGDa1"; // CHB 운영자에게 받은 API 계정 API Key (설정 파일로 관리 필요)

        //개발서버
        String password = "zgwWjMsacl08XI10"; // CHB 운영자에게 받은 API 계정 Password (설정 파일로 관리 필요)
        String apiKey = "FqoO6lURmPrizV51"; // CHB 운영자에게 받은 API 계정 API Key (설정 파일로 관리 필요)

        byte[] key = password.getBytes( "UTF-8" );
        byte[] iv = apiKey.getBytes( "UTF-8" );
        SecretKeySpec secretkey = new SecretKeySpec( key, "AES" );
        IvParameterSpec param = new IvParameterSpec( iv );
        Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
        cipher.init( Cipher.ENCRYPT_MODE, secretkey, param );
        byte[] encrypted = cipher.doFinal( jsonString.getBytes( "UTF-8" ) );
        byte[] encode = Base64.encodeBase64( encrypted );
        String encodeText = new String( encode, "UTF-8" );

        //System.out.println(encodeText);

        Map<String, Object> reqMessageMap = new HashMap<>();
        ObjectMapper reqMapper = new ObjectMapper();

        reqMessageMap.put( "apiCode", "sha.tagInfoService.getTagValueList" );
        reqMessageMap.put( "systemId", "SEJONG_SERVER" );  // 설정 파일로 관리 필요
        reqMessageMap.put( "encrypted", encodeText );

        String reqJsonString = reqMapper.writeValueAsString( reqMessageMap );

        //jsonString = mapper.writeValueAsString(messageMap);

        System.out.println("[Request Data] : ");
        System.out.println(reqJsonString);
        System.out.println();

        HttpClient client =  HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost( "http://192.168.0.225/interface/externalapi/gateway" ); // 설정 파일로 관리 필요

        StringEntity stringEntity = new StringEntity( reqJsonString );
        httpPost.setEntity( stringEntity );
        httpPost.setHeader( "Content-Type", "application/json; charset=UTF-8" );

        HttpResponse response = client.execute( httpPost );

        if ( response.getStatusLine().getStatusCode() == 200 )
        {

            // RESPONSE SUCCESS
            HttpEntity entity = response.getEntity();
            InputStreamReader inputStreamReader = new InputStreamReader( entity.getContent(), "UTF-8" );
            BufferedReader bufferedReader = new BufferedReader( inputStreamReader );
            String line = null;
            StringBuilder builder = new StringBuilder();

            while( (line = bufferedReader.readLine()) != null )
            {
                builder.append( line );
            }

            ObjectMapper resMapper = new ObjectMapper();

            Map<String, String> responseData = resMapper.readValue( builder.toString(), Map.class );

            System.out.println("[Response Data] : ");
            System.out.println(responseData.toString());
            System.out.println();

            String resEncryptedData = responseData.get( "encrypted" );

            System.out.println("[Encrypted Data] : ");
            System.out.println(resEncryptedData);
            System.out.println();

            // 수신받은 데이터 복호화
            cipher.init( Cipher.DECRYPT_MODE, secretkey, param );

            byte[] decrypt = cipher.doFinal( Base64.decodeBase64( resEncryptedData.getBytes( "UTF-8" ) ) );
            String decryptText = new String( decrypt, "UTF-8");

            System.out.println("[Decrypted Data] : ");
            System.out.println(decryptText);

            // TODO 복호화된 decryptText를 파싱하여 사용하면 됨

        }
        else
        {
            //RESPONSE Fail시 처리 로직 추가
        }

    }

}
