package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.mapper.EmpMapper;
import aero.cubox.api.deptemp.repository.CardRepository;
import aero.cubox.api.deptemp.repository.EmpRepository;
import aero.cubox.api.deptemp.repository.FaceRepository;
import aero.cubox.api.domain.entity.*;
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
@Profile("prod")
public class EmpTxService {

    @Autowired
    EmpRepository empRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    FaceRepository faceRepository;

    @Autowired
    EmpMdmErrService empMdmErrService;

    @Autowired
    InsttService insttService;

    @Autowired
    EmpMapper empMapper;

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
        String insttCd = "";
        String insttNm = "";
        if(!StringUtils.isEmpty((String) mdmItem.get("instt_cd"))){
            Optional<Instt> oInstt = insttService.findByInsttCd((String) mdmItem.get("instt_cd"));
            if(oInstt.isEmpty()){
                Instt instt = oInstt.get();
                insttCd = instt.getInsttCd();
                insttNm = instt.getInsttNm();
            }
        }

        boolean isEmpty = false;
        if (oEmp.isEmpty()) {
            isEmpty = true;
            emp = Emp.builder()
                    .empCd(empCd)
                    .empNm((String) mdmItem.get("emp_nm"))
                    .deptCd((String) mdmItem.get("dept_cd"))
                    .deptNm((String) mdmItem.get("dept_nm"))
                    .insttCd(insttCd)
                    .insttNm(insttNm)
                    .belongNm((String)mdmItem.get("belong_nm"))
                    .cardClassTyp((String)mdmItem.get("card_class_typ"))
                    .cardStateTyp((String)mdmItem.get("card_state_typ"))
                    .expiredDt((Timestamp) mdmItem.get("expired_dt"))
                    .mdmDt((Timestamp) mdmItem.get("mdm_dt"))
                    .createdAt(new Timestamp(new Date().getTime()))
                    .updatedAt(new Timestamp(new Date().getTime()))
                    .build();
            empMapper.insertEmp(emp);
            // 성공분 중 가장 최근에 등록된 사진
            // TO-DO 속도 오래 걸림. 해결방안 강구 필요
//            Optional<Face> oFace = faceRepository.findFirstByEmpCdAndFaceStateTypOrderByIdDesc(empCd, "FST002");
//            if (oFace.isPresent()) {
//                emp.setFaceId(oFace.get().getId());
//            }


        } else {
            emp = oEmp.get();

            // 현재 저장된게 더 최신이면 Skip
            if (emp.getMdmDt().compareTo((Timestamp) mdmItem.get("mdm_dt")) > 0) {
                return;
            }

            emp.setEmpNm((String) mdmItem.get("emp_nm"));
            emp.setDeptCd((String) mdmItem.get("dept_cd"));
            emp.setDeptNm((String) mdmItem.get("dept_nm"));
            emp.setInsttCd(insttCd);
            emp.setInsttNm(insttNm);
            emp.setBelongNm((String) mdmItem.get("belong_nm"));
            emp.setCardClassTyp((String) mdmItem.get("card_class_typ"));
            emp.setCardStateTyp((String) mdmItem.get("card_state_typ"));
            emp.setExpiredDt((Timestamp) mdmItem.get("expired_dt"));
            emp.setMdmDt((Timestamp) mdmItem.get("mdm_dt"));
            emp.setUpdatedAt(new Timestamp(new Date().getTime()));

            empMapper.updateEmp(emp);
        }
//        emp = empRepository.save(emp);



//        if (isEmpty) {
//            // MDM보다 먼저 올라온 사진
//            List<Face> faceList = faceRepository.findAllByEmpCd(empCd);
//            for(Face face : faceList)
//            {
//                face.setEmpId(emp.getId());
//                faceRepository.save(face);
//            }
//        }

    }



    public void SaveCard(Map<String, Object> mdmItem)
    {
        String cardNo = (String) mdmItem.get("card_no");
        String cardClassTyp = (String) mdmItem.get("card_class_typ");
        if ( "04".equals(cardClassTyp))
        {
            cardNo = cardNo.replaceFirst("^0+(?!$)", "");
            if ( cardNo.length() < 8)
            {
                cardNo = StringUtils.leftPad(cardNo, 8, "0");
            }
        }

        Card card = null;
        Optional<Card> oCard = cardRepository.findByCardNo(cardNo);
        if ( oCard.isEmpty() )
        {

            card = Card.builder()
                    .cardNo(cardNo)
                    .empCd(String.valueOf(mdmItem.get("emp_cd")))
                    .begDt((Timestamp)mdmItem.get("beg_dt"))
                    .endDt((Timestamp)mdmItem.get("end_dt"))
                    .cardClassTyp((String) mdmItem.get("card_class_typ"))
                    .cardStateTyp((String) mdmItem.get("card_state_typ"))
                    .cardSttusSe((String) mdmItem.get("card_sttus_se"))
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
            card.setCardStateTyp((String) mdmItem.get("card_state_typ"));
            card.setCardSttusSe((String) mdmItem.get("card_sttus_se"));
            card.setMdmDt((Timestamp)mdmItem.get("mdm_dt"));
            card.setUpdatedAt(new Timestamp(new Date().getTime()));

        }
        cardRepository.save(card);

    }


}
