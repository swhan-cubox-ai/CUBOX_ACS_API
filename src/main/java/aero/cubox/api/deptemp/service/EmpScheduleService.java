package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.repository.CardRepository;
import aero.cubox.api.deptemp.repository.EmpMdmErrRepository;
import aero.cubox.api.deptemp.repository.EmpRepository;
import aero.cubox.api.deptemp.repository.FaceRepository;
import aero.cubox.api.domain.entity.*;
import aero.cubox.api.mdm.service.MdmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@EnableScheduling
@Profile("imsimdm")
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
    private EmpTxService empTxService;

    // 일반출입증/공무원증은 10분 마다, 방문증은 10초마다 동기화하고 있는데
    @Scheduled(cron = "0/10 * * * * *") // 10초
    public void syncCard10sec() {

        log.info("syncCard 10 sec ....");

        // TC_EM_VISIT
        // 방문자출입증
        while (true) {
            List<Map<String, Object>> mdmVisitList = mdmService.getMdmTcEmVisit();
            if (mdmVisitList.size() == 0) {
                break;
            }

            for (int i = 0; i < mdmVisitList.size(); i++) {
                Map<String, Object> mdmItem = mdmVisitList.get(i);

//                this.SaveEmp(mdmItem);
//                this.SaveCard(mdmItem);
//                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => Y) 업데이트  (PK = card_no)

                try {
                    empTxService.SaveEmpCard(mdmItem);
                }
                catch(Exception ex)
                {
                    EmpMdmErr empMdmErr = EmpMdmErr.builder()
                            .tblNm("TC_EM_VISIT")
                            .cgpnHrSn(String.valueOf(mdmItem.get("cgpn_hr_sn")))
                            .hrNo(String.valueOf(mdmItem.get("emp_cd")))
                            .error(ex.toString())
                            .createdAt(new Timestamp(new Date().getTime()))
                            .build();
                    empMdmErrService.save(empMdmErr);
                }

                mdmService.updateMdmTcEmVisit(mdmItem);
            }
        }
    }


    // 일반출입증/공무원증은 10분 마다, 방문증은 10초마다 동기화하고 있는데
    @Scheduled(cron = "* 0/10 * * * *") // 10분
    public void syncCard10Min() {

        log.info("syncCard 10 Min ....");

        // TC_EM_CGPN
        // 일반출입증
        while (true) {

            List<Map<String, Object>> mdmCgpnList = mdmService.getMdmTcEmCgpn();
            if (mdmCgpnList.size() == 0) {
                break;
            }

            for (int i = 0; i < mdmCgpnList.size(); i++) {

                Map<String, Object> mdmItem = mdmCgpnList.get(i);
//                this.SaveEmp(mdmItem);
//                this.SaveCard(mdmItem);
                try {
                    empTxService.SaveEmpCard(mdmItem);
                }
                catch(Exception ex)
                {
                    EmpMdmErr empMdmErr = EmpMdmErr.builder()
                            .tblNm("TC_EM_CGPN")
                            .cgpnHrSn(String.valueOf(mdmItem.get("cgpn_hr_sn")))
                            .hrNo(String.valueOf(mdmItem.get("emp_cd")))
                            .error(ex.toString())
                            .createdAt(new Timestamp(new Date().getTime()))
                            .build();
                    empMdmErrService.save(empMdmErr);
                }

                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc N => Y) 업데이트  (PK = card_no)
                mdmService.updateMdmTcEmCgpn(mdmItem);
            }
        }


        // TC_EM_PBSVNT
        // 공무원증
        while (true) {
            List<Map<String, Object>> mdmPbsvntist = mdmService.getMdmTcEmPbsvnt();
            if (mdmPbsvntist.size() == 0) {
                break;
            }

            for (int i = 0; i < mdmPbsvntist.size(); i++) {
                Map<String, Object> mdmItem = mdmPbsvntist.get(i);

//                this.SaveEmp(mdmItem);
//                this.SaveCard(mdmItem);
                try {
                    empTxService.SaveEmpCard(mdmItem);
                }
                catch(Exception ex)
                {
                    EmpMdmErr empMdmErr = EmpMdmErr.builder()
                            .tblNm("TC_EM_PBSVNT")
                            .cgpnHrSn(String.valueOf(mdmItem.get("cgpn_hr_sn")))
                            .hrNo(String.valueOf(mdmItem.get("emp_cd")))
                            .error(ex.toString())
                            .createdAt(new Timestamp(new Date().getTime()))
                            .build();
                    empMdmErrService.save(empMdmErr);
                }

                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => Y) 업데이트  (PK = card_no)
                mdmService.updateMdmTcEmPbsvnt(mdmItem);

            }
        }
    }


}
