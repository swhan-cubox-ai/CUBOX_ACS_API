package aero.cubox.api.deptemp.mapper;

import aero.cubox.api.deptemp.vo.EntHistVO;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface EntHistMapper {

    EntHistVO getTerminalInfoById(String terminalCd);

}
