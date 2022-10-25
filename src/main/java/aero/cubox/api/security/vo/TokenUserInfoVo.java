package aero.cubox.api.security.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/***
 * Access Token내 Payload에 저장될 정보
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
public class TokenUserInfoVo {

    private Integer userId;

    private String loginId;

    private String userNm;

    private String userTyp;

    private String menuCds;

}
