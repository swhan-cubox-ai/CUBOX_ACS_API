package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.mapper.CardMapper;
import aero.cubox.api.deptemp.mapper.EntHistMapper;
import aero.cubox.api.deptemp.repository.CardRepository;
import aero.cubox.api.deptemp.repository.EntHistBioRepository;
import aero.cubox.api.deptemp.repository.EntHistRepository;
import aero.cubox.api.deptemp.vo.EntHistVO;
import aero.cubox.api.domain.entity.Card;
import aero.cubox.api.domain.entity.EntHist;
import aero.cubox.api.domain.entity.EntHistBio;
import aero.cubox.api.domain.entity.FaceFeature;
import aero.cubox.api.service.AbstractService;
import aero.cubox.api.util.CuboxTerminalUtil;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class EntHistService extends AbstractService<EntHist, Integer> {

    @Autowired
    private EntHistMapper mapper;

    @Autowired
    private EntHistRepository repository;

    @Autowired
    private EntHistBioRepository entHistBiorepository;

    @Override
    protected JpaRepository<EntHist, Integer> getRepository() {
        return repository;
    }

    @Transactional
    public EntHist saveEntHist(EntHistVO entHistVo) throws Exception {

        String img = entHistVo.getEntFaceImg();
        if (entHistVo.getEntFaceImg() != null && !"".equals(entHistVo.getEntFaceImg())) {
            //img = CuboxTerminalUtil.byteArrDecode(entHistVo.getEntFaceImg());

        }


        EntHist entHist = new EntHist();

        //entHist.setEvtDt(entHistVo.getEvtDt());
        entHist.setEvtDt(Timestamp.valueOf(entHistVo.getEvtDt())); // YYYY-MM-DD hh:mm:ss.ssssss
        entHist.setEntEvtTyp(entHistVo.getEntEvtTyp());
        entHist.setTerminalCd(entHistVo.getTerminalCd());
        entHist.setEmpCd(entHistVo.getEmpCd());
        entHist.setEmpNm(entHistVo.getEmpNm());
        entHist.setFaceId(entHistVo.getFaceId());
        entHist.setCardNo(entHistVo.getCardNo());
        entHist.setCardClassTyp(entHistVo.getCardClassTyp());
        entHist.setCardStateTyp(entHistVo.getCardStateTyp());
        entHist.setCardTagTyp(entHistVo.getCardTagTyp());
        if (!StringUtil.isEmpty(entHistVo.getBegDt())) { entHist.setBegDt(Timestamp.valueOf(entHistVo.getBegDt())); }
        //entHist.setEndDt(entHistVo.getEndDt());
        if (!StringUtil.isEmpty(entHistVo.getEndDt())) { entHist.setBegDt(Timestamp.valueOf(entHistVo.getEndDt())); }
        entHist.setAuthWayTyp(entHistVo.getAuthWayTyp());
        entHist.setMatchScore(entHistVo.getMatchScore());
        entHist.setFaceThreshold(entHistVo.getFaceThreshold());
//        entHist.setCaptureAt(entHistVo.getCaptureAt());
//        entHist.setTagAt(entHistVo.getTagAt());
        if (!StringUtil.isEmpty(entHistVo.getCaptureAt())) { entHist.setBegDt(Timestamp.valueOf(entHistVo.getCaptureAt())); }
        if (!StringUtil.isEmpty(entHistVo.getTagAt())) { entHist.setBegDt(Timestamp.valueOf(entHistVo.getTagAt())); }
        entHist.setTagCardNo(entHistVo.getTagCardNo());
        entHist.setTagEmpCd(entHistVo.getTagEmpCd());
        entHist.setTemper(entHistVo.getTemper());
        entHist.setMaskConfidence(entHistVo.getMaskConfidence());
        entHist.setTerminalTyp(entHistVo.getTerminalTyp());
        entHist.setDoorCd(entHistVo.getDoorCd());
        entHist.setDoorNm(entHistVo.getDoorNm());
        entHist.setBuildingCd(entHistVo.getBuildingCd());
        entHist.setBuildingNm(entHistVo.getBuildingNm());
        entHist.setDeptCd(entHistVo.getDeptCd());
        entHist.setDeptNm(entHistVo.getDeptNm());
        entHist.setBelongNm(entHistVo.getBelongNm());
        entHist.setCreatedAt(new Timestamp(new Date().getTime()));
        entHist.setUpdatedAt(new Timestamp(new Date().getTime()));

        entHist = repository.save(entHist);

        if (img != null) {
            EntHistBio entHistBio = new EntHistBio();
            entHistBio.setEntHistId(entHist.getId());
            entHistBio.setEntFaceImg(img);
            entHistBio.setCreatedAt(new Timestamp(new Date().getTime()));
            entHistBio.setUpdatedAt(new Timestamp(new Date().getTime()));
            entHistBiorepository.save(entHistBio);

        }
        return entHist;
    }
    //public Optional<Card> findByCardNo(String cardNo) { return repository.findByCardNo(cardNo); }

    //public List<Card> getCardList(Map<String, Object> params) { return mapper.getCardList(params);}

    public EntHistVO getTerminalInfoById(String terminalCd) {
        return mapper.getTerminalInfoById(terminalCd);
    }


}
