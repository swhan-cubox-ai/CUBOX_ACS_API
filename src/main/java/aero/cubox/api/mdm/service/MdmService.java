package aero.cubox.api.mdm.service;

import aero.cubox.api.mdm.mapper.MdmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MdmService  {

    @Autowired
    private MdmMapper mapper;

    // multi datasource test -- 추후삭제
    public List<Map<String, Object>> test (Integer count){
        List<Map<String, Object>> result = mapper.test(count);
        return result;
    }
    
    // mdm 데이터 갯수 조회 -- 동기화 분할처리용
    public int getMdmInsttCount(){
        Map<String, Object> result = mapper.getMdmInsttCount();
        int tot = Integer.parseInt( result.get("cnt").toString());
        return tot;
    };

    // mdm 데이터조회
    public List<Map<String, Object>> getMdmInsttRcv(){
        List<Map<String, Object>> result = mapper.getMdmInsttRcv();
        return result;
    }

    // 스마트세종청사컬럼 수정
    public void updateMdmInsttRcv(Map<String, Object> insttRcv){
        mapper.updateMdmInsttRcv(insttRcv);
    }



}
