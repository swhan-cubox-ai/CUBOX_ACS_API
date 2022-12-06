package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.mapper.FaceFeatureMapper;
import aero.cubox.api.deptemp.repository.FaceFeatureRepository;
import aero.cubox.api.deptemp.repository.FaceRepository;
import aero.cubox.api.domain.entity.Face;
import aero.cubox.api.domain.entity.FaceFeature;
import aero.cubox.api.domain.entity.FaceFeatureErr;
import aero.cubox.api.service.AbstractService;
import aero.cubox.api.util.CuboxTerminalUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FaceFeatureService extends AbstractService<FaceFeature, Integer> {

    @Autowired
    private FaceFeatureMapper mapper;
    @Autowired
    private FaceFeatureRepository repository;

    @Autowired
    private FaceRepository faceRepository;

    @Autowired
    private FaceService faceService;

    @Value("${cuboxacs.archera_api_host}")
    String archera_host;

    @Override
    protected JpaRepository<FaceFeature, Integer> getRepository() {
        return repository;
    }

    public List<FaceFeature> findAllByFaceFeatureAlchera()
    {
        String faceFeatureTyp = "FFT003";
        return repository.findTop10ByFaceFeatureTypAndFeatureMaskIsNullOrderByIdAsc(faceFeatureTyp);
    }

    public List<FaceFeature> findAllByFaceFeatureAlchera2()
    {
        String faceFeatureTyp = "FFT003";
        int faceId = 145000;
        return repository.findTop10ByFaceFeatureTypAndFeatureMaskIsNullAndFaceIdIsLessThanOrderByIdAsc(faceFeatureTyp, faceId);
    }

    public List<FaceFeature> findAllByFaceFeatureAlchera3()
    {
        String faceFeatureTyp = "FFT003";
        int faceId = 145000;
        return repository.findTop10ByFaceFeatureTypAndFeatureMaskIsNullAndFaceIdIsGreaterThanEqualOrderByIdAsc(faceFeatureTyp, faceId);
    }

    /**
     * feature mask 가 잘못된것만 조회
     * @return
     * 추후삭제
     */
    public List<FaceFeature> find2732Data(){
        return mapper.find2732Data();
    };

    public List<FaceFeature> getFeatureListForLandmark(int id){
        String faceFeatureTyp = "FFT003";

        return repository.findTop100ByFaceFeatureTypAndIdGreaterThanOrderByIdAsc(faceFeatureTyp, id);
    }

    public String getLandmarkScore(FaceFeature faceFeature){
        String landmarkResult = "fail";

        int faceId = faceFeature.getFaceId();
        Optional<Face> oFace = faceRepository.findById(faceId);

        if( oFace.isEmpty() )
        {
            delete(faceFeature.getId());
        } else {
            Face face = oFace.get();
            byte[] img = face.getFaceImg();
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(img);
            RestTemplate restTemplate = new RestTemplate();
            try{
                ResponseEntity<String> response = restTemplate.exchange(archera_host + "/faces", HttpMethod.POST, requestEntity , String.class);
                JSONObject jObj = new JSONObject(response.getBody());
                int statusCode = response.getStatusCodeValue();

                int count = jObj.getInt("count");
                JSONObject returnMsgO = jObj.getJSONObject("return_msg");
                String returnCd = (String) returnMsgO.get("return_code");
                String returnMsg = (String) returnMsgO.get("return_msg");

                if (statusCode == 200) {
                    JSONArray feturesInfo = jObj.getJSONArray("faces");
                    JSONObject featuresInfo = feturesInfo.getJSONObject(0);
                    JSONObject landmarkJSON = featuresInfo.getJSONObject("landmark");
                    // 성공 -- 랜드마크점수가 기준점이상이면 성공처리
                    float landmark = Float.parseFloat(String.valueOf(landmarkJSON.get("lm_confidence")));
                    System.out.println("lm_confidence = " +landmark);
                    if(landmark >0.88){
                        landmarkResult = "ok";
                    } else {
                        FaceFeatureErr faceFeatureErr = FaceFeatureErr.builder()
                                .faceId(face.getId())
                                .empCd(face.getEmpCd())
                                .faceFeatureTyp("FFT003") //알첼라 CPU
                                .error("landmark UnderScore : " + landmark)
                                .createdAt(new Timestamp(new Date().getTime()))
                                .updateAt(new Timestamp(new Date().getTime()))
                                .build();
                        faceService.saveFaceFeatrueErr(faceFeatureErr);
                    }
                } else {
                    face.setFaceStateTyp("FST003");
                    face.setUpdatedAt(new Timestamp((new Date().getTime())));
                    faceService.save(face);
                }
            } catch (Exception e){ // 기타 오류에대한 처리
                FaceFeatureErr faceFeatureErr = FaceFeatureErr.builder()
                        .faceId(face.getId())
                        .empCd(face.getEmpCd())
                        .faceFeatureTyp("FFT003") //알첼라 CPU
                        .error(e.getMessage())
                        .createdAt(new Timestamp(new Date().getTime()))
                        .updateAt(new Timestamp(new Date().getTime()))
                        .build();
                faceService.saveFaceFeatrueErr(faceFeatureErr);

                face.setFaceStateTyp("FST003");
                face.setUpdatedAt(new Timestamp((new Date().getTime())));
                faceService.save(face);

                return landmarkResult;
            }
        }


        return landmarkResult;
    }

    public List<FaceFeature> filterFeatureList(int id){
        String faceFeatureTyp = "FFT003";

        return repository.findTop100ByFaceFeatureTypAndFeatureMaskIsNotNullAndIdGreaterThanOrderByIdAsc(faceFeatureTyp, id);
    }

    public String filterFeature(FaceFeature faceFeature ,int faceId){
        String result = "fail";

        Optional<Face> oFace = faceRepository.findById(faceId);

        if( oFace.isEmpty() )
        {
            delete(faceFeature.getId());
        }
        else
        {
            Face face = oFace.get();
            byte[] img = face.getFaceImg();
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(img);
            RestTemplate restTemplate = new RestTemplate();
            try{
                ResponseEntity<String> response = restTemplate.exchange(archera_host + "/faces", HttpMethod.POST, requestEntity , String.class);
                JSONObject jObj = new JSONObject(response.getBody());
                int statusCode = response.getStatusCodeValue();

                int count = jObj.getInt("count");
                JSONObject returnMsgO = jObj.getJSONObject("return_msg");
                String returnCd = (String) returnMsgO.get("return_code");
                String returnMsg = (String) returnMsgO.get("return_msg");

                if (statusCode == 200) {
                    if(count == 0){ // 얼굴을 인식하지 못함.
                        return result;
                    } else {
                        JSONArray feturesInfo = jObj.getJSONArray("faces");
                        JSONObject featuresInfo = feturesInfo.getJSONObject(0);
                        JSONObject boxJSON = featuresInfo.getJSONObject("box");
                        JSONObject poseJSON = featuresInfo.getJSONObject("pose");
                        // 성공 -- 각 수치가 기준이상이면 성공처리
                        float width = Float.parseFloat(String.valueOf(boxJSON.get("width")));
                        float height = Float.parseFloat(String.valueOf(boxJSON.get("height")));
                        float yaw = Float.parseFloat(String.valueOf(poseJSON.get("yaw")));
                        float pitch = Float.parseFloat(String.valueOf(poseJSON.get("pitch")));
                        System.out.println("width = " +width + " height = " +height + " yaw = " +yaw + " pitch = " +pitch);
                        if(width >= 150 && height >= 150 && yaw <= 10 && yaw >= -10 && pitch <= 10 && pitch >= -10) {
                            return "ok";
                        } else {
//                        FaceFeatureErr faceFeatureErr = FaceFeatureErr.builder()
//                                .faceId(face.getId())
//                                .empCd(face.getEmpCd())
//                                .faceFeatureTyp("FFT003") //알첼라 CPU
//                                .error("below standard - [width] : " + width + " [height] : " + height +
//                                        " [yaw] : " + yaw + " [pitch] " + pitch)
//                                .createdAt(new Timestamp(new Date().getTime()))
//                                .updateAt(new Timestamp(new Date().getTime()))
//                                .build();
//                        faceService.saveFaceFeatrueErr(faceFeatureErr);

//                            face.setFaceStateTyp("FST003");
//                            face.setUpdatedAt(new Timestamp((new Date().getTime())));
//                            faceService.save(face);
                        }
                    }
                } else {
//                    FaceFeatureErr faceFeatureErr = FaceFeatureErr.builder()
//                                .faceId(face.getId())
//                                .empCd(face.getEmpCd())
//                                .faceFeatureTyp("FFT003") //알첼라 CPU
//                                .error("[ out standard ] Data validation Error. return_code:" + returnCd + " return_msg:" + returnMsg)
//                                .createdAt(new Timestamp(new Date().getTime()))
//                                .updateAt(new Timestamp(new Date().getTime()))
//                                .build();
//                        faceService.saveFaceFeatrueErr(faceFeatureErr);
//                    face.setFaceStateTyp("FST003");
//                    face.setUpdatedAt(new Timestamp((new Date().getTime())));
//                    faceService.save(face);
                }
            } catch (Exception e){ // 기타 오류에대한 처리
                return result;
            }
        }
        return result;
    }

}
