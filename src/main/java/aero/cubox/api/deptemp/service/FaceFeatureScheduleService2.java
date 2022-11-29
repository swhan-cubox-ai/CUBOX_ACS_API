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

import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
@EnableScheduling
@Profile("facefeature2")
public class FaceFeatureScheduleService2 {

    @Autowired
    private FaceService faceService;

    @Autowired
    private FaceFeatureService faceFeatureService;

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
    public void getFeatures2() throws JSONException {

        log.info("getFeatures2....");

        while ( true )
        {
            List<FaceFeature> faceFeatureList = faceFeatureService.findAllByFaceFeatureAlchera();
            //List<Face> faceList = faceService.getFaceFeatureMasknull(); //
            if ( faceFeatureList.size() == 0)
            {
                break;
            }

            for(int i=0; i<faceFeatureList.size(); i++) {

                FaceFeature faceFeature = faceFeatureList.get(i);

                Optional<Face> oFace = faceService.fetchById(faceFeature.getFaceId());
                if ( oFace.isEmpty() )
                {
                    // no expected
                    faceFeatureService.delete(faceFeature.getId());
                }
                else
                {
                    Face face = oFace.get();
                    String archeraStatus = archeraApi(face);
                    log.info(i + " data complete.  Archera Status" + archeraStatus);
                    if("ok".equals(archeraStatus)){
                        face.setUpdatedAt(new Timestamp(new Date().getTime()));
                        log.info("#### " + face.getId() + "### save  ########## ");
                        faceService.save(face);
                    }
                    else {
                        // 실패 시 feature 도 삭제
                        faceFeatureService.delete(faceFeature.getId());
                    }
                }


            }
        }

    }


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
        System.out.println(faceFeatureErr);
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



                    // STEP 2 - mask feature

                    // /alignedfaces
                    // alignedFace , masking 진행후 특징점을 리턴
                    Map<String, String> alignResult = alignFace(requestEntity);
                    String alignStatus = alignResult.get("alignStatus"); // ok or err
                    String alignStatusCode = alignResult.get("alignStatusCode");

                    // 이미지 정렬이 성공하면 mask_face 진행
                    if("ok".equals(alignStatus)){
                        String alignedImage = alignResult.get("alignedImage");
                        byte[] decodedBytes = Base64.getDecoder().decode(alignedImage);

                        // /feature/masking
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
                            log.info(String.valueOf(face.getId())+ "= id #########");
                            log.info(feature);
                            log.info(feature_mask);
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
