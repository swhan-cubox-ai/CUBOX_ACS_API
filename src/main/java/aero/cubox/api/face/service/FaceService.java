package aero.cubox.api.face.service;

import aero.cubox.api.face.mapper.FaceMapper;
import aero.cubox.api.util.CuboxTerminalUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@EnableScheduling
public class FaceService {

    @Autowired
    private FaceMapper mapper;

    @Value("${cuboxacs.upload_directory}")
    String upload_directory;
    @Value("${cuboxacs.move_directory}")
    String move_directory;

    @Value("${cuboxacs.cubox_frs_host}")
    String cubox_host;

    @Value("${cuboxacs.archera_api_host}")
    String archera_host;

    @Scheduled(cron = "0/10 * * * * *")
    public void insertFace() throws Exception {
        log.debug("insertFace....");
        File uploadFile = new File(upload_directory);
        File[] fileList = uploadFile.listFiles();
        for(int i=0; i<fileList.length; i++){
            File file = fileList[i];
            String fileName = file.getName();

            //파일명 => 직원코드 . 확장자
            int index = fileName.lastIndexOf(".");
            String extension = fileName.substring(index + 1);
            String emp_cd = fileName.substring(0,index);
            byte[] face_img = Files.readAllBytes(file.toPath());

            Map<String, Object> faceInfo = new HashMap<>();
            faceInfo.put("emp_cd", emp_cd);
            faceInfo.put("face_img", face_img);
            faceInfo.put("face_state_typ", "FST001"); // 대기상태

            int insertFace = insertFace(faceInfo);

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

    @Scheduled(cron = "0/10 * * * * *")
    public void getFeatures() throws JSONException {
        log.debug("getFeatures....");
        List<Map<String, Object>> faceInfoList = getFace001();
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
                updateFace(faceInfoItem);
            } else { // 둘중하나라도 실패시 추출실패.
                faceInfoItem.put("face_state_typ", "FST003");
                updateFace(faceInfoItem);
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

        String serverUrl = cubox_host + "/v1/cpu/file/feature";

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
                insertFaceFeature(faceInfoItem);
                featureStatus = "ok";
            } else if (statusCode == 422) {
                // Validation Error
                System.out.println(faceInfoItem.get("emp_cd"));
                System.out.println("data validation Error");
                faceInfoItem.put("error", "data validation Error. StatusCode = 422" );
                insertFaceFeatureErr(faceInfoItem); // 실패
            };
        } catch (Exception e){
            faceInfoItem.put("face_feature_typ" , "FFT001");
            faceInfoItem.put("error", e.getMessage());
            insertFaceFeatureErr(faceInfoItem); // 실패
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
            ResponseEntity<String> response = restTemplate.exchange(archera_host + "/facefeatures", HttpMethod.POST, requestEntity , String.class);
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
                    insertFaceFeature(faceInfoItem); // 성공
                    featureStatus = "ok";
                } else if (statusCode == 400) {
                    // 입력 처리 실패
                    faceInfoItem.put("error", "Data validation Error. return_code : " + returnCd + "return_msg : " + returnMsg );
                    insertFaceFeatureErr(faceInfoItem); // 실패
                } else if (statusCode == 406){
                    //API 사용 제한
                    faceInfoItem.put("error", "API usage restrictions. return_code : " + returnCd + "return_msg : " + returnMsg );
                    insertFaceFeatureErr(faceInfoItem); // 실패
                } else if (statusCode == 500){
                    //실패
                    faceInfoItem.put("error", "Fail. return_code : " + returnCd + "return_msg : " + returnMsg );
                    insertFaceFeatureErr(faceInfoItem); // 실패
                }
            } else {
                faceInfoItem.put("error", "Features is null. return_code : " + returnCd + "return_msg : " + returnMsg );
                insertFaceFeatureErr(faceInfoItem); // 실패
            }
        } catch (Exception e){ // 기타 오류에대한 처리
            faceInfoItem.put("face_feature_typ" , "FFT003");
            faceInfoItem.put("error", e.getMessage());
            insertFaceFeatureErr(faceInfoItem); // 실패
        }

        return featureStatus;
    }

    // T_FACE insert
    public int insertFace(Map<String, Object> faceInfo){
        int insertFace = mapper.insertFace(faceInfo);

        return insertFace;
    }

    // T_FACE id, 사진, emp_cd
    public List<Map<String, Object>> getFace001(){
        List<Map<String, Object>> faceInfoList = mapper.getFace001();

        return faceInfoList;
    }

    // T_FACE 추출성공 feature 저장
    public int insertFaceFeature(Map<String, Object> faceInfo){
        int insertFace = mapper.insertFaceFeature(faceInfo);

        return insertFace;
    }

    // T_FACE 추출 실패 error 저장
    public int insertFaceFeatureErr(Map<String, Object> faceInfo){
        int insertFace = mapper.insertFaceFeatureErr(faceInfo);

        return insertFace;
    }

    // 추출완료후 T_FACE 업데이트
    public int updateFace(Map<String, Object> faceInfo){
        int updateCnt = mapper.updateFace(faceInfo);

        return updateCnt;
    }


}
