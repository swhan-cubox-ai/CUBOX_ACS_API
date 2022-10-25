package aero.cubox.api.user.service;

import aero.cubox.api.domain.entity.User;
import aero.cubox.api.service.AbstractService;
import aero.cubox.api.user.mapper.UserMapper;
import aero.cubox.api.user.repository.UserRepository;

import aero.cubox.api.user.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService extends AbstractService<User, Integer> {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected JpaRepository<User, Integer> getRepository() {
        return repository;
    }

    public boolean matchesPassword(String rawPassword, String encrypted) {
        return passwordEncoder.matches(rawPassword, encrypted);
    }

    public Optional<User> findByLoginId(String loginId) {
        return repository.findByLoginId(loginId);
    }

    public Optional<UserVo> getUserDetailByLoginId(String loginId) {
        UserVo user = mapper.getUserDetailByLoginId(loginId);
        return Optional.ofNullable(user);
    }

}
