package aero.cubox.api.instt.mapper;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface InsttMapper {

    Map<String, String> getInsttIsYn(Map<String, Object> insttRcv);

    Map<String, String> getDeptIsYn(Map<String, Object> insttRcv);

    int insertInsttRcv(Map<String, Object> insttRcv);

    int insertDeptRcv(Map<String, Object> insttRcv);

    int updateInsttRcv(Map<String, Object> insttRcv);

    int updateDeptRcv(Map<String, Object> insttRcv);

}
