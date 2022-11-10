package aero.cubox.api.mdm.controller;


import aero.cubox.api.cmmncd.service.CmmnCdService;
import aero.cubox.api.common.Constants;
import aero.cubox.api.domain.entity.CmmnCd;
import aero.cubox.api.instt.service.InsttService;
import aero.cubox.api.mdm.service.MdmService;
import aero.cubox.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping({ Constants.API.API_ACS_PREFIX + Constants.API.API_MDM })
public class MdmController {

    @Autowired
    MdmService mdmService;

    @Autowired
    InsttService insttService;

    @Autowired
    CmmnCdService cmmnCdService;

    @RequestMapping(value="/test")
	public Map<String, Object> test(@RequestParam Integer count, @RequestParam String cdTyp) throws Exception {
        Map<String, Object> tmpMap = new HashMap<>();
        List<Map<String, Object>> result = mdmService.test(count);
        List<CmmnCd> tmp = cmmnCdService.findAllByCdTyp(cdTyp);
        tmpMap.put("emp", result);
        tmpMap.put("cmmncd", tmp);

		return tmpMap;
	}

    /**
     * 기관동기화
     */
    @RequestMapping(value="/syncInstt")
    public List<Map<String, Object>> syncInstt() throws Exception {
        // mdm에서 부서정보 가져오기
        List<Map<String, Object>> mdmInsttRcv = mdmService.getMdmInsttRcv();

        for(int i=0; i<mdmInsttRcv.size(); i++){
            Map<String, Object> insttInfo = mdmInsttRcv.get(i);
            String insttYn = String.valueOf(insttInfo.get("INSTT_YN"));
            int updatedCnt = 0;
            if("Y".equals(insttYn)){
                // 기관여부가 Y 인경우 기관테이블에 입력
                updatedCnt = insttService.updateInsttRcv(insttInfo);
            } else if("N".equals(insttYn)){
                // 기관여부가 N 인경우 부서테이블에 입력
                updatedCnt = insttService.updateDeptRcv(insttInfo);
            }
            // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => Y) 업데이트  (PK = cntn_sn)
            if(updatedCnt == 1) mdmService.updateMdmInsttRcv(insttInfo);
        }

        List<Map<String, Object>> result = null;
        return result;
    }

    /**
     * 사원동기화
     *
     2.2 3개의출입증 => T사원, T카드
     T사원. M.create_dt T.sync_dt
     없으면 인서트, 있을 경우 sync_dt보다 크면 업데이트, 아니면 skip
     */
    @RequestMapping(value="/syncEmp")
    public List<Map<String, Object>> syncEmp() throws Exception {
        // mdm에서 카드정보 가져오기
        //List<Map<String, Object>> mdmInsttRcv = mdmService.getMdmInsttRcv();

//        for(int i=0; i<mdmInsttRcv.size(); i++){
//            String insttYn = String.valueOf(mdmInsttRcv.get(i).get("INSTT_YN"));
//            if("Y".equals(insttYn)){
//                // 기관여부가 Y 인경우 기관테이블에 입력
//
//            } else if("N".equals(insttYn)){
//                // 기관여부가 N 인경우 부서테이블에 입력
//
//            }
//            // 동기화 진행한 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => Y) 업데이트  (PK = cntn_sn)
//
//        }


        List<Map<String, Object>> result = null;
        return result;
    }

}
