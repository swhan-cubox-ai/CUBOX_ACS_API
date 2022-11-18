package aero.cubox.api.face.service;

import aero.cubox.api.emp.mapper.EmpMapper;
import aero.cubox.api.face.mapper.FaceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FaceService {

    @Autowired
    private FaceMapper mapper;

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
