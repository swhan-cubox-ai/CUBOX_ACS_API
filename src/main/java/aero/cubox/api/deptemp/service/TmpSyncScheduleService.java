package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.mapper.TmpEmMapper;
import aero.cubox.api.deptemp.repository.CardRepository;
import aero.cubox.api.deptemp.repository.TmpEmCgpnRepository;
import aero.cubox.api.deptemp.repository.TmpEmPbsvntRepository;
import aero.cubox.api.deptemp.repository.TmpEmVisitRepository;
import aero.cubox.api.domain.entity.Card;
import aero.cubox.api.domain.entity.TmpEmCgpn;
import aero.cubox.api.domain.entity.TmpEmPbsvnt;
import aero.cubox.api.domain.entity.TmpEmVisit;
import aero.cubox.api.mdm.service.MdmService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class TmpSyncScheduleService {

    @Autowired
    TmpEmMapper tmpEmMapper;
    @Autowired
    TmpEmCgpnRepository tmpEmCgpnRepository;

    @Autowired
    TmpEmPbsvntRepository tmpEmPbsvntRepository;

    @Autowired
    TmpEmVisitRepository tmpEmVisitRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    private MdmService mdmService;

    public void resyncCard() {

        log.info("누락정보 동기화....");

        int hr_sn_cgpn = 4848660 +1 ;
        while (true) {
            Map<String, Object> params = new HashMap<>();

            params.put("hr_sn", hr_sn_cgpn);
            List<TmpEmCgpn> tmpEmList = tmpEmMapper.getListCgpn(params);
            if (tmpEmList.size() == 0) {
                break;
            }

            for(TmpEmCgpn tmpEm : tmpEmList){
                hr_sn_cgpn = tmpEm.getCgpnHrSn();

                String cardNo = tmpEm.getIssuNo();
                cardNo = cardNo.replaceFirst("^0+(?!$)", "");
                if ( cardNo.length() < 8)
                {
                    cardNo = StringUtils.leftPad(cardNo, 8, "0");
                }

                Optional<Card> oCard = cardRepository.findByCardNo(cardNo);

                if(oCard.isPresent()){
                    tmpEm.setDoneYn("S");
                    tmpEmCgpnRepository.save(tmpEm);
                } else {
                    tmpEm.setDoneYn("Y");
                    tmpEmCgpnRepository.save(tmpEm);

                    Map<String, Object> param = new HashMap<>();
                    param.put("cgpn_hr_sn", tmpEm.getCgpnHrSn());
                    // 동기화 진행할 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => N) 업데이트
                    mdmService.updateMdmTcEmCgpnReSync(param);
                }
            }
        }

        int hr_sn_pbsvnt = 4849044 +1 ;
        while (true) {
            Map<String, Object> params = new HashMap<>();

            params.put("hr_sn", hr_sn_pbsvnt);
            List<TmpEmPbsvnt> tmpEmList = tmpEmMapper.getListPbsvnt(params);
            if (tmpEmList.size() == 0) {
                break;
            }

            for(TmpEmPbsvnt tmpEm : tmpEmList){
                hr_sn_pbsvnt = tmpEm.getCgpnHrSn();

                Optional<Card> oCard = cardRepository.findByCardNo(tmpEm.getIssuNo());

                if(oCard.isPresent()){
                    tmpEm.setDoneYn("S");
                    tmpEmPbsvntRepository.save(tmpEm);
                } else {
                    tmpEm.setDoneYn("Y");
                    tmpEmPbsvntRepository.save(tmpEm);

                    Map<String, Object> param = new HashMap<>();
                    param.put("cgpn_hr_sn", tmpEm.getCgpnHrSn());
                    // 동기화 진행할 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => N) 업데이트
                    mdmService.updateMdmTcEmPbsvntReSync(param);
                }
            }
        }

        int hr_sn_visit = 4849073 +1 ;
        while (true) {
            Map<String, Object> params = new HashMap<>();

            params.put("hr_sn", hr_sn_visit);
            List<TmpEmVisit> tmpEmList = tmpEmMapper.getListVist(params);
            if (tmpEmList.size() == 0) {
                break;
            }

            for(TmpEmVisit tmpEm : tmpEmList){
                hr_sn_visit = tmpEm.getCgpnHrSn();

                Optional<Card> oCard = cardRepository.findByCardNo(tmpEm.getIssuNo());

                if(oCard.isPresent()){
                    tmpEm.setDoneYn("S");
                    tmpEmVisitRepository.save(tmpEm);
                } else {
                    tmpEm.setDoneYn("Y");
                    tmpEmVisitRepository.save(tmpEm);

                    Map<String, Object> param = new HashMap<>();
                    param.put("cgpn_hr_sn", tmpEm.getCgpnHrSn());
                    // 동기화 진행할 데이터는 연계데이터처리유무컬럼(process_yn_mdmsjsc => N) 업데이트
                    mdmService.updateMdmTcEmVisitReSync(param);
                }
            }
        }
    }

}
