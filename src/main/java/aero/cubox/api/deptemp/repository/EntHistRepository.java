package aero.cubox.api.deptemp.repository;

import aero.cubox.api.domain.entity.EntHist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntHistRepository extends JpaRepository<EntHist, Integer> {

    //Optional<Card> findByCardNo(String cardNo);

}
