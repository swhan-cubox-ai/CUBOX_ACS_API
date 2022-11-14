package aero.cubox.api.card.service;

import aero.cubox.api.card.mapper.CardMapper;
import aero.cubox.api.instt.mapper.InsttMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CardService {

    @Autowired
    private CardMapper mapper;

    // mdm 카드 동기화
    public int updateCard(Map<String, Object> mdmCardItem){
        // 1.기등록여부를 조회한 후
        Map<String, String> isYnMap = mapper.getCardIsYn(mdmCardItem);
        String isYn = isYnMap.get("isYn");
        String card_no = mdmCardItem.get("card_no").toString();

        int updateCnt = 0;
        if("N".equals(isYn)){
            // 2-1. 없으면 인서트
             updateCnt = mapper.insertCard(mdmCardItem);
        } else {
            // 2-2. 있으면 수정
            updateCnt = mapper.updateCard(mdmCardItem);
        }

        return updateCnt;
    }

    public int updateCardEmpId(Map<String, Object> cardItem){
        int updateCnt = 0;
        updateCnt = mapper.updateCardEmpId(cardItem);
        return updateCnt;
    }

}
