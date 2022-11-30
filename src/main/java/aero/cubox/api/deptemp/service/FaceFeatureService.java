package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.repository.FaceFeatureRepository;
import aero.cubox.api.domain.entity.FaceFeature;
import aero.cubox.api.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FaceFeatureService extends AbstractService<FaceFeature, Integer> {

    @Autowired
    private FaceFeatureRepository repository;

    @Override
    protected JpaRepository<FaceFeature, Integer> getRepository() {
        return repository;
    }

    public List<FaceFeature> findAllByFaceFeatureAlchera()
    {
        String faceFeatureTyp = "FFT003";
        return repository.findTop10ByFaceFeatureTypAndFeatureMaskIsNullOrderByIdAsc(faceFeatureTyp);
    }

    public List<FaceFeature> findAllByFaceFeatureAlchera2()
    {
        String faceFeatureTyp = "FFT003";
        int faceId = 145000;
        return repository.findTop10ByFaceFeatureTypAndFeatureMaskIsNullAndFaceIdIsLessThanOrderByIdAsc(faceFeatureTyp, faceId);
    }

    public List<FaceFeature> findAllByFaceFeatureAlchera3()
    {
        String faceFeatureTyp = "FFT003";
        int faceId = 145000;
        return repository.findTop10ByFaceFeatureTypAndFeatureMaskIsNullAndFaceIdIsGreaterThanEqualOrderByIdAsc(faceFeatureTyp, faceId);
    }

}
