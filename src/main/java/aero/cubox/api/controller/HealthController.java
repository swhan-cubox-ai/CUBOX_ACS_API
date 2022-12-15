package aero.cubox.api.controller;

import aero.cubox.api.mdm.service.MdmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins="*")
@Slf4j
@Controller
public class HealthController {

  @Autowired
  private MdmService mdmService;

  @ResponseBody
  @GetMapping(value = {"/health"})
  public String hello() {
    return "ok";
  }

  @ResponseBody
  @GetMapping(value = {"/health_cubrid"})
  public String helloCubrid() {
    String str = "fail";
    try{
      str = mdmService.getHealthCubrid();
    } catch (Exception e) {
      str = "cubrid connect fail!";
    }
    return str;
  }
}
