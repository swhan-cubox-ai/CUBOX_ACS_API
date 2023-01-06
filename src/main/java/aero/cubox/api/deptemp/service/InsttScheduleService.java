package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.mapper.EmpMapper;
import aero.cubox.api.domain.entity.Dept;
import aero.cubox.api.domain.entity.Instt;
import aero.cubox.api.mdm.service.MdmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@EnableScheduling
@Profile("prod")
public class InsttScheduleService {

    @Autowired
    InsttService insttService;

    @Autowired
    DeptService deptService;

    @Autowired
    EmpMapper empMapper;

    @Autowired
    private MdmService mdmService;

    // 기관, 부서 동기화
    @Scheduled(cron = "0/10 * * * * *")
    public void syncInstt() throws Exception {

        log.info("syncInstt....");

        while (true) {

            List<Map<String, Object>> mdmInsttRcv = mdmService.getMdmInsttRcv();
            if (mdmInsttRcv.size() == 0) {
                break;
            }

            for (int i = 0; i < mdmInsttRcv.size(); i++) {


                Map<String, Object> insttInfo = mdmInsttRcv.get(i);
                String insttCd = (String) insttInfo.get("instt_cd");
                String insttNm = (String) insttInfo.get("instt_nm");
                String deptCd = (String) insttInfo.get("dept_cd");
                String deptNm = (String) insttInfo.get("dept_nm");
                String insttYn = (String) insttInfo.get("instt_yn");

                int updatedCnt = 0;
                if ("Y".equals(insttYn)) {
                    // 기관여부가 Y 인경우 기관테이블에 입력
                    //updatedCnt = updateInsttRcv(insttInfo);

                    Instt instt = null;
                    Optional<Instt> oInstt = insttService.findByInsttCd(insttCd);
                    if ( oInstt.isEmpty())
                    {
                        instt = Instt.builder()
                                .insttCd(insttCd)
                                .insttNm(insttNm)
                                .createdAt(new Timestamp(new Date().getTime()))
                                .updatedAt(new Timestamp(new Date().getTime()))
                                .build()
                        ;
                    }
                    else
                    {
                        instt = oInstt.get();
                        instt.setInsttNm(insttNm);
                        instt.setUpdatedAt(new Timestamp(new Date().getTime()));
                    }
                    insttService.save(instt);
                    empMapper.updateEmpInstt(instt);
                } else if ("N".equals(insttYn)) {

                    Dept dept = null;
                    Optional<Dept> oDept = deptService.findByDeptCd(deptCd);
                    if ( oDept.isEmpty())
                    {
                        dept = Dept.builder()
                                .deptCd(deptCd)
                                .deptNm(deptNm)
                                .insttCd(insttCd)
                                .insttNm(insttNm)
                                .createdAt(new Timestamp(new Date().getTime()))
                                .updatedAt(new Timestamp(new Date().getTime()))
                                .build()
                        ;
                    }
                    else
                    {
                        dept = oDept.get();
                        dept.setDeptCd(deptCd);
                        dept.setDeptNm(deptNm);
                        dept.setInsttCd(insttCd);
                        dept.setInsttNm(insttNm);
                        dept.setUpdatedAt(new Timestamp(new Date().getTime()));
                    }
                    deptService.save(dept);
                    empMapper.updateEmpDept(dept);

                    // 기관여부가 N 인경우 부서테이블에 입력
                    //updatedCnt = updateDeptRcv(insttInfo);
                }

                // TO-DO
                // SYNC_DEPT_ERR

                // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => Y) 업데이트  (PK = cntn_sn)
                mdmService.updateMdmInsttRcv(insttInfo);

            }
        }


    }




}
