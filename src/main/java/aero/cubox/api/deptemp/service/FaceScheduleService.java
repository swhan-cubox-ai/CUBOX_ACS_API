package aero.cubox.api.deptemp.service;

import aero.cubox.api.domain.entity.Emp;
import aero.cubox.api.domain.entity.Face;
import aero.cubox.api.domain.entity.FaceFeature;
import aero.cubox.api.domain.entity.FaceFeatureErr;
import aero.cubox.api.emp.mapper.EmpMapper;
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
import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
@EnableScheduling
public class FaceScheduleService {

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

    @Value("${cuboxacs.syncmdm}")
    String syncmdm;

    @Value("${cuboxacs.syncface}")
    String syncface;

    @Scheduled(cron = "0/10 * * * * *")
    public void insertFace() throws Exception {
        if("N".equals(syncface)) return;

        log.info("insertFace....");

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

            Face face = Face.builder()
                    .empCd(emp_cd)
                    .faceImg(face_img)
                    .faceStateTyp("FST001") // 대기상태
                    .createdAt(new Timestamp(new Date().getTime()))
                    .build();
            Optional<Emp> oEmp = empService.findByEmpCd(emp_cd);
            if ( oEmp.isPresent())
            {
                face.setEmpId(oEmp.get().getId());
            }
            face = faceService.save(face);

            if ( oEmp.isPresent())
            {
                Emp emp  = oEmp.get();
                emp.setFaceId(face.getId());
                emp.setUpdatedAt(new Timestamp(new Date().getTime()));
                empService.save(emp);
            }


            // 파일 동기화 처리 이후 파일 이동.
            String filePath = file.getPath();
            Path resourcePath = Paths.get(filePath);
            Path backupPath = Paths.get(move_directory+"\\" + fileName);

            // TO-DO
            // ERROR

            Files.move(resourcePath, backupPath, StandardCopyOption.REPLACE_EXISTING);

        }
    }

    @Scheduled(cron = "0/10 * * * * *")
    public void getFeatures() throws JSONException {

        if("N".equals(syncface)) return;

        log.info("getFeatures....");

        List<Face> faceList = faceService.findAllByFaceStateTyp("FST001"); // 대기상태

        for(int i=0; i<faceList.size(); i++) {

            Face face = faceList.get(i);

            int cnt = 0;
            String cuboxStatus = cuboxApi(face);
            log.info(i + " data complete. cubox Status : " + cuboxStatus);
            if("ok".equals(cuboxStatus)) cnt++;

            String archeraStatus = archeraApi(face);
            log.info(i + " data complete.  Archera Status" + archeraStatus);
            if("ok".equals(archeraStatus)) cnt++;

            // cubox, Alchera 둘다 성공시 FACE 정보 업데이트.
            if(cnt == 2){
                face.setFaceStateTyp("FST002"); // 성공
            } else { // 둘중하나라도 실패시 추출실패.
                face.setFaceStateTyp("FST003"); // 실패
            }
            faceService.save(face);

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

                FaceFeature faceFeature = FaceFeature.builder()
                        .faceId(face.getId())
                        .empCd(face.getEmpCd())
                        .faceStateTyp("FFT001") //씨유박스 CPU
                        .feature(feature)
                        .createdAt(new Timestamp(new Date().getTime()))
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
                .faceStateTyp(faceStateTyp) //씨유박스 CPU
                .error(errMsg)
                .createdAt(new Timestamp(new Date().getTime()))
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

                    FaceFeature faceFeature = FaceFeature.builder()
                            .faceId(face.getId())
                            .empCd(face.getEmpCd())
                            .faceStateTyp("FFT003") // 알체라 CPU
                            .feature(feature)
                            .createdAt(new Timestamp(new Date().getTime()))
                            .build();
                    faceService.saveFaceFeatrue(faceFeature);


                    featureStatus = "ok";
                } else if (statusCode == 400) {
                    SaveError(face, "FFT003", "Data validation Error. return_code:" + returnCd + " return_msg:" + returnMsg);
                } else if (statusCode == 406){
                    //API 사용 제한
                    SaveError(face, "FFT003", "API usage restrictions. return_code:" + returnCd + "return_msg:" + returnMsg );
                } else if (statusCode == 500){
                    //실패
                    SaveError(face, "FFT003", "Fail. return_code:" + returnCd + " return_msg:" + returnMsg );
                } else {
                    //실패
                    SaveError(face, "FFT003", "Fail. statusCode:" + statusCode + " returnCd:" + returnMsg + " return_msg:" + returnMsg );
                }

            } else {
                SaveError(face, "FFT003", "Features is null. return_code:" + returnCd + " return_msg:" + returnMsg );
            }
        } catch (Exception e){ // 기타 오류에대한 처리

            SaveError(face, "FFT003",  e.getMessage());

        }

        return featureStatus;
    }
//
//    // T_FACE insert
//    public int insertFace(Map<String, Object> faceInfo){
//        int insertFace = mapper.insertFace(faceInfo);
//
//        return insertFace;
//    }
//
//    // T_FACE id, 사진, emp_cd
//    public List<Map<String, Object>> getFace001(){
//        List<Map<String, Object>> faceInfoList = mapper.getFace001();
//
//        return faceInfoList;
//    }
//
//    // T_FACE 추출성공 feature 저장
//    public int insertFaceFeature(Map<String, Object> faceInfo){
//        int insertFace = mapper.insertFaceFeature(faceInfo);
//
//        return insertFace;
//    }
//
//    // T_FACE 추출 실패 error 저장
//    public int insertFaceFeatureErr(Map<String, Object> faceInfo){
//        int insertFace = mapper.insertFaceFeatureErr(faceInfo);
//
//        return insertFace;
//    }
//
//    // 추출완료후 T_FACE 업데이트
//    public int updateFace(Map<String, Object> faceInfo){
//        int updateCnt = mapper.updateFace(faceInfo);
//
//        return updateCnt;
//    }


}
