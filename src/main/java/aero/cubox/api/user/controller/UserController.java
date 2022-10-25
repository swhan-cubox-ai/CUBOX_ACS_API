package aero.cubox.api.user.controller;


import aero.cubox.api.common.Constants;
import aero.cubox.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping({ Constants.API.API_ACSADM_PREFIX })
public class UserController {

    @Autowired
    UserService userService;


}
