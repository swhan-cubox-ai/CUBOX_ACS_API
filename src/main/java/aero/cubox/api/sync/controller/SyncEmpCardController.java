package aero.cubox.api.sync.controller;


import aero.cubox.api.common.Constants;
import aero.cubox.api.deptemp.service.EmpService;
import aero.cubox.api.domain.vo.ResultVo;
import aero.cubox.api.sync.vo.EmpVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping({ Constants.API.API_ACS_PREFIX + Constants.API.API_SYNC })
public class SyncEmpCardController {

    @Autowired
    EmpService empService;

    @GetMapping(value = {Constants.API.API_EMP})
    @ApiOperation(value="사원정보", notes="사원정보")
    public ResultVo<EmpVo> syncEmp(
            @ApiParam(value = "특징점유형 FFT001:씨유박스CPU FFT003:알체라CPU", required = true) @RequestParam String featureTyp
            , @ApiParam(value = "최종조회사원일시", required = true) @RequestParam String lastSyncDt
            , @ApiParam(value = "최종조회사원코드", required = true) @RequestParam String lastSyncEmpCd
            , @ApiParam(value = "조회건수", required = false, defaultValue = "100") @RequestParam Integer pageSize
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("featureTyp", featureTyp);
        params.put("lastSyncDt", lastSyncDt);
        params.put("lastSyncEmpCd", lastSyncEmpCd);
        params.put("pageSize", pageSize);
        List<EmpVo> list = empService.getEmpList(params);
        return ResultVo.ok(list);
    }

}
