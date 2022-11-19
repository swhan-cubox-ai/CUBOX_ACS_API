package aero.cubox.api.deptemp.service;

import aero.cubox.api.domain.entity.*;
import aero.cubox.api.mdm.service.MdmService;
import com.google.api.client.util.DateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@EnableScheduling
public class EmpScheduleService {

    @Autowired
    InsttService insttService;

    @Autowired
    EmpService empService;

    @Autowired
    CardService cardService;

    @Autowired
    DeptService deptService;

    @Autowired
    FaceService facdService;


    @Autowired
    private MdmService mdmService;
    @Value("${cuboxacs.syncmdm}")
    String syncmdm;

    public void SaveEmp(Map<String, Object> mdmEmp)
    {
        /*
		INSERT INTO T_EMP
			(emp_nm, emp_cd, dept_cd, dept_nm, instt_cd, instt_nm, belong_nm, face_id, expired_dt, mdm_dt, created_at, updated_at)
		VALUES
			(#{emp_nm}, #{emp_cd}, #{dept_cd}, #{dept_nm},  #{instt_cd},
		    #{instt_nm}, #{belong_nm}, #{face_id}, #{end_dt}, #{mdm_dt}, current_timestamp(6), current_timestamp(6))

        * */
        String empCd = String.valueOf(mdmEmp.get("emp_cd"));

        Emp emp = null;
        Optional<Emp> oEmp = empService.findByEmpCd(empCd);
        if ( oEmp.isEmpty() )
        {
            emp = Emp.builder()
                    .empCd(empCd)
                    .empNm(String.valueOf(mdmEmp.get("emp_cd")))
                    .deptCd(String.valueOf(mdmEmp.get("dept_cd")))
                    .deptNm(String.valueOf(mdmEmp.get("dept_nm")))
                    .insttCd(String.valueOf(mdmEmp.get("instt_cd")))
                    .insttNm(String.valueOf(mdmEmp.get("instt_nm")))
                    .belongNm(String.valueOf(mdmEmp.get("belong_nm")))
                    .expiredDt((Timestamp)mdmEmp.get("expired_dt"))
                    .mdmDt((Timestamp)mdmEmp.get("mdm_dt"))
                    .createdAt(new Timestamp(new Date().getTime()))
                    .build();


            Optional<Face> oFace = facdService.findFirstByEmpCd(empCd);
            if ( oFace.isPresent())
            {
                emp.setFaceId(oFace.get().getId());
            }
        }
        else
        {
            emp = oEmp.get();
            emp.setEmpNm(String.valueOf(mdmEmp.get("emp_cd")));
            emp.setDeptCd(String.valueOf(mdmEmp.get("dept_cd")));
            emp.setDeptNm(String.valueOf(mdmEmp.get("dept_nm")));
            emp.setInsttCd(String.valueOf(mdmEmp.get("instt_cd")));
            emp.setInsttNm(String.valueOf(mdmEmp.get("instt_nm")));
            emp.setBelongNm(String.valueOf(mdmEmp.get("belong_nm")));
            emp.setExpiredDt((Timestamp)mdmEmp.get("expired_dt"));
            emp.setUpdatedAt(new Timestamp(new Date().getTime()));

        }
        empService.save(emp);

    }



    public void SaveCard(Map<String, Object> mdmEmp)
    {
        /*
		INSERT INTO T_CARD
			(emp_id, emp_cd, card_no, beg_dt, end_dt, card_class_typ, card_state_typ, mdm_dt, created_at, updated_at)
		VALUES
			(1, #{emp_cd}, #{issu_no}, #{beg_dt}, #{end_dt},  #{card_class_typ}, #{card_state_typ}, #{mdm_dt}, current_timestamp(6), current_timestamp(6))

        * */
        String cardNo = String.valueOf(mdmEmp.get("card_no"));

        Card card = null;
        Optional<Card> oCard = cardService.findByCardNo(cardNo);
        if ( oCard.isEmpty() )
        {
            card = Card.builder()
                    .cardNo(cardNo)
                    .empCd(String.valueOf(mdmEmp.get("emp_cd")))
                    .begDt((Timestamp)mdmEmp.get("beg_dt"))
                    .endDt((Timestamp)mdmEmp.get("end_dt"))
                    .cardClassTyp(String.valueOf(mdmEmp.get("card_class_typ")))
                    .cardStateTyp(String.valueOf(mdmEmp.get("card_state_typ")))
                    .mdmDt((Timestamp)mdmEmp.get("mdm_dt"))
                    .createdAt(new Timestamp(new Date().getTime()))
                    .build();
        }
        else
        {
            card = oCard.get();
            card.setEmpCd(String.valueOf(mdmEmp.get("emp_cd")));
            card.setBegDt((Timestamp)mdmEmp.get("beg_dt"));
            card.setEndDt((Timestamp)mdmEmp.get("end_dt"));
            card.setCardStateTyp(String.valueOf(mdmEmp.get("card_state_typ")));
            card.setMdmDt((Timestamp)mdmEmp.get("mdm_dt"));
            card.setUpdatedAt(new Timestamp(new Date().getTime()));

        }
        cardService.save(card);

    }


    // 일반출입증/공무원증은 10분 마다, 방문증은 10초마다 동기화하고 있는데
    @Scheduled(cron = "0/10 * * * * *")
    public void syncCard10sec() {
        if("N".equals(syncmdm)) return;
        log.info("syncCard 10 sec ....");

        // 방문자출입증
        while (true) {
            List<Map<String, Object>> mdmVisitList = mdmService.getMdmTcEmVisit();
            if (mdmVisitList.size() == 0) {
                break;
            }

            for (int i = 0; i < mdmVisitList.size(); i++) {
                Map<String, Object> cardItem = mdmVisitList.get(i);
                this.SaveEmp(cardItem);

                this.SaveCard(cardItem);

                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => Y) 업데이트  (PK = card_no)
                mdmService.updateMdmTcEmVisit(cardItem);
            }
        }
    }


    // 일반출입증/공무원증은 10분 마다, 방문증은 10초마다 동기화하고 있는데
    @Scheduled(cron = "0/10 * * * * *")
    public void syncCard10Min() {
        if("N".equals(syncmdm)) return;
        log.info("syncCard 10 Min ....");

        // 일반출입증
        while (true) {

            List<Map<String, Object>> mdmCgpnList = mdmService.getMdmTcEmCgpn();
            if (mdmCgpnList.size() == 0) {
                break;
            }

            for (int i = 0; i < mdmCgpnList.size(); i++) {

                Map<String, Object> cardItem = mdmCgpnList.get(i);

                this.SaveEmp(cardItem);

                this.SaveCard(cardItem);

                // TO-DO
                // SYNC_EMPCARD_ERR

                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc N => Y) 업데이트  (PK = card_no)
                mdmService.updateMdmTcEmCgpn(cardItem);
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

                this.SaveEmp(cardItem);

                this.SaveCard(cardItem);

                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => Y) 업데이트  (PK = card_no)
                mdmService.updateMdmTcEmPbsvnt(cardItem);

            }
        }
    }


}
