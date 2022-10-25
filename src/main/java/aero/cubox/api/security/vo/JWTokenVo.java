package aero.cubox.api.security.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class JWTokenVo {
    private String accessToken;
    private String refreshToken;
    private Date expireDate;
    private Date refreshExpireDt;
}
