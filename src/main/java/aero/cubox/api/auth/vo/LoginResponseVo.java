package aero.cubox.api.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class LoginResponseVo {

    @ApiModelProperty(value="사용자id")
    private Integer userId;

    @ApiModelProperty(value="로그인아이디")
    private String loginId;

    @ApiModelProperty(value="사용자명")
    private String userNm;

    @ApiModelProperty(value="메뉴코드목록")
    private String menuCds;

    @ApiModelProperty(value="엑세스토큰")
    private String accessToken;

    @ApiModelProperty(value="토큰만료일시")
    private Date expireDt;

    @ApiModelProperty(value="리플레쉬토큰")
    private String refreshToken;

    @ApiModelProperty(value="리플레쉬토큰만료일시")
    private Date refreshExpireDt;

}
