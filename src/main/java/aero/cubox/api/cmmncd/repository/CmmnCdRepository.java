package aero.cubox.api.cmmncd.repository;

import aero.cubox.api.domain.entity.CmmnCd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CmmnCdRepository extends JpaRepository<CmmnCd, Integer> {

    List<CmmnCd> findAllByCdTypOrderByOrderNo(String cdTyp);

}
