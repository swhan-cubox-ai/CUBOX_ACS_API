package aero.cubox.api.sync.controller;


import aero.cubox.api.common.Constants;
import aero.cubox.api.deptemp.service.CardService;
import aero.cubox.api.deptemp.service.EmpService;
import aero.cubox.api.deptemp.service.EntHistService;
import aero.cubox.api.deptemp.service.FaceService;
import aero.cubox.api.domain.entity.Card;
import aero.cubox.api.domain.entity.EntHist;
import aero.cubox.api.domain.entity.Face;
import aero.cubox.api.domain.vo.ResultVo;
import aero.cubox.api.sync.vo.EmpVo;
import aero.cubox.api.util.CuboxTerminalUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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


    @GetMapping(value = {Constants.API.API_EMP})
    @ApiOperation(value="사원정보", notes="사원정보")
    public ResultVo<EmpVo> syncEmp(
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
    public ResultVo<Card> syncCard(
            @ApiParam(value = "최종조회카드일시", required = true) @RequestParam String lastSyncDt
            , @ApiParam(value = "최종조회사원코드", required = true) @RequestParam String lastSyncEmpCd
            , @ApiParam(value = "조회건수", required = false, defaultValue = "100") @RequestParam(required = false, defaultValue = "100") Integer pageSize
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("lastSyncDt", lastSyncDt);
        params.put("lastSyncEmpCd", lastSyncEmpCd);
        params.put("pageSize", pageSize);
        List<Card> list = cardService.getCardList(params);
        return ResultVo.ok(list);
    }


    @GetMapping(value = {Constants.API.API_FACE})
    @ApiOperation(value="사진", notes="사진")
    public ResultVo<String> syncFace(
           @ApiParam(value = "사원코드", required = true) @RequestParam String empCd
    ) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("empCd", empCd);
        Face face = faceService.getFace(params);
        byte[] imgBytes = face.getFaceImg();

        String encodeImg = CuboxTerminalUtil.byteArrEncode(imgBytes);

        return ResultVo.ok(encodeImg);
    }

    @PostMapping(value = {"/enthist"})
    @ApiOperation(value="출입기록등록", notes="출입기록등록")
    public ResultVo<String> insertEntHist(@RequestBody EntHist entHist) throws Exception {
        try {
            entHistService.saveEntHist(entHist);
        } catch (Exception ex){
            return ResultVo.fail("save fail", ex);
        }
        return ResultVo.ok("save complete!");
    }

}