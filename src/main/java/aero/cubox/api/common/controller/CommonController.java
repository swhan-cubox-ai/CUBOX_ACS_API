package aero.cubox.api.common.controller;


import aero.cubox.api.common.Constants;
import aero.cubox.api.common.mapper.TerminalMapper;
import aero.cubox.api.domain.vo.ResultVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@RestController
@RequestMapping({ Constants.API.API_CMMN })
public class CommonController {

    @Autowired
    TerminalMapper terminalMapper;


    @Value("${cuboxacs.linx_host}")
    String linx_host;


    @GetMapping(value = {"/fireOff"})
    @ApiOperation(value="화재 해제", notes="화재 해제")
    public ResultVo fireOff(
            @ApiParam(value = "빌딩코드", required = true) @RequestParam String buildingCd
    ) {
        closeCuboxTerminal(buildingCd);
        closeLinxTerminal(buildingCd);
        return ResultVo.ok();
    }

    public void closeCuboxTerminal(String buildingCd){
        List<String> ipAddrList = terminalMapper.getCUBOXIpAddrList(buildingCd);
        String port = ":5000";
        String api = "/fire?fireOn=true";
        for(String ip : ipAddrList){
            String uri = ip + port + api;

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, null , String.class);
        }
    }



    public void closeLinxTerminal(String buildingCd){
        List<String> ipAddrList = terminalMapper.getLINXIpAddrList(buildingCd);

        String api = "/push/pushCommand";
        for(String ip : ipAddrList){
            String uri = linx_host + api;

            Map<String, Object> params = new HashMap<>();
            params.put("terminalIp", ip);
            params.put("commandId", "0012");
            params.put("sub", 2);

            RestTemplate restTemplate = new RestTemplate();


//            ResponseEntity<String> response = restTemplate.getForEntity(uri , String.class, params);
        }


    }


}
