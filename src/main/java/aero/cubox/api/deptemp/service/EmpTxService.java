package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.repository.CardRepository;
import aero.cubox.api.deptemp.repository.EmpRepository;
import aero.cubox.api.deptemp.repository.FaceRepository;
import aero.cubox.api.domain.entity.Card;
import aero.cubox.api.domain.entity.Emp;
import aero.cubox.api.domain.entity.Face;
import aero.cubox.api.mdm.service.MdmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@EnableScheduling
@Profile("imsimdm")
public class EmpTxService {

    @Autowired
    EmpRepository empRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    FaceRepository faceRepository;


    @Transactional
    public void SaveEmpCard(Map<String, Object> mdmItem)
    {
        SaveEmp(mdmItem);
        SaveCard(mdmItem);
    }

    public void SaveEmp(Map<String, Object> mdmItem)
    {
        String empCd = String.valueOf(mdmItem.get("emp_cd"));

        Emp emp = null;
        Optional<Emp> oEmp = empRepository.findByEmpCd(empCd);
        if ( oEmp.isEmpty() )
        {
            emp = Emp.builder()
                    .empCd(empCd)
                    .empNm(String.valueOf(mdmItem.get("emp_nm")))
                    .deptCd(String.valueOf(mdmItem.get("dept_cd")))
                    .deptNm(String.valueOf(mdmItem.get("dept_nm")))
                    .insttCd(String.valueOf(mdmItem.get("instt_cd")))
                    .insttNm(String.valueOf(mdmItem.get("instt_nm")))
                    .belongNm(String.valueOf(mdmItem.get("belong_nm")))
                    .expiredDt((Timestamp)mdmItem.get("expired_dt"))
                    .mdmDt((Timestamp)mdmItem.get("mdm_dt"))
                    .createdAt(new Timestamp(new Date().getTime()))
                    .build();

            Optional<Face> oFace = faceRepository.findFirstByEmpCdOrderByIdDesc(empCd);
            if ( oFace.isPresent())
            {
                emp.setFaceId(oFace.get().getId());
            }
        }
        else
        {
            emp = oEmp.get();

            // 현재 저장된게 더 최신이면 Skip
            if ( emp.getMdmDt().compareTo((Timestamp)mdmItem.get("mdm_dt")) > 0)
            {
                return;
            }

            emp.setEmpNm(String.valueOf(mdmItem.get("emp_nm")));
            emp.setDeptCd(String.valueOf(mdmItem.get("dept_cd")));
            emp.setDeptNm(String.valueOf(mdmItem.get("dept_nm")));
            emp.setInsttCd(String.valueOf(mdmItem.get("instt_cd")));
            emp.setInsttNm(String.valueOf(mdmItem.get("instt_nm")));
            emp.setBelongNm(String.valueOf(mdmItem.get("belong_nm")));
            emp.setExpiredDt((Timestamp)mdmItem.get("expired_dt"));
            emp.setMdmDt((Timestamp)mdmItem.get("mdm_dt"));
            emp.setUpdatedAt(new Timestamp(new Date().getTime()));

        }

        empRepository.save(emp);

    }



    public void SaveCard(Map<String, Object> mdmItem)
    {
        String cardNo = String.valueOf(mdmItem.get("card_no"));

        Card card = null;
        Optional<Card> oCard = cardRepository.findByCardNo(cardNo);
        if ( oCard.isEmpty() )
        {
            card = Card.builder()
                    .cardNo(cardNo)
                    .empCd(String.valueOf(mdmItem.get("emp_cd")))
                    .begDt((Timestamp)mdmItem.get("beg_dt"))
                    .endDt((Timestamp)mdmItem.get("end_dt"))
                    .cardClassTyp(String.valueOf(mdmItem.get("card_class_typ")))
                    .cardStateTyp(String.valueOf(mdmItem.get("card_state_typ")))
                    .mdmDt((Timestamp)mdmItem.get("mdm_dt"))
                    .createdAt(new Timestamp(new Date().getTime()))
                    .build();
        }
        else
        {
            card = oCard.get();
            card.setEmpCd(String.valueOf(mdmItem.get("emp_cd")));
            card.setBegDt((Timestamp)mdmItem.get("beg_dt"));
            card.setEndDt((Timestamp)mdmItem.get("end_dt"));
            card.setCardStateTyp(String.valueOf(mdmItem.get("card_state_typ")));
            card.setMdmDt((Timestamp)mdmItem.get("mdm_dt"));
            card.setUpdatedAt(new Timestamp(new Date().getTime()));

        }
        cardRepository.save(card);

    }


}
