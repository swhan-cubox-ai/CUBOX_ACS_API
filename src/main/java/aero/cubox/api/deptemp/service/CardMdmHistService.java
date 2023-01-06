package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.repository.CardMdmHistRepository;
import aero.cubox.api.domain.entity.Card;
import aero.cubox.api.domain.entity.CardMdmHist;
import aero.cubox.api.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class CardMdmHistService extends AbstractService<CardMdmHist, Integer> {


    @Autowired
    private CardMdmHistRepository repository;

    @Override
    protected JpaRepository<CardMdmHist, Integer> getRepository() {
        return repository;
    }

    public void SaveCard(String tblNm, Map<String, Object> mdmItem)
    {

        CardMdmHist hist = CardMdmHist.builder()
            .tblNm(tblNm)
            .empCd(String.valueOf(mdmItem.get("emp_cd")))
            .cardNo((String) mdmItem.get("card_no"))
            .begDt((Timestamp)mdmItem.get("beg_dt"))
            .endDt((Timestamp)mdmItem.get("end_dt"))
            .cardClassTyp((String) mdmItem.get("card_class_typ"))
            .cardStateTyp((String) mdmItem.get("card_state_typ"))
            .cardSttusSe((String) mdmItem.get("card_sttus_se"))
            .mdmDt((Timestamp)mdmItem.get("mdm_dt"))
            .fetchAt((Timestamp)mdmItem.get("fetch_at"))
            .startAt((Timestamp)mdmItem.get("start_at"))
            .doneAt((Timestamp)mdmItem.get("done_at"))
            .createdAt(new Timestamp(new Date().getTime()))
            .updatedAt(new Timestamp(new Date().getTime()))
            .build();
        repository.save(hist);

    }

}
