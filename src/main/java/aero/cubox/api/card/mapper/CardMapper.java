package aero.cubox.api.card.mapper;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface CardMapper {

    Map<String, String> getCardIsYn(Map<String, Object> mdmCardItem);

    int updateCard(Map<String, Object> mdmCardItem);

    int insertCard(Map<String, Object> mdmCardItem);

    int updateCardEmpId(Map<String, Object> mdmCardItem);

}
