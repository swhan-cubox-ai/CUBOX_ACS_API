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
//@Profile("facefeature")
@Profile("local")
public class FaceFeatureScheduleService {

    @Autowired
    private FaceService faceService;


    @Autowired
    private EmpService empService;

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

                Face face = faceList.get(i);

                String archeraOk = "N";
                String cuboxOk = "N";

                int cnt = 0;
                String cuboxStatus = cuboxApi(face);
                log.info(i + " data complete. cubox Status : " + cuboxStatus);
//                if("ok".equals(cuboxStatus)) cnt++;
                if("ok".equals(cuboxStatus)) cuboxOk = "Y";

                String archeraStatus = archeraApi(face);
                log.info(i + " data complete.  Archera Status" + archeraStatus);
//                if("ok".equals(archeraStatus)) cnt++;
                if("ok".equals(archeraStatus)) archeraOk = "Y";

//                // cubox, Alchera 둘다 성공시 FACE 정보 업데이트.
//                if(cnt == 2){
//                    face.setFaceStateTyp("FST002"); // 성공
//
//                    // 이미지 특징점 추출 성공 시 T_EMP.face_id 갱신
//                    Optional<Emp> oEmp = empService.findByEmpCd(face.getEmpCd());
//                    if ( oEmp.isPresent())
//                    {
//                        Emp emp  = oEmp.get();
//                        emp.setFaceId(face.getId());
//                        emp.setUpdatedAt(new Timestamp(new Date().getTime()));
//                        empService.save(emp);
//                    }
//
//                } else { // 둘중하나라도 실패시 추출실패.
//                    face.setFaceStateTyp("FST003"); // 실패
//                }
//                faceService.save(face);

