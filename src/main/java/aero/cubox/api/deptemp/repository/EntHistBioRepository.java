package aero.cubox.api.deptemp.repository;

import aero.cubox.api.domain.entity.EntHistBio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntHistBioRepository extends JpaRepository<EntHistBio, Integer> {

    //Optional<Card> findByCardNo(String cardNo);

}
