package aero.cubox.api.deptemp.mapper;

import aero.cubox.api.domain.entity.Dept;
import aero.cubox.api.domain.entity.Emp;
import aero.cubox.api.domain.entity.Instt;
import aero.cubox.api.sync.vo.EmpVo;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Component
public interface EmpMapper {

    public List<EmpVo> getEmpList(Map<String, Object> params);

    int insertEmp(Emp emp);

    int updateEmp(Emp emp);

    int updateEmpInstt(Instt instt);
    int updateEmpDept(Dept dept);
}
