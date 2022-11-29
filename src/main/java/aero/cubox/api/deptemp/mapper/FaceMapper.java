package aero.cubox.api.deptemp.mapper;

import aero.cubox.api.domain.entity.Face;
import aero.cubox.api.sync.vo.EmpVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface FaceMapper {

    public Face getFace(Map<String, Object> params);

    public List<Face> getFaceFeatureMasknull();
}
