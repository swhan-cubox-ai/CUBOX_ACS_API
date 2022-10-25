package aero.cubox.api.cmmncd.service;

import aero.cubox.api.domain.entity.CmmnCd;
import aero.cubox.api.service.AbstractService;
import aero.cubox.api.cmmncd.mapper.CmmnCdMapper;
import aero.cubox.api.cmmncd.repository.CmmnCdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CmmnCdService extends AbstractService<CmmnCd, Integer> {

    @Autowired
    private CmmnCdRepository repository;

    @Autowired
    private CmmnCdMapper mapper;


    @Override
    protected JpaRepository<CmmnCd, Integer> getRepository() {
        return repository;
    }

    public List<CmmnCd> findAllByCdTyp(String cdTyp)
    {
        return repository.findAllByCdTypOrderByOrderNo(cdTyp);
    }
}
