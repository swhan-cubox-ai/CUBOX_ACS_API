package aero.cubox.api.cmmncd.controller;


import aero.cubox.api.auth.vo.LoginResponseVo;
import aero.cubox.api.common.Constants;
import aero.cubox.api.cmmncd.service.CmmnCdService;
import aero.cubox.api.domain.entity.CmmnCd;
import aero.cubox.api.domain.entity.User;
import aero.cubox.api.domain.vo.ResultVo;
import aero.cubox.api.messages.Messages;
import aero.cubox.api.security.vo.JWTokenVo;
import aero.cubox.api.user.vo.UserVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping({ Constants.API.API_ACS_PREFIX + Constants.API.API_CMMNCD })
public class CmmnCdController {

    @Autowired
    CmmnCdService cmmnCdService;


    @GetMapping(value = {""})
    @ApiOperation(value="공통코드조회", notes="공통코드조회")
    public ResultVo<LoginResponseVo> login(
            @ApiParam(value = "공통코드유형", required = true) @RequestParam String cdTyp
    ) {

        List<CmmnCd> list = cmmnCdService.findAllByCdTyp(cdTyp);

        return ResultVo.ok(list);
    }
}
