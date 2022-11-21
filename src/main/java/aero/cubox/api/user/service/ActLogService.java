package aero.cubox.api.user.service;

import aero.cubox.api.domain.entity.ActLog;
import aero.cubox.api.service.AbstractService;
import aero.cubox.api.user.repository.ActLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class ActLogService extends AbstractService<ActLog, Integer> {

    @Autowired
    private ActLogRepository repository;

    @Autowired
    private ActLogTxService actLogTxService;

    @Override
    protected JpaRepository<ActLog, Integer> getRepository() {
        return repository;
    }

    public List<ActLog> findAll() {
        return repository.findAll();
    }

}
