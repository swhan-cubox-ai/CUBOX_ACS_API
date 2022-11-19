package aero.cubox.api.deptemp.repository;

import aero.cubox.api.domain.entity.Emp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpRepository extends JpaRepository<Emp, Integer> {

    Optional<Emp> findByEmpCd(String empCd);

}
