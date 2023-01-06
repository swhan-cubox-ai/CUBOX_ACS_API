package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.repository.*;
import aero.cubox.api.domain.entity.*;
import aero.cubox.api.mdm.service.MdmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@EnableScheduling
@Profile("prod")
public class EmpScheduleService {

    @Autowired
    EmpRepository empRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    FaceRepository faceRepository;

    @Autowired
    private MdmService mdmService;

    @Autowired
    private EmpMdmErrService empMdmErrService;

    @Autowired
    private CardMdmHistService cardMdmHistService;

    @Autowired
    private EmpTxService empTxService;

    // 일반출입증/공무원증은 10분 마다, 방문증은 10초마다 동기화하고 있는데
    @Scheduled(cron = "0/10 * * * * *") // 10초
    public void syncCard10sec() {

        log.info("syncCard 10 sec ....");

        String tblNm = "TC_EM_VISIT";


        // TC_EM_VISIT
        // 방문자출입증
        while (true) {

            Timestamp fetchTime = new Timestamp(new Date().getTime());

            List<Map<String, Object>> mdmVisitList = mdmService.getMdmTcEmVisit();
            if (mdmVisitList.size() == 0) {
                break;
            }

            for (int i = 0; i < mdmVisitList.size(); i++) {

                // TO-DO START_TIME
                Timestamp startTime = new Timestamp(new Date().getTime());

                Map<String, Object> mdmItem = mdmVisitList.get(i);

                try {
                    empTxService.SaveEmpCard(mdmItem);
                }
                catch(Exception ex)
                {
                    EmpMdmErr empMdmErr = EmpMdmErr.builder()
                            .tblNm(tblNm)
                            .empCd(String.valueOf(mdmItem.get("emp_cd")))
                            .cardNo((String) mdmItem.get("card_no"))
                            .mdmDt((Timestamp)mdmItem.get("mdm_dt"))
                            .error(ex.toString())
                            .createdAt(new Timestamp(new Date().getTime()))
                            .build();
                    empMdmErrService.save(empMdmErr);
                }

                // TO-DO DONE_TIME
                Timestamp doneTime = new Timestamp(new Date().getTime());

                mdmItem.put("fetch_at", fetchTime);
                mdmItem.put("start_at", startTime);
                mdmItem.put("done_at", doneTime);
                cardMdmHistService.SaveCard(tblNm, mdmItem);

                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => Y) 업데이트  (PK = card_no)
                mdmService.updateMdmTcEmVisit(mdmItem);
            }
        }
    }


    // 일반출입증/공무원증은 10분 마다, 방문증은 10초마다 동기화하고 있는데
    @Scheduled(cron = "0 0/10 * * * *") // 10분
    public void syncCard10Min() {

        log.info("syncCard 10 Min ....");

        String tblNm = "TC_EM_CGPN";

        // TC_EM_CGPN
        // 일반출입증
        while (true) {
            Timestamp fetchTime = new Timestamp(new Date().getTime());

            List<Map<String, Object>> mdmCgpnList = mdmService.getMdmTcEmCgpn();
            if (mdmCgpnList.size() == 0) {
                break;
            }

            for (int i = 0; i < mdmCgpnList.size(); i++) {

                Timestamp startTime = new Timestamp(new Date().getTime());

                Map<String, Object> mdmItem = mdmCgpnList.get(i);
                try {
                    empTxService.SaveEmpCard(mdmItem);
                }
                catch(Exception ex)
                {
                    EmpMdmErr empMdmErr = EmpMdmErr.builder()
                            .tblNm(tblNm)
                            .empCd(String.valueOf(mdmItem.get("emp_cd")))
                            .cardNo((String) mdmItem.get("card_no"))
                            .mdmDt((Timestamp)mdmItem.get("mdm_dt"))
                            .error(ex.toString())
                            .createdAt(new Timestamp(new Date().getTime()))
                            .build();
                    empMdmErrService.save(empMdmErr);
                }

                Timestamp doneTime = new Timestamp(new Date().getTime());

                mdmItem.put("fetch_at", fetchTime);
                mdmItem.put("start_at", startTime);
                mdmItem.put("done_at", doneTime);
                cardMdmHistService.SaveCard(tblNm, mdmItem);
                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc N => Y) 업데이트  (PK = card_no)
                mdmService.updateMdmTcEmCgpn(mdmItem);
            }
        }


        tblNm = "TC_EM_PBSVNT";


        // TC_EM_PBSVNT
        // 공무원증
        while (true) {
            Timestamp fetchTime = new Timestamp(new Date().getTime());

            List<Map<String, Object>> mdmPbsvntist = mdmService.getMdmTcEmPbsvnt();
            if (mdmPbsvntist.size() == 0) {
                break;
            }

            for (int i = 0; i < mdmPbsvntist.size(); i++) {

                Timestamp startTime = new Timestamp(new Date().getTime());

                Map<String, Object> mdmItem = mdmPbsvntist.get(i);

                try {
                    empTxService.SaveEmpCard(mdmItem);
                }
                catch(Exception ex)
                {
                    EmpMdmErr empMdmErr = EmpMdmErr.builder()
                            .tblNm(tblNm)
                            .empCd(String.valueOf(mdmItem.get("emp_cd")))
                            .cardNo((String) mdmItem.get("card_no"))
                            .mdmDt((Timestamp)mdmItem.get("mdm_dt"))
                            .error(ex.toString())
                            .createdAt(new Timestamp(new Date().getTime()))
                            .build();
                    empMdmErrService.save(empMdmErr);
                }

                Timestamp doneTime = new Timestamp(new Date().getTime());

                mdmItem.put("fetch_at", fetchTime);
                mdmItem.put("start_at", startTime);
                mdmItem.put("done_at", doneTime);
                cardMdmHistService.SaveCard(tblNm, mdmItem);
                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => Y) 업데이트  (PK = card_no)
                mdmService.updateMdmTcEmPbsvnt(mdmItem);

            }
        }
    }


}
