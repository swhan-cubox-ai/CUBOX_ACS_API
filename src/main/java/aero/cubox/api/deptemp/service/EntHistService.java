package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.mapper.CardMapper;
import aero.cubox.api.deptemp.mapper.EntHistMapper;
import aero.cubox.api.deptemp.repository.CardRepository;
import aero.cubox.api.deptemp.repository.EntHistRepository;
import aero.cubox.api.domain.entity.Card;
import aero.cubox.api.domain.entity.EntHist;
import aero.cubox.api.domain.entity.FaceFeature;
import aero.cubox.api.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class EntHistService extends AbstractService<EntHist, Integer> {

    @Autowired
    private EntHistMapper mapper;

    @Autowired
    private EntHistRepository repository;

    @Override
    protected JpaRepository<EntHist, Integer> getRepository() {
        return repository;
    }

    public void saveEntHist(EntHist entHist)
    {
        repository.save(entHist);
    }

    //public Optional<Card> findByCardNo(String cardNo) { return repository.findByCardNo(cardNo); }

    //public List<Card> getCardList(Map<String, Object> params) { return mapper.getCardList(params);}

}
