package aero.cubox.api.deptemp.mapper;

import aero.cubox.api.domain.entity.Card;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface CardMapper {

    public List<Card> getCardList(Map<String, Object> params);


}
