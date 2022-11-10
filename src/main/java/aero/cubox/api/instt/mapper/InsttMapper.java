package aero.cubox.api.instt.mapper;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface InsttMapper {

    int updateInsttRcv(Map<String, Object> insttRcv);

    int updateDeptRcv(Map<String, Object> insttRcv);

    int insertInsttRcv(Map<String, Object> insttRcv);

    int insertDeptRcv(Map<String, Object> insttRcv);
}
