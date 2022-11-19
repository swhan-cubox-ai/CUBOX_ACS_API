package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.mapper.CardMapper;
import aero.cubox.api.deptemp.repository.CardRepository;
import aero.cubox.api.domain.entity.Card;
import aero.cubox.api.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@EnableScheduling
public class CardService extends AbstractService<Card, Integer> {

    @Autowired
    private CardMapper mapper;

    @Autowired
    private CardRepository repository;

    @Override
    protected JpaRepository<Card, Integer> getRepository() {
        return repository;
    }


    public Optional<Card> findByCardNo(String cardNo) {
        return repository.findByCardNo(cardNo);
    }

}