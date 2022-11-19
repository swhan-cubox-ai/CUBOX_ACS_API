package aero.cubox.api.deptemp.service;

import aero.cubox.api.card.service.CardService;
import aero.cubox.api.deptemp.mapper.InsttMapper;
import aero.cubox.api.deptemp.repository.InsttRepository;
import aero.cubox.api.domain.entity.Dept;
import aero.cubox.api.domain.entity.Instt;
import aero.cubox.api.mdm.service.MdmService;
import com.google.api.client.util.DateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@EnableScheduling
public class InsttScheduleService {

    @Autowired
    InsttService insttService;

    @Autowired
    DeptService deptService;

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
                String insttCode = String.valueOf(insttInfo.get("instt_code"));
                String insttNm = String.valueOf(insttInfo.get("instt_nm"));
                String deptCode = String.valueOf(insttInfo.get("dept_code"));
                String deptNm = String.valueOf(insttInfo.get("dept_nm"));
                String insttYn = String.valueOf(insttInfo.get("instt_yn"));

                int updatedCnt = 0;
                if ("Y".equals(insttYn)) {
                    // 기관여부가 Y 인경우 기관테이블에 입력
                    //updatedCnt = updateInsttRcv(insttInfo);

                    Instt instt = null;
                    Optional<Instt> oInstt = insttService.findByInsttCd(insttCode);
                    if ( oInstt.isPresent())
                    {
                        instt = oInstt.get();
                        instt.setInsttNm(insttNm);
                        instt.setUpdatedAt(new DateTime(new Timestamp(new Date().getTime())));
                    }
                    else
                    {
                        instt = Instt.builder()
                                .insttCd(insttCode)
                                .insttNm(insttNm)
                                .createdAt(new DateTime(new Timestamp(new Date().getTime())))
                                .build()
                                ;
                    }
                    insttService.save(instt);

                } else if ("N".equals(insttYn)) {

                    Dept dept = null;
                    Optional<Dept> oDept = deptService.findByDeptCd(deptCode);
                    if ( oDept.isPresent())
                    {
                        dept = oDept.get();
                        dept.setDeptCd(deptCode);
                        dept.setDeptNm(deptNm);
                        dept.setInsttCd(insttCode);
                        dept.setInsttNm(insttNm);
                        dept.setUpdatedAt(new DateTime(new Timestamp(new Date().getTime())));
                    }
                    else
                    {
                        dept = Dept.builder()
                                .deptCd(deptCode)
                                .deptNm(deptNm)
                                .insttCd(insttCode)
                                .insttNm(insttNm)
                                .createdAt(new DateTime(new Timestamp(new Date().getTime())))
                                .build()
                        ;
                    }
                    deptService.save(dept);


                    // 기관여부가 N 인경우 부서테이블에 입력
                    //updatedCnt = updateDeptRcv(insttInfo);
                }

                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => Y) 업데이트  (PK = cntn_sn)
                if (updatedCnt == 1) mdmService.updateMdmInsttRcv(insttInfo);

            }
        }


    }




}
