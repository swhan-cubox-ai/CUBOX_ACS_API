package aero.cubox.api.demo.controller;


import aero.cubox.api.common.Constants;
import aero.cubox.api.demo.service.DemoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import kotlin.collections.ArrayDeque;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

@RestController
@RequestMapping({ Constants.API.API_ACS_PREFIX + Constants.API.API_DEMO })
public class DemoController {

    @Autowired
    DemoService demoService;

    @ResponseBody
    @GetMapping(value = {"/dt/terminal"})
    public String OnTerminal() throws Exception  {

        String result = "";
        try {
            String requestMapJsonString = this.GetReqBodyTerminal();

            HttpClient client =  HttpClientBuilder.create().build();
            //HttpPost httpPost = new HttpPost( "http://192.168.0.213:10002/acs/cubox/terminal" );
            HttpPost httpPost = new HttpPost( "http://localhost:8080/acs/v1/demo/device/dtmock/terminal" );

            StringEntity stringEntity = new StringEntity( requestMapJsonString );
            httpPost.setEntity( stringEntity );
            httpPost.setHeader( "Content-Type", "application/json; charset=UTF-8" );

            HttpResponse response = client.execute( httpPost );

            if ( response.getStatusLine().getStatusCode() == 200 ) {
                HttpEntity entity = response.getEntity();
                InputStreamReader inputStreamReader = new InputStreamReader( entity.getContent(), "UTF-8" );
                BufferedReader bufferedReader = new BufferedReader( inputStreamReader );
                String line = null;
                StringBuilder builder = new StringBuilder();

                while( (line = bufferedReader.readLine()) != null )
                {
                    builder.append( line );
                }
                result = builder.toString();
            }
            else
            {
                result = "connection fail";
            }

        }
        catch (Exception e)
        {
            result = e.getStackTrace().toString();
        }
        return result;
    }

    @ResponseBody
    @GetMapping(value = {"/dt/event"})
    public String OnEvent() throws Exception  {

        String result = "";
        try {
            String requestMapJsonString = this.GetReqBodyEvent();

            HttpClient client =  HttpClientBuilder.create().build();
            //HttpPost httpPost = new HttpPost( "http://192.168.0.213:10002/acs/cubox/terminal" );
            HttpPost httpPost = new HttpPost( "http://localhost:8080/acs/v1/demo/device/dtmock/terminal" );

            StringEntity stringEntity = new StringEntity( requestMapJsonString );
            httpPost.setEntity( stringEntity );
            httpPost.setHeader( "Content-Type", "application/json; charset=UTF-8" );

            HttpResponse response = client.execute( httpPost );

            if ( response.getStatusLine().getStatusCode() == 200 ) {
                HttpEntity entity = response.getEntity();
                InputStreamReader inputStreamReader = new InputStreamReader( entity.getContent(), "UTF-8" );
                BufferedReader bufferedReader = new BufferedReader( inputStreamReader );
                String line = null;
                StringBuilder builder = new StringBuilder();

                while( (line = bufferedReader.readLine()) != null )
                {
                    builder.append( line );
                }
                result = builder.toString();
            }
            else
            {
                result = "connection fail";
            }

        }
        catch (Exception e)
        {
            result = e.getStackTrace().toString();
        }
        return result;
    }

    @ResponseBody
    @GetMapping(value = {"/dt/terminal/check"})
    public String OnTerminalCheck() throws Exception  {

        String result = this.GetReqBodyTerminal();

        return result;
    }

    @ResponseBody
    @GetMapping(value = {"/dt/event/check"})
    public String OnEventCheck() throws Exception  {

        String result = this.GetReqBodyEvent();

        return result;
    }

    public String GetReqBodyEvent() throws Exception
    {

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> requestBodyMap = new HashMap<>();

        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put( "terminalCd", "D0101-02R" );
        itemMap.put( "doorAlarmTyp", "DAT001" );
        itemMap.put( "evtDt", "2022-10-01 10:20:30.123456" );

        List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
        dataMap.add(itemMap);

        String dataJsonString = mapper.writeValueAsString( dataMap );

        String encodeDataJsonString = demoService.EncodeForDigitalTwin(dataJsonString);

        return encodeDataJsonString;
    }

    public String GetReqBodyTerminal() throws Exception
    {

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> requestBodyMap = new HashMap<>();

        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put( "terminalCd", "D0101-02R" );
        itemMap.put( "terminalTyp", "TMT002" );
        itemMap.put( "buildingCd", "03" );
        itemMap.put( "floorCd", "01" );
        itemMap.put( "doorNm", "3동-1층-주출입구2(IN)" );
        itemMap.put( "ipAddr", "123.123.123.123" );
        itemMap.put( "complexAuthTyp", "CAT001" );
        itemMap.put( "opModeTyp", "OMT001" );
        itemMap.put( "useYn", "Y" );
        itemMap.put( "deleteYn", "N" );
        itemMap.put( "createdAt", "2022-10-01 10:20:30.123456" );
        itemMap.put( "updatedAt", "2022-10-01 10:20:30.123456" );

        List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
        dataMap.add(itemMap);

        String dataJsonString = mapper.writeValueAsString( dataMap );

        String encodeDataJsonString = demoService.EncodeForDigitalTwin(dataJsonString);

        return encodeDataJsonString;
    }


    @ResponseBody
    @PostMapping(value = {"/device/dtmock/terminal"})
    public String DigitalTwinMock(@RequestBody String jsonBody) throws Exception {


        ObjectMapper resMapper = new ObjectMapper();

        String data = demoService.DecodeForDigitalTwin(jsonBody);
//        Map<String, String> map = resMapper.readValue( jsonBody, Map.class );
//        String key = map.get( "terminalCd" );

        return data;
    }


}
