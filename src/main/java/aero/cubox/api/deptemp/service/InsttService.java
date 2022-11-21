package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.mapper.InsttMapper;
import aero.cubox.api.deptemp.repository.InsttRepository;
import aero.cubox.api.domain.entity.Instt;
import aero.cubox.api.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class InsttService extends AbstractService<Instt, Integer> {

    @Autowired
    private InsttMapper mapper;

    @Autowired
    private InsttRepository repository;

    @Override
    protected JpaRepository<Instt, Integer> getRepository() {
        return repository;
    }


    public Optional<Instt> findByInsttCd(String insttCd) {
        return repository.findByInsttCd(insttCd);
    }

}
