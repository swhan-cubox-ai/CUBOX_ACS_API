package aero.cubox.api.emp.service;

import aero.cubox.api.card.service.CardService;
import aero.cubox.api.emp.mapper.EmpMapper;
import aero.cubox.api.face.mapper.FaceMapper;
import aero.cubox.api.mdm.service.MdmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@EnableScheduling
public class EmpService {

    @Autowired
    private EmpMapper mapper;

    @Autowired
    private FaceMapper faceMapper;

    @Autowired
    private MdmService mdmService;

    @Autowired
    private CardService cardService;

    @Value("${cuboxacs.syncmdm}")
    String syncmdm;

    @Scheduled(cron = "0/10 * * * * *")
    public void syncCard() {
        if("N".equals(syncmdm)) return;
        log.info("syncCard....");
        // 일반출입증
        while (true) {
            List<Map<String, Object>> mdmCgpnList = mdmService.getMdmTcEmCgpn();
            if (mdmCgpnList.size() == 0) {
                break;
            }

            for (int i = 0; i < mdmCgpnList.size(); i++) {
                Map<String, Object> cardItem = mdmCgpnList.get(i);
                int updatedCnt = 0;
                updatedCnt = cardService.updateCard(cardItem);


                // EMP insert or update
                int emp_id = updateEmp(cardItem);
                if (1 != emp_id) {
                    Map<String, String> faceInfo= faceMapper.getFaceIdByEmpCd((String) cardItem.get("emp_cd"));
                    if (faceInfo != null && !faceInfo.isEmpty()) {
                        cardItem.put("face_id", faceInfo.get("face_id"));
                    }

                    cardItem.put("emp_id", emp_id);
                    int updateCardRow = cardService.updateCardEmpId(cardItem);
                }

                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc N => Y) 업데이트  (PK = card_no)
                if (updatedCnt == 1) mdmService.updateMdmTcEmCgpn(cardItem);
            }
        }


        // 공무원증

        while (true) {
            List<Map<String, Object>> mdmPbsvntist = mdmService.getMdmTcEmPbsvnt();
            if (mdmPbsvntist.size() == 0) {
                break;
            }

            for (int i = 0; i < mdmPbsvntist.size(); i++) {
                Map<String, Object> cardItem = mdmPbsvntist.get(i);
                int updatedCnt = 0;
                updatedCnt = cardService.updateCard(cardItem);

                // EMP insert or update
                int emp_id = updateEmp(cardItem);
                if(1 != emp_id){
                    Map<String, String> faceInfo= faceMapper.getFaceIdByEmpCd((String) cardItem.get("emp_cd"));
                    if (faceInfo != null && !faceInfo.isEmpty()) {
                        cardItem.put("face_id", faceInfo.get("face_id"));
                    }

                    cardItem.put("emp_id", emp_id);
                    int updateCardRow = cardService.updateCardEmpId(cardItem);
                }

                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => Y) 업데이트  (PK = card_no)
                if (updatedCnt == 1) mdmService.updateMdmTcEmPbsvnt(cardItem);

            }
        }

        // 방문자출입증
        while (true) {
            List<Map<String, Object>> mdmVisitList = mdmService.getMdmTcEmVisit();
            if (mdmVisitList.size() == 0) {
                break;
            }

            for (int i = 0; i < mdmVisitList.size(); i++) {
                Map<String, Object> cardItem = mdmVisitList.get(i);
                int updatedCnt = 0;
                updatedCnt = cardService.updateCard(cardItem);

                // EMP insert or update
                int emp_id = updateEmp(cardItem);
                if(1 != emp_id){
                    Map<String, String> faceInfo= faceMapper.getFaceIdByEmpCd((String) cardItem.get("emp_cd"));
                    if (faceInfo != null && !faceInfo.isEmpty()) {
                        cardItem.put("face_id", faceInfo.get("face_id"));
                    }

                    cardItem.put("emp_id", emp_id);
                    int updateCardRow = cardService.updateCardEmpId(cardItem);
                }

                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => Y) 업데이트  (PK = card_no)
                if (updatedCnt == 1) mdmService.updateMdmTcEmVisit(cardItem);
            }
        }
    }

    public int updateEmp(Map<String, Object> cardItem){
        Map<String, String>  isYnMap = mapper.getEmpIsYn(cardItem);

        String isYn = isYnMap.get("isYn");
        if("N".equals(isYn)){
            // 2-1. 없으면 인서트
            mapper.insertEmp(cardItem);
        } else {
            // 2-2. 있으면 수정
            mapper.updateEmp(cardItem);
        }

        int id = Integer.parseInt(String.valueOf(cardItem.get("id")));
        return id;
    }

}
