package aero.cubox.api.mdm.service;

import aero.cubox.api.mdm.mapper.MdmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MdmService  {

    @Autowired
    private MdmMapper mapper;

    public List<Map<String, Object>> test (Integer count){
        List<Map<String, Object>> result = mapper.test(count);
        return result;
    }

    public List<Map<String, Object>> getMdmInsttRcv(){
        List<Map<String, Object>> result = mapper.getMdmInsttRcv();
        return result;
    }

    public void updateMdmInsttRcv(Map<String, Object> insttRcv){
        mapper.updateMdmInsttRcv(insttRcv);

    }



}
