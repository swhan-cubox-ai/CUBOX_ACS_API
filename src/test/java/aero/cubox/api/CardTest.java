package aero.cubox.api;

import aero.cubox.api.card.service.CardService;
import aero.cubox.api.emp.service.EmpService;
import aero.cubox.api.instt.service.InsttService;
import aero.cubox.api.mdm.service.MdmService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


@RunWith(SpringRunner.class)
@ActiveProfiles({"dev"})
@SpringBootTest
public class CardTest {

    String message = "";

    @Autowired
    private MdmService mdmService;

    @Autowired
    private CardService cardService;

    @Autowired
    private EmpService empService;

    @Test
    public void syncCard(){
        // 일반출입증
        int totalCount1 = mdmService.getMdmTcEmCgpnCount();
        // 분할처리용. 페이징아님.(1000건씩 분할처리중)
        int pageNo1 = (totalCount1/1000) +1;

        List<Map<String, Object>> mdmCgpnList = new ArrayList<>();
        for(var k=0; k<pageNo1; k++) {
            mdmCgpnList = mdmService.getMdmTcEmCgpn();

            for (int i = 0; i < mdmCgpnList.size(); i++) {
                Map<String, Object> cardItem = mdmCgpnList.get(i);
                int updatedCnt = 0;
                updatedCnt = cardService.updateCard(cardItem);

                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc N => Y) 업데이트  (PK = card_no)
                if (updatedCnt == 1) mdmService.updateMdmTcEmCgpn(cardItem);

                // EMP insert or update
                int emp_id = empService.updateEmp(cardItem);
                if(1 != emp_id){
                    cardItem.put("emp_id", emp_id);
                    int updateCardRow = cardService.updateCardEmpId(cardItem);
                }
            }
        }

        // 공무원증
        int totalCount2 = mdmService.getMdmTcEmPbsvntCount();
        int pageNo2= (totalCount2/1000) +1;

        List<Map<String, Object>> mdmPbsvntist = new ArrayList<>();
        for(var k=0; k<pageNo2; k++) {
            mdmPbsvntist = mdmService.getMdmTcEmPbsvnt();

            for (int i = 0; i < mdmPbsvntist.size(); i++) {
                Map<String, Object> cardItem = mdmPbsvntist.get(i);
                int updatedCnt = 0;
                updatedCnt = cardService.updateCard(cardItem);

                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => Y) 업데이트  (PK = card_no)
                if (updatedCnt == 1) mdmService.updateMdmTcEmPbsvnt(cardItem);

                // EMP insert or update
                int emp_id = empService.updateEmp(cardItem);
                if(1 != emp_id){
                    cardItem.put("emp_id", emp_id);
                    int updateCardRow = cardService.updateCardEmpId(cardItem);
                }
            }
        }

        // 방문자출입증
        int totalCount3 = mdmService.getMdmTcEmVisitCount();
        int pageNo3= (totalCount3/1000) +1;

        List<Map<String, Object>> mdmVisitList = new ArrayList<>();
        for(var k=0; k<pageNo3; k++) {
            mdmVisitList = mdmService.getMdmTcEmVisit();
            for (int i = 0; i < mdmVisitList.size(); i++) {
                Map<String, Object> cardItem = mdmVisitList.get(i);
                int updatedCnt = 0;
                updatedCnt = cardService.updateCard(cardItem);

                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => Y) 업데이트  (PK = card_no)
                if (updatedCnt == 1) mdmService.updateMdmTcEmVisit(cardItem);

                // EMP insert or update
                int emp_id = empService.updateEmp(cardItem);
                if(1 != emp_id){
                    cardItem.put("emp_id", emp_id);
                    int updateCardRow = cardService.updateCardEmpId(cardItem);
                }
            }
        }
    }

}
