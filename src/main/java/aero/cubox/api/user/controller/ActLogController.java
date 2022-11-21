package aero.cubox.api.user.controller;


import aero.cubox.api.auth.vo.LoginResponseVo;
import aero.cubox.api.common.Constants;
import aero.cubox.api.domain.entity.ActLog;
import aero.cubox.api.domain.entity.CmmnCd;
import aero.cubox.api.domain.vo.ResultVo;
import aero.cubox.api.user.service.ActLogService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping({ Constants.API.API_ACS_PREFIX + Constants.API.API_ACTLOG })
public class ActLogController {

    @Autowired
    ActLogService actLogService;

    @GetMapping(value = {""})
    @ApiOperation(value="활동로그조회", notes="활동로그조회")
    public ResultVo actlog() {

        actLogService.test();

        List<ActLog> list = actLogService.findAll();
        return ResultVo.ok(list);
    }

}
