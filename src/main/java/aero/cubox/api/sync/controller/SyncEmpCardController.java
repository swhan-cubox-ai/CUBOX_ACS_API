package aero.cubox.api.sync.controller;


import aero.cubox.api.common.Constants;
import aero.cubox.api.deptemp.service.*;
import aero.cubox.api.deptemp.vo.EntHistVO;
import aero.cubox.api.domain.entity.*;
import aero.cubox.api.domain.vo.ResultVo;
import aero.cubox.api.sync.vo.EmpVo;
import aero.cubox.api.util.CuboxTerminalUtil;
import com.github.pagehelper.util.StringUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping({ Constants.API.API_ACS_PREFIX + Constants.API.API_SYNC })
public class SyncEmpCardController {

    @Autowired
    EmpService empService;

    @Autowired
    CardService cardService;

    @Autowired
    FaceService faceService;

    @Autowired
    EntHistService entHistService;

    @Autowired
    EntHistBioService entHistBioService;


    @GetMapping(value = {Constants.API.API_EMP})
    @ApiOperation(value="사원정보", notes="사원정보")
    public ResultVo<List<EmpVo>> syncEmp(
            @ApiParam(value = "특징점유형 FFT001:씨유박스CPU FFT003:알체라CPU", required = true) @RequestParam String featureTyp
            , @ApiParam(value = "최종조회사원일시", required = true) @RequestParam String lastSyncDt
            , @ApiParam(value = "최종조회사원코드", required = true) @RequestParam String lastSyncEmpCd
            , @ApiParam(value = "조회건수", required = false, defaultValue = "100") @RequestParam(required = false, defaultValue = "100") Integer pageSize
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("featureTyp", featureTyp);
        params.put("lastSyncDt", lastSyncDt);
        params.put("lastSyncEmpCd", lastSyncEmpCd);
        params.put("pageSize", pageSize);
        List<EmpVo> list = empService.getEmpList(params);
        return ResultVo.ok(list);
    }

    @GetMapping(value = {Constants.API.API_CARD})
    @ApiOperation(value="카드정보", notes="카드정보")
    public ResultVo<List<Card>> syncCard(
            @ApiParam(value = "최종조회카드일시", required = true) @RequestParam String lastSyncDt
            , @ApiParam(value = "최종조회카드번호", required = true) @RequestParam String lastSyncCardNo
            , @ApiParam(value = "조회건수", required = false, defaultValue = "100") @RequestParam(required = false, defaultValue = "100") Integer pageSize
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("lastSyncDt", lastSyncDt);
        params.put("lastSyncCardNo", lastSyncCardNo);
        params.put("pageSize", pageSize);
        List<Card> list = cardService.getCardList(params);
        return ResultVo.ok(list);
    }


    @GetMapping(value = {Constants.API.API_FACE})
    @ApiOperation(value="사진", notes="사진")
    public ResultVo<String> syncFace(
           @ApiParam(value = "사진Id", required = true) @RequestParam String faceId
    ) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("id", faceId);
        Face face = faceService.getFace(params);
        if(face == null){
            return ResultVo.fail("get FaceInfo Fail");
        }
        byte[] imgBytes = face.getFaceImg();
        String encodeImg = CuboxTerminalUtil.byteArrEncode(imgBytes);
        return ResultVo.ok(encodeImg);
    }

    @PostMapping(value = {"/enthist"})
    @ApiOperation(value="출입기록등록", notes="출입기록등록")
    public ResultVo insertEntHist(@RequestBody EntHistVO entHist) throws Exception {
        if(StringUtil.isEmpty(entHist.getTerminalCd())) {
            return ResultVo.fail("terminalCd or evtDt is empty");
        }
        try {
            String termainalCd = entHist.getTerminalCd();
            if(!StringUtil.isEmpty(termainalCd)){
                EntHistVO terminalInfo = entHistService.getTerminalInfoById(termainalCd);
                if(terminalInfo != null){
                    String terminalTyp = terminalInfo.getTerminalTyp();
                    String buildingCd = terminalInfo.getBuildingCd();
                    String buildingNm = terminalInfo.getBuildingNm();
                    String doorCd = terminalInfo.getDoorCd();
                    String doorNm = terminalInfo.getDoorNm();

                    entHist.setTerminalTyp(terminalTyp);
                    entHist.setBuildingCd(buildingCd);
                    entHist.setBuildingNm(buildingNm);
                    entHist.setDoorCd(doorCd);
                    entHist.setDoorNm(doorNm);
                }
            }

            String empCd = entHist.getEmpCd();
            if(!StringUtil.isEmpty(empCd)){
                Optional<Emp> oEmp  = empService.findByEmpCd(empCd);
                Emp emp = oEmp.get();
                if( oEmp.isPresent() ){
                    String deptNm = emp.getDeptNm();
                    String deptCd = emp.getDeptCd();
                    String belongNm = emp.getBelongNm();
                    entHist.setDeptCd(deptCd);
                    entHist.setDeptNm(deptNm);
                    entHist.setBelongNm(belongNm);
                }
            }

            entHistService.saveEntHist(entHist);
        } catch (Exception ex){
            return ResultVo.fail("save fail", ex);
        }
        return ResultVo.ok();
    }

}