                // cubox, Alchera 둘다 성공시 FACE 정보 업데이트.
                if("Y".equals(archeraOk)){ //알체라 성공시 성공
                    face.setFaceStateTyp("FST002"); // 성공

                    // 이미지 특징점 추출 성공 시 T_EMP.face_id 갱신
                    Optional<Emp> oEmp = empService.findByEmpCd(face.getEmpCd());
                    if ( oEmp.isPresent())
                    {
                        Emp emp  = oEmp.get();
                        emp.setFaceId(face.getId());
                        emp.setUpdatedAt(new Timestamp(new Date().getTime()));
                        empService.save(emp);
                    }

                } else if ("N".equals(archeraOk)){ // 알체라실패시 실패
                    face.setFaceStateTyp("FST003"); // 실패
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
    public String cuboxApi(Face face) throws JSONException {
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

            //face.setFaceStateTyp("FFT001");  //씨유박스 CPU

            if (statusCode == 200) {

                String feature = (String) jObj.get("feature");

                FaceFeature faceFeature = null;
//                Optional<FaceFeature> oFaceFeature = faceService.findFaceFeatrue(face.getId(), face.getEmpCd(), "FFT001");
//                if ( oFaceFeature.isEmpty())
//                {
//                    faceFeature = FaceFeature.builder()
//                            .faceId(face.getId())
//                            .empCd(face.getEmpCd())
//                            .faceFeatureTyp("FFT001") //씨유박스 CPU
//                            .feature(feature)
//                            .createdAt(new Timestamp(new Date().getTime()))
//                            .build();
//                }
//                else
//                {
//                    faceFeature = oFaceFeature.get();
//                    faceFeature.setFaceId(face.getId());
//                    faceFeature.setEmpCd(face.getEmpCd());
//                    faceFeature.setFaceFeatureTyp("FFT001"); //씨유박스 CPU;
//                    faceFeature.setFeature(feature);
//                    faceFeature.setCreatedAt(new Timestamp(new Date().getTime()));
//                }
                faceFeature = FaceFeature.builder()
                    .faceId(face.getId())
                    .empCd(face.getEmpCd())
                    .faceFeatureTyp("FFT001") //씨유박스 CPU
                    .feature(feature)
                    .createdAt(new Timestamp(new Date().getTime()))
                    .updatedAt(new Timestamp(new Date().getTime()))
                    .build();
                faceService.saveFaceFeatrue(faceFeature);

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

    /**
     * archera Api -- feature값 추출 후 DB저장
     * @param face
     * @return
     */
    public String archeraApi(Face face){

        String featureStatus = "err";

        byte[] arr = (byte[]) face.getFaceImg();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(arr);
        RestTemplate restTemplate = new RestTemplate();

        try{
            ResponseEntity<String> response = restTemplate.exchange(archera_host + "/facefeatures", HttpMethod.POST, requestEntity , String.class);
            JSONObject jObj = new JSONObject(response.getBody());
            int statusCode = response.getStatusCodeValue();

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

                    // alignedFace , masking 진행후 특징점을 리턴
                    Map<String, String> alignResult = alignFace(requestEntity);
                    String alignStatus = alignResult.get("alignStatus"); // ok or err
                    String alignStatusCode = alignResult.get("alignStatusCode");

                    // 이미지 정렬이 성공하면 mask_face 진행
                    if("ok".equals(alignStatus)){
                        String alignedImage = alignResult.get("alignedImage");
                        byte[] decodedBytes = Base64.getDecoder().decode(alignedImage);

                        Map<String, String> maskResult = featureMask(decodedBytes);

                        String maskStatus = maskResult.get("maskStatus"); // ok or err
                        String maskStatusCode = maskResult.get("alignStatusCode");

                        // faceFeatures, align, mask 모두 성공시 저장
                        if("ok".equals(maskStatus)){
                            String feature_mask = maskResult.get("maskFeature");
                            FaceFeature faceFeature = null;
                            faceFeature = FaceFeature.builder()
                                    .faceId(face.getId())
                                    .empCd(face.getEmpCd())
                                    .faceFeatureTyp("FFT003") //archera CPU
                                    .feature(feature)
                                    .featureMask(feature_mask)
                                    .createdAt(new Timestamp(new Date().getTime()))
                                    .build();
                            faceService.saveFaceFeatrue(faceFeature);

                            featureStatus = "ok";
                        } else if ("err".equals(maskStatus)) {  //feature 오류저장처리
                            String maskReturnCd = maskResult.get("returnCd");
                            String maskReturnMsg = maskResult.get("returnMsg");
                            saveErrorArchera(face, "[mask] ", maskStatusCode, maskReturnCd, maskReturnMsg);
                        } else if("ex".equals(maskStatus)){
                            String exceptionStr = maskResult.get("exception");
                            SaveError(face, "FFT003", exceptionStr);
                        }
                    } else if("err".equals(alignStatus)) { //align 오류저장처리
                        String alignReturnCd = alignResult.get("returnCd");
                        String alignReturnMsg = alignResult.get("returnMsg");
                        saveErrorArchera(face, "[align] ", alignStatusCode, alignReturnCd, alignReturnMsg);
                    } else if("ex".equals(alignStatusCode)){ //align 오류저장처리
                        String exceptionStr = alignResult.get("exception");
                        SaveError(face, "FFT003", exceptionStr);
                    }
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

    private void saveErrorArchera(Face face,String api, String statusCode, String returnCd, String returnMsg){
        if ("400".equals(statusCode)) {
            SaveError(face, "FFT003", api + "Data validation Error. return_code:" + returnCd + " return_msg:" + returnMsg);
        } else if ("406".equals(statusCode)){
            SaveError(face, "FFT003", api +"API usage restrictions. return_code:" + returnCd + "return_msg:" + returnMsg );
        } else if ("500".equals(statusCode)){
            SaveError(face, "FFT003", api +"Fail. return_code:" + returnCd + " return_msg:" + returnMsg );
        } else {
            SaveError(face, "FFT003", api + "Fail. statusCode:" + statusCode + " returnCd:" + returnMsg + " return_msg:" + returnMsg );
        }
    }

    private Map<String, String> alignFace(HttpEntity<byte[]> requestEntity){
        Map<String, String> alignResult = new HashMap<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);


        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<String> response2 = restTemplate.exchange(archera_host + "/alignedfaces", HttpMethod.POST, requestEntity , String.class);
            JSONObject jObj2 = new JSONObject(response2.getBody());

            int statusCode = response2.getStatusCodeValue();
            int count = jObj2.getInt("count");

            JSONObject returnMsgO = jObj2.getJSONObject("return_msg");
            String returnCd = (String) returnMsgO.get("return_code");
            String returnMsg = (String) returnMsgO.get("return_msg");

            alignResult.put("statusCode", Integer.toString(statusCode));
            alignResult.put("alignStatus", "err");
            if(count == 1 && statusCode == 200 ){ // image를 추출하지 못한 파일도 성공코드로 출력하여, count 1 일경우만 정상으로 간주.
                // 성공 -- 추출된 정렬된 이미지를 리턴
                JSONArray alignedFaceInfo = jObj2.getJSONArray("aligned_faces");
                JSONObject alignedImageInfo = alignedFaceInfo.getJSONObject(0);
                String alignedImage = alignedImageInfo.getString("image_data");

                alignResult.put("alignedImage", alignedImage);
                alignResult.replace("alignStatus", "ok");
            } else {
                alignResult.put("returnCd", returnCd);
                alignResult.put("returnMsg", returnMsg);
            }

        } catch (Exception e){ // 기타 오류에대한 처리
            alignResult.put("alignStatus", "ex");
            alignResult.put("exception", "[align] " + e.getMessage());
            return alignResult;
        }

        return alignResult;
    }

    private Map<String, String> featureMask(byte[] decodedBytes){

        Map<String, String> maskResult = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(decodedBytes);

        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<String> response = restTemplate.exchange(archera_host + "/feature/masking", HttpMethod.POST, requestEntity , String.class);
            JSONObject jObj = new JSONObject(response.getBody());

            int statusCode = response.getStatusCodeValue();

            JSONObject returnMsgO = jObj.getJSONObject("return_msg");
            String returnCd = (String) returnMsgO.get("return_code");
            String returnMsg = (String) returnMsgO.get("return_msg");

            maskResult.put("statusCode", Integer.toString(statusCode));
            maskResult.put("maskStatus", "err");
            if(statusCode == 200 ){
                // 성공 -- 추출된 정렬된 이미지를 리턴
                String maskFeature = jObj.getString("masked_feature");

                maskResult.put("maskFeature", maskFeature);
                maskResult.replace("maskStatus", "ok");
            } else {
                maskResult.put("returnCd", returnCd);
                maskResult.put("returnMsg", returnMsg);
            }

        } catch (Exception e){ // 기타 오류에대한 처리
            maskResult.put("maskStatus", "ex");
            maskResult.put("exception", "[mask] " +e.getMessage());
            return maskResult;
        }

        return maskResult;
    }


}
