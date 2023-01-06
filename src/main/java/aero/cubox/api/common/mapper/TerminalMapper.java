package aero.cubox.api.common.mapper;

import aero.cubox.api.domain.entity.DoorAlarmHist;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TerminalMapper {

    public List<String> getCUBOXIpAddrList(String buildingCd);

    public List<String> getLINXIpAddrList(String buildingCd);
    public DoorAlarmHist getTerminalInfo(String terminalCd);


}
