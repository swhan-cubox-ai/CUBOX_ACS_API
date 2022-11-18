package aero.cubox.api.face.mapper;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface FaceMapper {

    List<Map<String, Object>> getFace001();

    int insertFace(Map<String, Object> faceInfo);

    int insertFaceFeature(Map<String, Object> faceInfo);

    int insertFaceFeatureErr(Map<String, Object> faceInfo);

    int updateFace(Map<String, Object> faceInfo);
}
