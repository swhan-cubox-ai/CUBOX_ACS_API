package aero.cubox.api;

import aero.cubox.api.instt.service.InsttService;
import aero.cubox.api.mdm.service.MdmService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@ActiveProfiles({"dev"})
@SpringBootTest
public class InsttTest {

    String message = "";

    @Autowired
    private MdmService mdmService;

    @Autowired
    private InsttService insttService;


    @Test
    public void syncInstt(){
        int totalCount = mdmService.getMdmInsttCount();
        int pageNo = totalCount/10;

        List<Map<String, Object>> mdmInsttRcv = new ArrayList<>();
        for(var k=0; k<pageNo; k++) {
            // mdm에서 부서정보 가져오기
            mdmInsttRcv = mdmService.getMdmInsttRcv();

            for (int i = 0; i < mdmInsttRcv.size(); i++) {
                Map<String, Object> insttInfo = mdmInsttRcv.get(i);
                String insttYn = String.valueOf(insttInfo.get("instt_yn"));
                int updatedCnt = 0;
                if ("Y".equals(insttYn)) {
                    // 기관여부가 Y 인경우 기관테이블에 입력
                    updatedCnt = insttService.updateInsttRcv(insttInfo);
                } else if ("N".equals(insttYn)) {
                    // 기관여부가 N 인경우 부서테이블에 입력
                    updatedCnt = insttService.updateDeptRcv(insttInfo);
                }
                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => Y) 업데이트  (PK = cntn_sn)
                if (updatedCnt == 1) mdmService.updateMdmInsttRcv(insttInfo);
            }
        }
    }

}
