package aero.cubox.api.deptemp.mapper;

import aero.cubox.api.domain.entity.Face;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface FaceMapper {

    public Face getFace(Map<String, Object> params);

    public List<Face> getFaceFeatureMasknull();

    public Face getFaceInfoByEmpCd(String empCd);

    public List<Face> getFaceAllInfoByEmpCd(String empCd);

    public int updateFaceEmpCd(Face face);
}
