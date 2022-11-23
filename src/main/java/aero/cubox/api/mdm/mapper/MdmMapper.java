package aero.cubox.api.mdm.mapper;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface MdmMapper {

    List<Map<String, Object>> getMdmInsttRcv();

    int updateMdmInsttRcv(Map<String, Object> insttRcv);

    // 일반출입증
    List<Map<String, Object>> getMdmTcEmCgpn();
    int updateMdmTcEmCgpn(Map<String, Object> cardItem);

    // 공무원증
    List<Map<String, Object>> getMdmTcEmPbsvnt();
    int updateMdmTcEmPbsvnt(Map<String, Object> cardItem);

    // 방문자출입증
    List<Map<String, Object>> getMdmTcEmVisit();
    int updateMdmTcEmVisit(Map<String, Object> cardItem);
}
