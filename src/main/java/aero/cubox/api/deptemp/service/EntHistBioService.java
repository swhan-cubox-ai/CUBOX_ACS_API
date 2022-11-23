package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.repository.EntHistBioRepository;
import aero.cubox.api.domain.entity.EntHistBio;
import aero.cubox.api.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EntHistBioService extends AbstractService<EntHistBio, Integer> {


    @Autowired
    private EntHistBioRepository repository;

    @Override
    protected JpaRepository<EntHistBio, Integer> getRepository() {
        return repository;
    }

    //public Optional<Card> findByCardNo(String cardNo) { return repository.findByCardNo(cardNo); }

    //public List<Card> getCardList(Map<String, Object> params) { return mapper.getCardList(params);}

}
