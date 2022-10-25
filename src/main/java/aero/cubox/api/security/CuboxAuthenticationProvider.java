package aero.cubox.api.security;

import aero.cubox.api.domain.entity.User;
import aero.cubox.api.messages.MessageService;
import aero.cubox.api.messages.Messages;
import aero.cubox.api.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class CuboxAuthenticationProvider implements AuthenticationProvider {


    @Autowired
    private UserService adminService;

    @Autowired
    private MessageService messageService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String loginId = authentication.getName();
        String password = (String) authentication.getCredentials();

        Optional<User> oUser = adminService.findByLoginId(loginId);

        if ( oUser.isEmpty() ) {
            throw new BadCredentialsException(messageService.getMessage(Messages.AUTH_USER_NOT_FOUND));
        }

        User user = oUser.get();

        // 비밀번호 검증
        if (!adminService.matchesPassword(password, user.getLoginPwd())) {
            throw new BadCredentialsException(
                    messageService.getMessage(Messages.AUTH_PASSWORDS_DO_NOT_MATCH));
        }


        // 상태체크
        //if (  )
//        {
//            throw new BadCredentialsException(messageService.getMessage(Messages.AUTH_USERSTATUS_IS_NOT_APPROVED));
//        }


        // 권한
//        List<AdminRoleDto> adminRoles = adminService.getAdminRoles(admin.getId());
//
//        boolean hasRole = false;
//        for (String allowedRole : ALLOWED_ROLES) {
//            for (AdminRoleDto adminRole : adminRoles) {
//                if (adminRole.getAuthorityName().equals(allowedRole)) {
//                    hasRole = true;
//                    break;
//                }
//            }
//        }
//
//        if (!hasRole) {
//            throw new BadCredentialsException(messageService.getMessage(Messages.AUTH_NOT_ALLOWED));
//        }

//        List<GrantedAuthority> grantedAuthorities = adminRoles.stream()
//                .map(adminRole -> new SimpleGrantedAuthority(adminRole.getAuthorityName()))
//                .collect(Collectors.toList());

//        return new UsernamePasswordAuthenticationToken(admin, password, grantedAuthorities);
        return new UsernamePasswordAuthenticationToken(user, password, convertAuthorities(user));
    }

    private Collection<GrantedAuthority> convertAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<> ();

//        for(Role role : user.getRoles())
//        {
//            authorities.add(new SimpleGrantedAuthority(role.getNm()));
//        }

        return authorities;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

}
