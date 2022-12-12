package aero.cubox.api.deptemp.service;

import aero.cubox.api.domain.entity.Emp;
import aero.cubox.api.domain.entity.Face;
import aero.cubox.api.domain.entity.FaceFeature;
import aero.cubox.api.domain.entity.FaceFeatureErr;
import aero.cubox.api.util.CuboxTerminalUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
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
import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
@EnableScheduling
//@Profile("imsimdm")
@Profile("facefeature")
//@Profile("local")
public class FaceFeatureScheduleService {

    @Autowired
    private FaceService faceService;


    @Autowired
    private EmpService empService;
    @Autowired
    private FaceFeatureService faceFeatureService;



    @Value("${cuboxacs.upload_directory}")
    String upload_directory;
    @Value("${cuboxacs.move_directory}")
    String move_directory;

    @Value("${cuboxacs.cubox_frs_host}")
    String cubox_host;

    @Value("${cuboxacs.archera_api_host}")
    String archera_host;

    @Scheduled(cron = "0/10 * * * * *")
    public void getFeatures() throws JSONException {

        log.info("getFeatures....");

        while ( true )
        {
            List<Face> faceList = faceService.findTop100ByFaceStateTyp("FST001"); // 대기상태
            if ( faceList.size() == 0)
            {
                break;
            }

            for(int i=0; i<faceList.size(); i++) {
                FaceFeature faceFeature  = new FaceFeature();
                FaceFeature faceFeature2  = new FaceFeature();

                Face face = faceList.get(i);

                String archeraOk = "N";

                String cuboxStatus = cuboxApi(face, faceFeature);
                log.info(i + " data complete. cubox Status : " + cuboxStatus);

                //String archeraStatus = archeraApi(face, faceFeature);
                String archeraStatus = archeraApiTmp(face, faceFeature2);
                log.info(i + " data complete.  Archera Status " + archeraStatus);
                if("ok".equals(archeraStatus)) archeraOk = "Y";


                // Alchera 성공시 FACE 정보 업데이트.
                if("Y".equals(archeraOk)){ //알체라 성공시 성공
                    face.setFaceStateTyp("FST002"); // 성공
                } else if ("N".equals(archeraOk)){ // 알체라실패시 실패
                    face.setFaceStateTyp("FST003"); // 실패
                }

                // 이미지 특징점 추출 성공과 관계없이 T_EMP.face_id 갱신
                Optional<Emp> oEmp = empService.findByEmpCd(face.getEmpCd());
                if ( oEmp.isPresent())
                {
                    Emp emp  = oEmp.get();
                    emp.setFaceId(face.getId());
                    emp.setUpdatedAt(new Timestamp(new Date().getTime()));
                    empService.save(emp);
                }

                faceService.save(face);

            }
        }

    }

    /**
     * cubox Api -- feature값 추출 후 DB저장
     * @param face
     * @return
     * @throws JSONException
     */
    public String cuboxApi(Face face, FaceFeature faceFeature) throws JSONException {
        String featureStatus = "err";

        byte[] arr = (byte[]) face.getFaceImg();
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

            if (statusCode == 200) {

                String feature = (String) jObj.get("feature");

                faceFeature.setFaceId(face.getId());
                faceFeature.setEmpCd(face.getEmpCd());
                faceFeature.setFeature(feature);
                faceFeature.setFaceFeatureTyp("FFT001");
                faceFeature.setCreatedAt(new Timestamp(new Date().getTime()));
                faceFeature.setUpdatedAt(new Timestamp(new Date().getTime()));
                faceFeatureService.save(faceFeature);

                featureStatus = "ok";

            } else {
                // Validation Error
                SaveError(face, "FFT001", "data validation Error. StatusCode : " + statusCode);
            };


        } catch (Exception e){
            SaveError(face, "FFT001", e.getMessage());
        }

