package aero.cubox.api.auth.controller;

import aero.cubox.api.auth.vo.LoginResponseVo;
import aero.cubox.api.common.Constants;
import aero.cubox.api.domain.entity.User;
import aero.cubox.api.domain.vo.ResultVo;
import aero.cubox.api.messages.MessageService;
import aero.cubox.api.messages.Messages;
import aero.cubox.api.security.service.JWTService;
import aero.cubox.api.security.vo.JWTokenVo;
import aero.cubox.api.user.service.UserService;
import aero.cubox.api.user.vo.UserVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins="*")
@Slf4j
@RestController
@RequestMapping({Constants.API.API_ACS_PREFIX + Constants.API.API_AUTH})
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private JWTService jwtService;

    @PostMapping(value = {"/login"})
    @ApiOperation(value="로그인", notes="로그인")
    public ResultVo<LoginResponseVo> login(
            @ApiParam(value = "로그인아이디") @RequestParam String loginId
            , @ApiParam(value = "비밀번호") @RequestParam String pwd
    ) {

        Optional<User> oUser = userService.findByLoginId(loginId);
        // 아이디 존재여부체크
        if ( oUser.isEmpty() )
        {
            return ResultVo.fail(messageService.getMessage(Messages.AUTH_USER_NOT_FOUND));
        }

        User user = oUser.get();


        if(!userService.matchesPassword(pwd, user.getLoginPwd()))
        {
            return ResultVo.fail(messageService.getMessage(Messages.AUTH_PASSWORDS_DO_NOT_MATCH));
        }


        Optional<UserVo> oUserVo = userService.getUserDetailByLoginId(loginId);
        UserVo userDetail = oUserVo.get();

        JWTokenVo jwTokenVo = jwtService.create(userDetail);
        LoginResponseVo loginResponseVo = LoginResponseVo.builder()
                .userId(userDetail.getId())
                .loginId(userDetail.getLoginId())
                .userNm(userDetail.getUserNm())
                .accessToken(jwTokenVo.getAccessToken())
                .refreshToken(jwTokenVo.getRefreshToken())
                .expireDt(jwTokenVo.getExpireDate())
                .refreshExpireDt(jwTokenVo.getRefreshExpireDt())
                .build();

        return ResultVo.ok(loginResponseVo);
    }

    @PostMapping(value = {"/refreshToken"})
    @ApiOperation(value="토큰 갱신", notes="토큰 갱신")
    public ResultVo<JWTokenVo> refreshToken(
            @ApiParam(value = "리플레쉬토킅")
            @RequestParam String refreshToken
    ) {

        JWTokenVo jwTokenVo = jwtService.refreshToken(refreshToken);
        return ResultVo.ok(jwTokenVo);
    }
}




