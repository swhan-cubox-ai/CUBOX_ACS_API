package aero.cubox.api.deptemp.repository;

import aero.cubox.api.domain.entity.Instt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InsttRepository extends JpaRepository<Instt, Integer> {

    Optional<Instt> findByInsttCd(String insttCd);

}
