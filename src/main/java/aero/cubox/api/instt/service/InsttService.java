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
        int insertCnt = mapper.insertInsttRcv(insttRcv);
        return insertCnt;
    }

    // mdm 부서정보를 동기화
    public int updateDeptRcv(Map<String, Object> insttRcv){
        int insertCnt = mapper.insertDeptRcv(insttRcv);
        return insertCnt;
    }



}
