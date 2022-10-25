package aero.cubox.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@CrossOrigin(origins="*")
@Slf4j
@Controller
public class HealthController {

  @ResponseBody
  @GetMapping(value = {"/health"})
  public String hello() {
    return "ok";
  }

}
