package aero.cubox.api.user.repository;

import aero.cubox.api.domain.entity.ActLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActLogRepository extends JpaRepository<ActLog, Integer> {

    List<ActLog> findAll();

}
