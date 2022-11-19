package aero.cubox.api.deptemp.repository;

import aero.cubox.api.domain.entity.Dept;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeptRepository extends JpaRepository<Dept, Integer> {

    Optional<Dept> findByDeptCd(String deptCd);

}
