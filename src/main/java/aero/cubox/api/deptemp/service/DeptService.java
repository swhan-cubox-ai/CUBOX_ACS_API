package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.mapper.DeptMapper;
import aero.cubox.api.deptemp.repository.DeptRepository;
import aero.cubox.api.domain.entity.Dept;
import aero.cubox.api.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@EnableScheduling
public class DeptService extends AbstractService<Dept, Integer> {

    @Autowired
    private DeptMapper mapper;

    @Autowired
    private DeptRepository repository;

    @Override
    protected JpaRepository<Dept, Integer> getRepository() {
        return repository;
    }


    public Optional<Dept> findByDeptCd(String deptCd) {
        return repository.findByDeptCd(deptCd);
    }

}
