package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.mapper.EmpMapper;
import aero.cubox.api.deptemp.repository.EmpRepository;
import aero.cubox.api.domain.entity.Emp;
import aero.cubox.api.service.AbstractService;
import aero.cubox.api.sync.vo.EmpVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class EmpService extends AbstractService<Emp, Integer> {

    @Autowired
    private EmpMapper mapper;

    @Autowired
    private EmpRepository repository;

    @Override
    protected JpaRepository<Emp, Integer> getRepository() {
        return repository;
    }

    public Optional<Emp> findByEmpCd(String empCd) {
        return repository.findByEmpCd(empCd);
    }

    public List<EmpVo> getEmpList(Map<String, Object> params) {
        return mapper.getEmpList(params);
    }


}
