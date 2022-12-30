package aero.cubox.api.deptemp.mapper;

import aero.cubox.api.domain.entity.TmpEmCgpn;
import aero.cubox.api.domain.entity.TmpEmPbsvnt;
import aero.cubox.api.domain.entity.TmpEmVisit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface TmpEmMapper {

    List<TmpEmCgpn> getListCgpn(Map<String, Object> params);
    List<TmpEmPbsvnt> getListPbsvnt(Map<String, Object> params);
    List<TmpEmVisit> getListVist(Map<String, Object> params);
}
