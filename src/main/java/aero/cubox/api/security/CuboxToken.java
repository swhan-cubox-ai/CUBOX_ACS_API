package aero.cubox.api.security;

import aero.cubox.api.security.vo.TokenUserInfoVo;
import aero.cubox.api.user.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.io.Serializable;
import java.util.Collection;

@Slf4j
public class CuboxToken extends AbstractAuthenticationToken implements Serializable {

  private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

  private String principal;
  private String credentials;

  public CuboxToken(String principal, String credentials, boolean authenticated) {
    super(null);
    this.principal = principal;
    this.credentials = credentials;
    setAuthenticated(authenticated);
  }

  public CuboxToken(String principal, String credentials,
                          Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.principal = principal;
    this.credentials = credentials;
    super.setAuthenticated(true); // must use super, as we override
  }

  @Override
  public Object getCredentials() {
    return credentials;
  }

  @Override
  public Object getPrincipal() {
    return principal;
  }

  public String getToken() {
    return credentials;
  }

  public TokenUserInfoVo getUser() {
    return (TokenUserInfoVo) getDetails();
  }


}