        return featureStatus;
    };

    public void SaveError(Face face, String  faceStateTyp, String errMsg)
    {
        FaceFeatureErr faceFeatureErr = FaceFeatureErr.builder()
                .faceId(face.getId())
                .empCd(face.getEmpCd())
                .faceFeatureTyp(faceStateTyp) //씨유박스 CPU
                .error(errMsg)
                .createdAt(new Timestamp(new Date().getTime()))
                .updateAt(new Timestamp(new Date().getTime()))
                .build();
        faceService.saveFaceFeatrueErr(faceFeatureErr);
    }



    public String archeraApiTmp(Face face, FaceFeature faceFeature) {
        // lm_confidence 조회
        Map<String, Object> landmark_result = getArcheraResponse(face, faceFeature, "/faces");
        String landmark_status = String.valueOf(landmark_result.get("status"));

        if("ok".equals(landmark_status)){
            JSONObject landmarkObject = (JSONObject) landmark_result.get("body");
            int count = landmarkObject.getInt("count");
            if(count == 0){
                SaveError(face, "FFT003", "[/faces] face Found  null" );
                return landmark_status;
            }
            JSONArray landmarkInfoS = landmarkObject.getJSONArray("faces");
            JSONObject landmarkInfo = landmarkInfoS.getJSONObject(0);
            JSONObject landmarkJSON = landmarkInfo.getJSONObject("landmark");
            // 성공 -- 랜드마크점수가 기준점이상이면 성공처리
            float landmark = Float.parseFloat(String.valueOf(landmarkJSON.get("lm_confidence")));
            // 0.88 초과 성공
            if(landmark >0.88){
                landmark_status = archeraFeatureApi(face, faceFeature);
            } else { // 0.88 이하 실패
                SaveError(face, "FFT003", "[_] landmark UnderScore : " + landmark);
            }
        }
        return landmark_status;
    }

    public String archeraFeatureApi(Face face, FaceFeature faceFeature){
        Map<String, Object> feature_result = getArcheraResponse(face, faceFeature, "/facefeatures/masking");
        String feature_status = String.valueOf(feature_result.get("status"));
        try{

            if("ok".equals(feature_status)) {
                JSONObject featureObject = (JSONObject) feature_result.get("body");

                // 성공 -- 추출된 feature 값을 바이트배열변환, 암호화 처리후 저장
                JSONArray featuresInfoS = featureObject.getJSONArray("faces_with_masked_features");
                JSONObject featuresInfo = featuresInfoS.getJSONObject(0);
                JSONArray featuresJSON = featuresInfo.getJSONArray("normal_feature");
                float[] vectors = new float[featuresJSON.length()];
                for (int k=0; k<featuresJSON.length(); k++){
                    float vector = Float.parseFloat(featuresJSON.get(k).toString());
                    vectors[k] = vector;
                }

                byte[] bytes = CuboxTerminalUtil.floatArrayToByteArray(vectors);
                String feature = CuboxTerminalUtil.byteArrEncode(bytes);

                JSONArray maskJSON = featuresInfo.getJSONArray("masked_feature");
                float[] vectors2 = new float[maskJSON.length()];
                for (int k=0; k<maskJSON.length(); k++){
                    float vector = Float.parseFloat(maskJSON.get(k).toString());
                    vectors2[k] = vector;
                }

                byte[] maskBytes = CuboxTerminalUtil.floatArrayToByteArray(vectors2);
                String maskFeature = CuboxTerminalUtil.byteArrEncode(maskBytes);

                faceFeature.setFaceId(face.getId());
                faceFeature.setEmpCd(face.getEmpCd());
                faceFeature.setFeature(feature);
                faceFeature.setFeatureMask(maskFeature);
                faceFeature.setFaceFeatureTyp("FFT003");
                faceFeature.setCreatedAt(new Timestamp(new Date().getTime()));
                faceFeature.setUpdatedAt(new Timestamp(new Date().getTime()));
                faceFeatureService.save(faceFeature);
            }
        } catch (Exception ex){
            SaveError(face, "FFT003",  "[ /facefeatures/masking ] " + ex.getMessage());
        }

        return feature_status;
    }

    public Map<String, Object> getArcheraResponse(Face face, FaceFeature faceFeature, String url){
        byte[] arr = face.getFaceImg();

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(arr);
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("status", "err");

        try{
            ResponseEntity<String> response = restTemplate.exchange(archera_host + url, HttpMethod.POST, requestEntity , String.class);

            JSONObject jBody = new JSONObject(response.getBody());
            int statusCode = response.getStatusCodeValue();

            int count = jBody.getInt("count");
            JSONObject returnMsgO = jBody.getJSONObject("return_msg");
            String returnCd = (String) returnMsgO.get("return_code");
            String returnMsg = (String) returnMsgO.get("return_msg");

            if(statusCode == 200){
                responseResult.put("status", "ok");
                if(count == 0){
                    SaveError(face, "FFT003", "[ " +url+" ] Data is null. return_code:" + returnCd + " return_msg:" + returnMsg);
                    responseResult.put("status", "err");
                    return responseResult;
                }
                responseResult.put("count", count);
                responseResult.put("body", jBody);
            } else if (statusCode == 400) {
                SaveError(face, "FFT003", "[ " +url+" ] Data validation Error. return_code:" + returnCd + " return_msg:" + returnMsg);
            } else if (statusCode == 406){
                //API 사용 제한
                SaveError(face, "FFT003", "[ " +url+" ] API usage restrictions. return_code:" + returnCd + "return_msg:" + returnMsg );
            } else if (statusCode == 500){
                //실패
                SaveError(face, "FFT003", "[ " +url+" ] Fail. return_code:" + returnCd + " return_msg:" + returnMsg );
            } else {
                //실패
                SaveError(face, "FFT003", "[ " +url+" ] Fail. statusCode:" + statusCode + " returnCd:" + returnMsg + " return_msg:" + returnMsg );
            }

        } catch (Exception ex){
            // 예외처리 오류 처리
            SaveError(face, "FFT003",  "[ " +url+" ] " + ex.getMessage());
            responseResult.put("status", "ex");
        }
        return responseResult;
    }

    /**
     * archera Api -- feature값 추출 후 DB저장
     * @param face
     * @return
     */
    public String archeraApi(Face face, FaceFeature faceFeature){

        String featureStatus = "err";

        byte[] arr = face.getFaceImg();

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(arr);
        RestTemplate restTemplate = new RestTemplate();

        try{
            ResponseEntity<String> response = restTemplate.exchange(archera_host + "/facefeatures/masking", HttpMethod.POST, requestEntity , String.class);
            JSONObject jObj = new JSONObject(response.getBody());
            int statusCode = response.getStatusCodeValue();

            int count = jObj.getInt("count");
            JSONObject returnMsgO = jObj.getJSONObject("return_msg");
            String returnCd = (String) returnMsgO.get("return_code");
            String returnMsg = (String) returnMsgO.get("return_msg");

            if(count == 1){ // feature를 추출하지 못한 파일도 성공코드로 출력하여, count 0 일경우만 정상으로 간주.
                if (statusCode == 200) {
                    // 성공 -- 추출된 feature 값을 바이트배열변환, 암호화 처리후 저장
                    JSONArray feturesInfo = jObj.getJSONArray("faces_with_masked_features");
                    JSONObject featuresInfo = feturesInfo.getJSONObject(0);
                    JSONArray featuresJSON = featuresInfo.getJSONArray("normal_feature");
                    float[] vectors = new float[featuresJSON.length()];
                    for (int k=0; k<featuresJSON.length(); k++){
                        float vector = Float.parseFloat(featuresJSON.get(k).toString());
                        vectors[k] = vector;
                    }

                    byte[] bytes = CuboxTerminalUtil.floatArrayToByteArray(vectors);
                    String feature = CuboxTerminalUtil.byteArrEncode(bytes);

                    JSONArray maskJSON = featuresInfo.getJSONArray("masked_feature");
                    float[] vectors2 = new float[maskJSON.length()];
                    for (int k=0; k<maskJSON.length(); k++){
                        float vector = Float.parseFloat(maskJSON.get(k).toString());
                        vectors2[k] = vector;
                    }

                    byte[] maskBytes = CuboxTerminalUtil.floatArrayToByteArray(vectors2);
                    String maskFeature = CuboxTerminalUtil.byteArrEncode(maskBytes);

                    faceFeature.setFaceId(face.getId());
                    faceFeature.setEmpCd(face.getEmpCd());
                    faceFeature.setFeature(feature);
                    faceFeature.setFeatureMask(maskFeature);
                    faceFeature.setFaceFeatureTyp("FFT003");
                    faceFeature.setCreatedAt(new Timestamp(new Date().getTime()));
                    faceFeature.setUpdatedAt(new Timestamp(new Date().getTime()));
                    faceFeatureService.save(faceFeature);
                    //faceService.saveFaceFeatrue(faceFeature);
                    featureStatus = "ok";
                } else if (statusCode == 400) {
                    SaveError(face, "FFT003", "[features] Data validation Error. return_code:" + returnCd + " return_msg:" + returnMsg);
                } else if (statusCode == 406){
                    //API 사용 제한
                    SaveError(face, "FFT003", "[features] API usage restrictions. return_code:" + returnCd + "return_msg:" + returnMsg );
                } else if (statusCode == 500){
                    //실패
                    SaveError(face, "FFT003", "[features] Fail. return_code:" + returnCd + " return_msg:" + returnMsg );
                } else {
                    //실패
                    SaveError(face, "FFT003", "[features] Fail. statusCode:" + statusCode + " returnCd:" + returnMsg + " return_msg:" + returnMsg );
                }
            } else {
                SaveError(face, "FFT003", "[features] Features is null. return_code:" + returnCd + " return_msg:" + returnMsg );
            }
        } catch (Exception e){ // 기타 오류에대한 처리
            SaveError(face, "FFT003",  "[features] " + e.getMessage());
        }

        return featureStatus;
    }
}
