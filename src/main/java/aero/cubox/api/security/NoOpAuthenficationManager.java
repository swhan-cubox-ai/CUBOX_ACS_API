package aero.cubox.api.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

public class NoOpAuthenficationManager implements AuthenticationManager, AuthenticationProvider {

//    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Authentication authenticate(Authentication authentication) throws org.springframework.security.core.AuthenticationException {
        return authentication;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

}
