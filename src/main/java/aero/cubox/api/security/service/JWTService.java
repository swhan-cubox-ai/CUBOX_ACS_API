package aero.cubox.api.security.service;

import aero.cubox.api.security.CuboxToken;
import aero.cubox.api.security.vo.JWTokenVo;
import aero.cubox.api.security.vo.TokenUserInfoVo;
import aero.cubox.api.user.service.UserService;
import aero.cubox.api.user.vo.UserVo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class JWTService {

    @Value("${cuboxacs.jwtSecretKey}")
    private String jwtSecretKey;

    @Value("${cuboxacs.tokenTimeoutMinute}")
    private Integer tokenTimeoutMinute;

    @Value("${cuboxacs.refreshTokenTimeoutMinute}")
    private Integer refreshTokenTimeoutMinute;

    @Autowired
    UserService userService;

    private final String UID = "uid"; // userId
    private final String LID = "lid"; // loginId
    private final String UNM = "unm"; // userNm

    public JWTokenVo create(UserVo user) {
        Date now = new Date();
        Date expireDt = new Date(now.getTime() + Duration.ofMinutes(tokenTimeoutMinute).toMillis());
        Date refreshExpireDt = new Date(now.getTime() + Duration.ofMinutes(refreshTokenTimeoutMinute).toMillis());

        TokenUserInfoVo userInfo = TokenUserInfoVo.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .userNm(user.getUserNm())
                .build();

        String accessJws = createAccessJws(userInfo, expireDt);
        String refreshJws = createRefreshJws(userInfo, refreshExpireDt);

        JWTokenVo tokens = JWTokenVo.builder()
                .accessToken(accessJws)
                .refreshToken(refreshJws)
                .expireDate(expireDt)
                .refreshExpireDt(refreshExpireDt)
                .build()
                ;

        return tokens;
    }

    private String createAccessJws(TokenUserInfoVo userInfo, Date expireDt) {

        Date now = new Date();
//        Date expiredAt = new Date(now.getTime() + Duration.ofMinutes(tokenTimeoutMinute).toMillis());
//        Date refreshExpiredAt = new Date(now.getTime() + Duration.ofMinutes(refreshTokenTimeoutMinute).toMillis());

        byte[] bytes = TextCodec.BASE64.decode(jwtSecretKey);

        String jws = Jwts.builder()
                //.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setHeaderParam("typ", "JWT")
                .setIssuer("CUBOX")
                .setIssuedAt(now)
                .setExpiration(expireDt)
                .claim(UID, userInfo.getUserId())
                .claim(LID, userInfo.getLoginId())
                .claim(UNM, userInfo.getUserNm())
                .signWith(SignatureAlgorithm.HS256, bytes)
                .compact();

        return jws;

    }

    private String createRefreshJws(TokenUserInfoVo userInfo, Date refreshExpireDt) {

        Date now = new Date();

        byte[] bytes = TextCodec.BASE64.decode(jwtSecretKey);

        String jws = Jwts.builder()
                //.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setHeaderParam("typ", "JWT")
                .setIssuer("CUBOX")
                .setIssuedAt(now)
                .setExpiration(refreshExpireDt)
                .claim(UID, userInfo.getUserId())
                .signWith(SignatureAlgorithm.HS256, bytes)
                .compact();

        return jws;

    }


    public CuboxToken getAuthenticationToken(String accessToken) {

        byte[] bytes = TextCodec.BASE64.decode(jwtSecretKey);

        // ExpiredJwtException
        // UnsupportedJwtException
        // MalformedJwtException
        // SignatureException
        Map<String, Object> claimMap = null;
        try {
            claimMap = Jwts.parser()
                    .setSigningKey(bytes)
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (Exception ex ) {
            throw new AuthenticationServiceException("expired token");
        }

        if ( !claimMap.containsKey(UID) || !claimMap.containsKey(LID) || !claimMap.containsKey(UNM) ) {
            throw new AuthenticationServiceException("invalid payload");
        }

        TokenUserInfoVo userInfoVo = TokenUserInfoVo.builder()
                .userId(Integer.parseInt(String.valueOf(claimMap.get(UID))))
                .loginId(String.valueOf(claimMap.get(LID)))
                .userNm(String.valueOf(claimMap.get(UNM)))
                .build();

        Set<GrantedAuthority> authorities = new HashSet<>();
        for(String menuCd : userInfoVo.getMenuCds().split(","))
        {
            String role = "ROLE_" + menuCd;
            authorities.add(new SimpleGrantedAuthority(role));
        }

        CuboxToken cuboxToken = new CuboxToken(userInfoVo.getLoginId(), accessToken, authorities);
        cuboxToken.setDetails(userInfoVo);

        return cuboxToken;
    }

    public JWTokenVo refreshToken(String refreshToken) {
        // ExpiredJwtException
        // UnsupportedJwtException
        // MalformedJwtException
        // SignatureException
        Map<String, Object> claimMap = Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(refreshToken)
                .getBody();

        if ( !claimMap.containsKey("uid") ) {
            throw new MalformedJwtException("invalid payload");
        }

        String loginId = String.valueOf(claimMap.get("lid"));

        Optional<UserVo> oUser = userService.getUserDetailByLoginId(loginId);
        if (oUser.isEmpty() ) {
            throw new UnsupportedJwtException("user not found");
        }

        JWTokenVo jwTokenVo = this.create(oUser.get());

        return jwTokenVo;
    }

}
