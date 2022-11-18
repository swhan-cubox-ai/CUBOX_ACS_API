package aero.cubox.api;

import aero.cubox.api.face.service.FaceService;
import aero.cubox.api.util.CuboxTerminalUtil;
import cubl.util.AES256Util;
import net.minidev.json.parser.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;


@RunWith(SpringRunner.class)
@ActiveProfiles({"dev"})
@SpringBootTest
public class FaceTest {

    @Autowired
    private FaceService faceService;

    @Test
    public void insertFace() throws Exception {
        String upload_directory = "C:\\imgTmp";
        String move_directory = "C:\\imgBackup";
        File uploadFile = new File(upload_directory);
        File[] fileList = uploadFile.listFiles();
        for(int i=0; i<fileList.length; i++){
            File file = fileList[i];
            String fileName = file.getName();

            //파일명 => 직원코드 . 확장자
            int index = fileName.lastIndexOf(".");
            String extension = fileName.substring(index + 1);
            String emp_cd = fileName.substring(0,index);
            //byte[] face_img = imageToByteArray(file);
            byte[] face_img = Files.readAllBytes(file.toPath());

            Map<String, Object> faceInfo = new HashMap<>();
            faceInfo.put("emp_cd", emp_cd);
            faceInfo.put("face_img", face_img);
            faceInfo.put("face_state_typ", "FST001"); // 대기상태

            int insertFace = faceService.insertFace(faceInfo);

            // 파일 동기화 처리 이후 파일 이동.
            String filePath = file.getPath();
            Path resourcePath = Paths.get(filePath);
            Path backupPath = Paths.get(move_directory+"\\" + fileName);
            if(insertFace == 1){
                Files.move(resourcePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                System.out.println("error: checkFile " + fileName + " @@@@@@");
            }

        }
    }


    @Test
    public void getFeatures() throws ParseException, JSONException {
        List<Map<String, Object>> faceInfoList = faceService.getFace001();
        for(int i=0; i<faceInfoList.size(); i++) {
        //for(int i=69; i<72; i++) {
            Map<String, Object> faceInfoItem = faceInfoList.get(i);
            int cnt = 0;
            String cuboxStatus = cuboxApi(faceInfoItem);
            System.out.println(i + " data complete. cubox Status : " + cuboxStatus);
            if("ok".equals(cuboxStatus)) cnt++;

            String archeraStatus = archeraApi(faceInfoItem);
            System.out.println(i + " data complete.  Archera Status" + archeraStatus);
            if("ok".equals(archeraStatus)) cnt++;


            // cubox, Alchera 둘다 성공시 FACE 정보 업데이트.
            if(cnt == 2){
                faceInfoItem.put("face_state_typ", "FST002");
                faceService.updateFace(faceInfoItem);
            } else { // 둘중하나라도 실패시 추출실패.
                faceInfoItem.put("face_state_typ", "FST003");
                faceService.updateFace(faceInfoItem);
            }

        }

    }

    /**
     * cubox Api -- feature값 추출 후 DB저장
     * @param faceInfoItem
     * @return
     * @throws JSONException
     */
    public String cuboxApi(Map<String, Object> faceInfoItem) throws JSONException {
        String featureStatus = "err";
        byte[] arr = (byte[]) faceInfoItem.get("face_img");
        String filename = "tmp";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("accept", MediaType.APPLICATION_JSON_VALUE);

        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        ContentDisposition contentDisposition = ContentDisposition
                .builder("form-data")
                .name("image")
                .filename(filename)
                .build();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        fileMap.add(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
        HttpEntity<byte[]> fileEntity = new HttpEntity<>(arr, fileMap);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", fileEntity);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String serverUrl = "http://3.35.157.138:8081/v1/cpu/file/feature";

        RestTemplate restTemplate = new RestTemplate();
        try{

            ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);

            JSONObject jObj = new JSONObject(response.getBody());
            int statusCode = response.getStatusCodeValue();
            faceInfoItem.put("face_feature_typ" , "FFT001"); //씨유박스 CPU

            if (statusCode == 200) {
                // 성공
                String feature = (String) jObj.get("feature");
                faceInfoItem.put("feature" , feature);
                faceService.insertFaceFeature(faceInfoItem);
                featureStatus = "ok";
            } else if (statusCode == 422) {
                // Validation Error
                System.out.println(faceInfoItem.get("emp_cd"));
                System.out.println("data validation Error");
                faceInfoItem.put("error", "data validation Error. StatusCode = 422" );
                faceService.insertFaceFeatureErr(faceInfoItem); // 실패
            };
        } catch (Exception e){
            faceInfoItem.put("face_feature_typ" , "FFT001");
            faceInfoItem.put("error", e.getMessage());
            faceService.insertFaceFeatureErr(faceInfoItem); // 실패
        }

        return featureStatus;
    };

    /**
     * archera Api -- feature값 추출 후 DB저장
     * @param faceInfoItem
     * @return
     */
    public String archeraApi(Map<String, Object> faceInfoItem){
        String featureStatus = "err";
        byte[] arr = (byte[]) faceInfoItem.get("face_img");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(arr);
        RestTemplate restTemplate = new RestTemplate();

        try{
            ResponseEntity<String> response = restTemplate.exchange("http://192.168.26.188:9297/facefeatures", HttpMethod.POST, requestEntity , String.class);
            JSONObject jObj = new JSONObject(response.getBody());
            int statusCode = response.getStatusCodeValue();
            faceInfoItem.put("face_feature_typ" , "FFT003"); // 알체라 CPU
            int count = jObj.getInt("count");
            JSONObject returnMsgO = jObj.getJSONObject("return_msg");
            String returnCd = (String) returnMsgO.get("return_code");
            String returnMsg = (String) returnMsgO.get("return_msg");

            if(count == 1){ // feature를 추출하지 못한 파일도 성공코드로 출력하여, count 0 일경우만 정상으로 간주.
                if (statusCode == 200) {
                    // 성공 -- 추출된 feature 값을 바이트배열변환, 암호화 처리후 저장
                    JSONArray feturesInfo = jObj.getJSONArray("faces_with_features");
                    JSONObject featuresInfo = feturesInfo.getJSONObject(0);
                    JSONArray featuresJSON = featuresInfo.getJSONArray("feature_vector");
                    float[] vectors = new float[featuresJSON.length()];
                    for (int k=0; k<featuresJSON.length(); k++){
                        float vector = Float.parseFloat(featuresJSON.get(k).toString());
                        vectors[k] = vector;
                    }

                    byte[] bytes = CuboxTerminalUtil.floatArrayToByteArray(vectors);
                    String feature = CuboxTerminalUtil.byteArrEncode(bytes);

                    faceInfoItem.put("feature" , feature);
                    faceService.insertFaceFeature(faceInfoItem); // 성공
                    featureStatus = "ok";
                } else if (statusCode == 400) {
                    // 입력 처리 실패
                    faceInfoItem.put("error", "Data validation Error. return_code : " + returnCd + "return_msg : " + returnMsg );
                    faceService.insertFaceFeatureErr(faceInfoItem); // 실패
                } else if (statusCode == 406){
                    //API 사용 제한
                    faceInfoItem.put("error", "API usage restrictions. return_code : " + returnCd + "return_msg : " + returnMsg );
                    faceService.insertFaceFeatureErr(faceInfoItem); // 실패
                } else if (statusCode == 500){
                    //실패
                    faceInfoItem.put("error", "Fail. return_code : " + returnCd + "return_msg : " + returnMsg );
                    faceService.insertFaceFeatureErr(faceInfoItem); // 실패
                }
            } else {
                faceInfoItem.put("error", "Features is null. return_code : " + returnCd + "return_msg : " + returnMsg );
                faceService.insertFaceFeatureErr(faceInfoItem); // 실패
            }
        } catch (Exception e){ // 기타 오류에대한 처리
            faceInfoItem.put("face_feature_typ" , "FFT003");
            faceInfoItem.put("error", e.getMessage());
            faceService.insertFaceFeatureErr(faceInfoItem); // 실패
        }

        return featureStatus;
    }

}



