package aero.cubox.api.deptemp.repository;

import aero.cubox.api.domain.entity.Face;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FaceRepository extends JpaRepository<Face, Integer> {

    List<Face> findAllByEmpCd(String empCd);

    Optional<Face> findFirstByEmpCdAAndFaceStateTypOrderByIdDesc(String empCd, String faceStateTyp);

    List<Face> findTop100ByFaceStateTypOrderByCreatedAt(String faceStateTyp);


}
