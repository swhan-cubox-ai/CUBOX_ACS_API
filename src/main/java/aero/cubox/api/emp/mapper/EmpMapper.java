package aero.cubox.api.emp.mapper;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface EmpMapper {

    Map<String, String> getEmpIsYn(Map<String, Object> mdmCardItem);

    int insertEmp(Map<String, Object> cardItem);

    int updateEmp(Map<String, Object> cardItem);

}
