package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.repository.CardRepository;
import aero.cubox.api.deptemp.repository.EmpRepository;
import aero.cubox.api.deptemp.repository.FaceRepository;
import aero.cubox.api.domain.entity.Card;
import aero.cubox.api.domain.entity.Emp;
import aero.cubox.api.domain.entity.EmpMdmErr;
import aero.cubox.api.domain.entity.Face;
import aero.cubox.api.mdm.service.MdmService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
//@Profile("local")
@Profile("imsimdm")
public class EmpTxService {

    @Autowired
    EmpRepository empRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    FaceRepository faceRepository;

    @Autowired
    EmpMdmErrService empMdmErrService;

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

        boolean isEmpty = false;
        if (oEmp.isEmpty()) {
            isEmpty = true;
            emp = Emp.builder()
                    .empCd(empCd)
                    .empNm(String.valueOf(mdmItem.get("emp_nm")))
                    .deptCd(String.valueOf(mdmItem.get("dept_cd")))
                    .deptNm(String.valueOf(mdmItem.get("dept_nm")))
                    .insttCd(String.valueOf(mdmItem.get("instt_cd")))
                    .insttNm(String.valueOf(mdmItem.get("instt_nm")))
                    .belongNm(String.valueOf(mdmItem.get("belong_nm")))
                    .cardClassTyp(String.valueOf(mdmItem.get("card_class_typ")))
                    .cardStateTyp(String.valueOf(mdmItem.get("card_state_typ")))
                    .expiredDt((Timestamp) mdmItem.get("expired_dt"))
                    .mdmDt((Timestamp) mdmItem.get("mdm_dt"))
                    .createdAt(new Timestamp(new Date().getTime()))
                    .updatedAt(new Timestamp(new Date().getTime()))
                    .build();

            // 성공분 중 가장 최근에 등록된 사진
            Optional<Face> oFace = faceRepository.findFirstByEmpCdAndFaceStateTypOrderByIdDesc(empCd, "FST002");
            if (oFace.isPresent()) {
                emp.setFaceId(oFace.get().getId());
            }


        } else {
            emp = oEmp.get();

            // 현재 저장된게 더 최신이면 Skip
            if (emp.getMdmDt().compareTo((Timestamp) mdmItem.get("mdm_dt")) > 0) {
                return;
            }

            emp.setEmpNm(String.valueOf(mdmItem.get("emp_nm")));
            emp.setDeptCd(String.valueOf(mdmItem.get("dept_cd")));
            emp.setDeptNm(String.valueOf(mdmItem.get("dept_nm")));
            emp.setInsttCd(String.valueOf(mdmItem.get("instt_cd")));
            emp.setInsttNm(String.valueOf(mdmItem.get("instt_nm")));
            emp.setBelongNm(String.valueOf(mdmItem.get("belong_nm")));
            emp.setCardClassTyp(String.valueOf(mdmItem.get("card_class_typ")));
            emp.setCardStateTyp(String.valueOf(mdmItem.get("card_state_typ")));
            emp.setExpiredDt((Timestamp) mdmItem.get("expired_dt"));
            emp.setMdmDt((Timestamp) mdmItem.get("mdm_dt"));
            emp.setUpdatedAt(new Timestamp(new Date().getTime()));

        }
        emp = empRepository.save(emp);


        if (isEmpty) {
            // MDM보다 먼저 올라온 사진
            List<Face> faceList = faceRepository.findAllByEmpCd(empCd);
            for(Face face : faceList)
            {
                face.setEmpId(emp.getId());
                faceRepository.save(face);
            }
        }

    }



    public void SaveCard(Map<String, Object> mdmItem)
    {
        String cardNo = String.valueOf(mdmItem.get("card_no"));

        Card card = null;
        Optional<Card> oCard = cardRepository.findByCardNo(cardNo);
        if ( oCard.isEmpty() )
        {
            String cardNoEx = cardNo.replaceFirst("^0+(?!$)", "");
            if ( cardNoEx.length() < 8)
            {
                cardNoEx = StringUtils.leftPad(cardNoEx, 8, "0'");
            }

            card = Card.builder()
                    .cardNo(cardNo)
                    .cardNoEx(cardNoEx)
                    .empCd(String.valueOf(mdmItem.get("emp_cd")))
                    .begDt((Timestamp)mdmItem.get("beg_dt"))
                    .endDt((Timestamp)mdmItem.get("end_dt"))
                    .cardClassTyp(String.valueOf(mdmItem.get("card_class_typ")))
                    .cardStateTyp(String.valueOf(mdmItem.get("card_state_typ")))
                    .cardSttusSe(String.valueOf(mdmItem.get("card_sttus_se")))
                    .mdmDt((Timestamp)mdmItem.get("mdm_dt"))
                    .createdAt(new Timestamp(new Date().getTime()))
                    .updatedAt(new Timestamp(new Date().getTime()))
                    .build();
        }
        else
        {
            card = oCard.get();
            card.setEmpCd(String.valueOf(mdmItem.get("emp_cd")));
            card.setBegDt((Timestamp)mdmItem.get("beg_dt"));
            card.setEndDt((Timestamp)mdmItem.get("end_dt"));
            card.setCardStateTyp(String.valueOf(mdmItem.get("card_state_typ")));
            card.setCardSttusSe(String.valueOf(mdmItem.get("card_sttus_se")));
            card.setMdmDt((Timestamp)mdmItem.get("mdm_dt"));
            card.setUpdatedAt(new Timestamp(new Date().getTime()));

        }
        cardRepository.save(card);

    }


}
