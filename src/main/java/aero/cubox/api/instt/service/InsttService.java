package aero.cubox.api.instt.service;

import aero.cubox.api.instt.mapper.InsttMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InsttService {

    @Autowired
    private InsttMapper mapper;

    // mdm 기관정보를 동기화
    public int updateInsttRcv(Map<String, Object> insttRcv){
        // 1.기등록여부를 조회한 후
        Map<String, String> isYnMap = mapper.getInsttIsYn(insttRcv);
        String isYn = isYnMap.get("isYn");

        int updateCnt = 0;
        if("N".equals(isYn)){
            // 2-1. 없으면 인서트
             updateCnt = mapper.insertInsttRcv(insttRcv);
        } else {
            // 2-2. 있으면 수정
            updateCnt = mapper.updateInsttRcv(insttRcv);
        }

        return updateCnt;
    }

    // mdm 부서정보를 동기화
    public int updateDeptRcv(Map<String, Object> insttRcv){
        // 1.기등록여부를 조회한 후
        Map<String, String> isYnMap = mapper.getDeptIsYn(insttRcv);
        String isYn = isYnMap.get("isYn");

        int updateCnt = 0;
        if("N".equals(isYn)){
            // 2-1. 없으면 인서트
            updateCnt = mapper.insertDeptRcv(insttRcv);
        } else {
            // 2-2. 있으면 수정
            updateCnt = mapper.updateDeptRcv(insttRcv);
        }

        return updateCnt;
    }

}
