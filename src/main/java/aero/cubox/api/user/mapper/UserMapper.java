package aero.cubox.api.user.mapper;

import aero.cubox.api.user.vo.UserVo;
import org.springframework.stereotype.Component;


@Component
public interface UserMapper {

    UserVo getUserDetailByLoginId(String loginid);


}
