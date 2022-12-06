package aero.cubox.api.deptemp.repository;

import aero.cubox.api.domain.entity.Face;
import aero.cubox.api.domain.entity.FaceFeature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FaceRepository extends JpaRepository<Face, Integer> {

    List<Face> findAllByEmpCd(String empCd);

    Optional<Face> findFirstByEmpCdAndFaceStateTypOrderByIdDesc(String empCd, String faceStateTyp);

    List<Face> findTop100ByFaceStateTypOrderByCreatedAt(String faceStateTyp);


    List<Face> findTop100ByIdGreaterThanOrderByIdAsc(int id);


}
