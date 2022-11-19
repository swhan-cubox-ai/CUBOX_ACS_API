package aero.cubox.api.deptemp.repository;

import aero.cubox.api.domain.entity.FaceFeature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FaceFeatureRepository extends JpaRepository<FaceFeature, Integer> {

}
