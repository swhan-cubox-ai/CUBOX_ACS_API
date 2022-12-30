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
@Profile("notuse")
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

    //@Scheduled(cron = "0/10 * * * * *")
    public void getFeatures2() throws JSONException {

        log.info("getFeatures2....");

        while ( true )
        {
            List<FaceFeature> faceFeatureList = faceFeatureService.find2732Data();
            if ( faceFeatureList.size() == 0)
            {
                System.exit(0);
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
                    String archeraStatus = archeraApi(face, faceFeature);
                    log.info(i + " data complete.  Archera Status" + archeraStatus);
                    if("ok".equals(archeraStatus)){
                        face.setFaceStateTyp("FST002");
                        face.setUpdatedAt(new Timestamp(new Date().getTime()));
                        log.info("#### " + face.getId() + "### save  ########## ");
                        faceService.save(face);
                    }
                    else {
                        // 실패 시 feature 도 삭제
                        faceFeatureService.delete(faceFeature.getId());
                        face.setFaceStateTyp("FST003");
                        face.setUpdatedAt(new Timestamp(new Date().getTime()));
                        log.info("#### " + face.getId() + "### fail  ########## ");
                        faceService.save(face);
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
        log.info("##########"+faceFeatureErr.getEmpCd() + "error ####################");
        log.info(faceFeatureErr.getError());
        faceService.saveFaceFeatrueErr(faceFeatureErr);
    }

    /**
     * archera Api -- feature값 추출 후 DB저장
     * @param face
     * @return
     */
    public String archeraApi(Face face, FaceFeature faceFeature){

        String featureStatus = "err";

        byte[] arr = (byte[]) face.getFaceImg();

        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.TEXT_PLAIN);
//        headers.setContentType(MediaType.IMAGE_JPEG);



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


                    faceFeature.setFeature(feature);
                    faceFeature.setFeatureMask(maskFeature);
                    faceFeature.setUpdatedAt(new Timestamp(new Date().getTime()));
                    faceFeatureService.save(faceFeature);
//                    FaceFeature faceFeature = null;
//                    faceFeature = FaceFeature.builder()
//                            .faceId(face.getId())
//                            .empCd(face.getEmpCd())
//                            .faceFeatureTyp("FFT003") //archera CPU
//                            .feature(feature)
//                            .featureMask(maskFeature)
//                            .createdAt(new Timestamp(new Date().getTime()))
//                            .build();
//                    faceService.saveFaceFeatrue(faceFeature);
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

    //@Scheduled(cron = "0/10 * * * * *")
    public void setLandmark (){ // 랜드마크 적용 전용 서비스
        int id = 243430;
        while ( true )
        {
            List<FaceFeature> faceFeatureList = faceFeatureService.getFeatureListForLandmark(id);

            if ( faceFeatureList.size() == 0)
            {
                System.exit(0);
                //break;
            }

            for(int i=0; i<faceFeatureList.size(); i++) {

                FaceFeature faceFeature = faceFeatureList.get(i);
                id = faceFeature.getId();
                String result = faceFeatureService.getLandmarkScore(faceFeature);

                if ("ok".equals(result)) {
                    //성공시 facefeature 갱신
                    System.out.println("성공@@@@@@@@@@@@@@@@@@@@  " + id + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                } else if ("fail".equals(result)) {
                    //실패시 feature 삭제, 페이스상태 실패로 갱신
                    System.out.println("실패@@@@@@@@@@@@@@@@@@@@  " + id + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    faceFeature.setFeature("");
                    faceFeature.setFeatureMask(null);
                    faceFeature.setUpdatedAt(new Timestamp(new Date().getTime()));
                    faceFeatureService.save(faceFeature);
                }
            }
        }
    }

    //@Scheduled(cron = "0/10 * * * * *")
    public void getCuboxFeature (){ //cubox 추출전용 서비스
        log.info("cubox API Running");
        int id = 8176;
        while ( true )
        {
            List<Face> faceList = faceService.findTop100ByIdGreaterThanOrderByIdAsc(id);

            if ( faceList.size() == 0)
            {
                System.exit(0);
                //break;
            }

            for(int i=0; i<faceList.size(); i++) {
                Face face = faceList.get(i);
                id = face.getId();
                byte[] arr = face.getFaceImg();
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
                        FaceFeature faceFeature = new FaceFeature();
                        faceFeature.setFaceId(face.getId());
                        faceFeature.setEmpCd(face.getEmpCd());
                        faceFeature.setFeature(feature);
                        faceFeature.setFaceFeatureTyp("FFT001");
                        faceFeature.setCreatedAt(new Timestamp(new Date().getTime()));
                        faceFeature.setUpdatedAt(new Timestamp(new Date().getTime()));
                        faceFeatureService.save(faceFeature);



                    } else {
                        // Validation Error
                        SaveError(face, "FFT001", "data validation Error. StatusCode : " + statusCode);
                    };


                } catch (Exception e){
                    SaveError(face, "FFT001", e.getMessage());
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
            }
        }
    }

    @Scheduled(cron = "0/10 * * * * *")
    public void archeraFilter (){ // 부적정이미지 피쳐 삭제
        int id = 1;
        while ( true )
        {
            List<FaceFeature> faceFeatureList = faceFeatureService.filterFeatureList(id);

            if ( faceFeatureList.size() == 0)
            {
                System.exit(0);
                //break;
            }

            for(int i=0; i<faceFeatureList.size(); i++) {

                FaceFeature faceFeature = faceFeatureList.get(i);
                id = faceFeature.getId();
                int faceId = faceFeature.getFaceId();
                String result = faceFeatureService.filterFeature(faceFeature, faceId);

                if ("ok".equals(result)) {
                    //성공시 facefeature 갱신
                    System.out.println("성공@@@@@@@@@@@@@@@@@@@@  " + id + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                } else if ("fail".equals(result)) {
                    //실패시 feature 삭제, 페이스상태 실패로 갱신
                    System.out.println("실패###################  " + id + " ############################");
//                    faceFeature.setFeature("");
//                    faceFeature.setFeatureMask(null);
//                    faceFeature.setUpdatedAt(new Timestamp(new Date().getTime()));
//                    faceFeatureService.save(faceFeature);

//                    Optional<Emp> oEmp = empService.findByEmpCd(faceFeature.getEmpCd());
//                    if ( oEmp.isPresent())
//                    {
//                        Emp emp  = oEmp.get();
//                        emp.setFaceId(faceFeature.getFaceId());
//                        emp.setUpdatedAt(new Timestamp(new Date().getTime()));
//                        empService.save(emp);
//                    }
                }
            }
        }
    }


}
