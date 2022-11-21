package aero.cubox.api.deptemp.service;


import aero.cubox.api.deptemp.repository.EmpMdmErrRepository;
import aero.cubox.api.domain.entity.EmpMdmErr;
import aero.cubox.api.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class EmpMdmErrService extends AbstractService<EmpMdmErr, Integer> {

    @Autowired
    private EmpMdmErrRepository repository;

    @Override
    protected JpaRepository<EmpMdmErr, Integer> getRepository() {
        return repository;
    }

}
