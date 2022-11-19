package aero.cubox.api.deptemp.repository;

import aero.cubox.api.domain.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Integer> {

    Optional<Card> findByCardNo(String cardNo);

}
