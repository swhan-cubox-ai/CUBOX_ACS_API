package aero.cubox.api.deptemp.repository;

import aero.cubox.api.domain.entity.EmpMdmErr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpMdmErrRepository extends JpaRepository<EmpMdmErr, Integer> {


}
