package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.mapper.FaceMapper;
import aero.cubox.api.deptemp.repository.FaceFeatureErrRepository;
import aero.cubox.api.deptemp.repository.FaceFeatureRepository;
import aero.cubox.api.deptemp.repository.FaceRepository;
import aero.cubox.api.domain.entity.Face;
import aero.cubox.api.domain.entity.FaceFeature;
import aero.cubox.api.domain.entity.FaceFeatureErr;
import aero.cubox.api.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@EnableScheduling
public class FaceService extends AbstractService<Face, Integer> {

    @Autowired
    private FaceMapper mapper;

    @Autowired
    private FaceRepository repository;

    @Autowired
    private FaceFeatureRepository faceFeatureRepo;

    @Autowired
    private FaceFeatureErrRepository faceFeatureErrRepo;

    @Override
    protected JpaRepository<Face, Integer> getRepository() {
        return repository;
    }


    public Optional<Face> findFirstByEmpCd(String empCd) {
        return repository.findFirstByEmpCdOrderByIdDesc(empCd);
    }

    public List<Face> findAllByFaceStateTyp(String faceStateTyp)
    {
        return repository.findAllByFaceStateTyp(faceStateTyp);
    }

    public void saveFaceFeatrue(FaceFeature faceFeature)
    {
        faceFeatureRepo.save(faceFeature);
    }

    public void saveFaceFeatrueErr(FaceFeatureErr faceFeatureErr)
    {
        faceFeatureErrRepo.save(faceFeatureErr);
    }

}
