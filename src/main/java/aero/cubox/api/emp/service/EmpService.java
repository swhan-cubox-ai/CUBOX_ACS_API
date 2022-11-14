package aero.cubox.api.emp.service;

import aero.cubox.api.emp.mapper.EmpMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmpService {

    @Autowired
    private EmpMapper mapper;

    public int updateEmp(Map<String, Object> cardItem){
        Map<String, String>  isYnMap = mapper.getEmpIsYn(cardItem);

        String isYn = isYnMap.get("isYn");
        if("N".equals(isYn)){
            // 2-1. 없으면 인서트
            mapper.insertEmp(cardItem);
        } else {
            // 2-2. 있으면 수정
            mapper.updateEmp(cardItem);
        }

        int id = Integer.parseInt(String.valueOf(cardItem.get("id")));
        return id;
    }

}
