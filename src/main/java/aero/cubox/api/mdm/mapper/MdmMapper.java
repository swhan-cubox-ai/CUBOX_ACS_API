package aero.cubox.api.mdm.mapper;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface MdmMapper {

    List<Map<String, Object>> test (Integer count);

    List<Map<String, Object>> getMdmInsttRcv();

    int updateMdmInsttRcv(Map<String, Object> insttRcv);
}
