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

    // 기관
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
    // ---기관

    // 카드, 직원
    // mdm 일반출입증 데이터조회
    public List<Map<String, Object>> getMdmTcEmCgpn(){
        List<Map<String, Object>> result = mapper.getMdmTcEmCgpn();
        return result;
    }
    // mdm 데이터 갯수 조회 -- 동기화 분할처리용
    public int getMdmTcEmCgpnCount(){
        Map<String, Object> result = mapper.getMdmTcEmCgpnCount();
        int tot = Integer.parseInt( result.get("cnt").toString());
        return tot;
    };
    // 스마트세종청사컬럼 수정
    public void updateMdmTcEmCgpn(Map<String, Object> cardItem){
        mapper.updateMdmTcEmCgpn(cardItem);
    }

    // mdm 공무원증 데이터조회
    public List<Map<String, Object>> getMdmTcEmPbsvnt(){
        List<Map<String, Object>> result = mapper.getMdmTcEmPbsvnt();
        return result;
    }
    // mdm 데이터 갯수 조회 -- 동기화 분할처리용
    public int getMdmTcEmPbsvntCount(){
        Map<String, Object> result = mapper.getMdmTcEmPbsvntCount();
        int tot = Integer.parseInt( result.get("cnt").toString());
        return tot;
    };
    // 스마트세종청사컬럼 수정
    public void updateMdmTcEmPbsvnt(Map<String, Object> cardItem){
        mapper.updateMdmTcEmPbsvnt(cardItem);
    }



    // mdm 방문증 데이터조회
    public List<Map<String, Object>> getMdmTcEmVisit(){
        List<Map<String, Object>> result = mapper.getMdmTcEmVisit();
        return result;
    }
    // mdm 데이터 갯수 조회 -- 동기화 분할처리용
    public int getMdmTcEmVisitCount(){
        Map<String, Object> result = mapper.getMdmTcEmVisitCount();
        int tot = Integer.parseInt( result.get("cnt").toString());
        return tot;
    };
    // 스마트세종청사컬럼 수정
    public void updateMdmTcEmVisit(Map<String, Object> cardItem){
        mapper.updateMdmTcEmVisit(cardItem);
    }
    // --카드, 직원



}
