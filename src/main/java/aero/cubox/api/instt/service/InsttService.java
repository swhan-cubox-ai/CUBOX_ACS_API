package aero.cubox.api.instt.service;

import aero.cubox.api.instt.mapper.InsttMapper;
import aero.cubox.api.mdm.service.MdmService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@EnableScheduling
public class InsttService {

    @Autowired
    private InsttMapper mapper;

    @Autowired
    private MdmService mdmService;

    @Value("${cuboxacs.syncmdm}")
    String syncmdm;


    // 기관, 부서 동기화
    @Scheduled(cron = "0/10 * * * * *")
    public void syncInstt() throws Exception {
        if("N".equals(syncmdm)) return;
        log.info("syncInstt....");
        while (true) {
            List<Map<String, Object>> mdmInsttRcv = mdmService.getMdmInsttRcv();
            if (mdmInsttRcv.size() == 0) {
                break;
            }
            for (int i = 0; i < mdmInsttRcv.size(); i++) {
                Map<String, Object> insttInfo = mdmInsttRcv.get(i);
                String insttYn = String.valueOf(insttInfo.get("instt_yn"));
                int updatedCnt = 0;
                if ("Y".equals(insttYn)) {
                    // 기관여부가 Y 인경우 기관테이블에 입력
                    updatedCnt = updateInsttRcv(insttInfo);
                } else if ("N".equals(insttYn)) {
                    // 기관여부가 N 인경우 부서테이블에 입력
                    updatedCnt = updateDeptRcv(insttInfo);
                }
                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => Y) 업데이트  (PK = cntn_sn)
                if (updatedCnt == 1) mdmService.updateMdmInsttRcv(insttInfo);
            }
        }
    }


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
