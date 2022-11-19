package aero.cubox.api.deptemp.repository;

import aero.cubox.api.domain.entity.Face;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FaceRepository extends JpaRepository<Face, Integer> {

    Optional<Face> findFirstByEmpCdOrderByIdDesc(String empCd);

    List<Face> findAllByFaceStateTyp(String faceStateTyp);

}
