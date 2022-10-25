package aero.cubox.api.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class SecurityUtils {

  private SecurityUtils() {
  }

  public static void logout(HttpServletRequest request, HttpServletResponse response) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      new SecurityContextLogoutHandler().logout(request, response, auth);
    }
  }

  public static Optional<CuboxToken> getCurrentToken() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication instanceof CuboxToken) {
      CuboxToken token = ((CuboxToken) authentication);
      return Optional.of(token);
    }
    return Optional.empty();
  }

  public static void GrantContext(Authentication authentication) {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    securityContext.setAuthentication(authentication);
  }

}
