package aero.cubox.api.deptemp.service;

import aero.cubox.api.deptemp.mapper.FaceMapper;
import aero.cubox.api.deptemp.repository.FaceFeatureErrRepository;
import aero.cubox.api.deptemp.repository.FaceFeatureRepository;
import aero.cubox.api.deptemp.repository.FaceRepository;
import aero.cubox.api.domain.entity.Face;
import aero.cubox.api.domain.entity.FaceFeature;
import aero.cubox.api.domain.entity.FaceFeatureErr;
import aero.cubox.api.service.AbstractService;
import aero.cubox.api.sync.vo.EmpVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class FaceService extends AbstractService<Face, Integer> {

    @Autowired
    private FaceMapper mapper;

    @Autowired
    private FaceRepository repository;

    // TO-DO FaceFeatureService로 이동
    @Autowired
    private FaceFeatureRepository faceFeatureRepo;

    @Autowired
    private FaceFeatureErrRepository faceFeatureErrRepo;

    @Override
    protected JpaRepository<Face, Integer> getRepository() {
        return repository;
    }

    public List<Face> findTop100ByFaceStateTyp(String faceStateTyp)
    {
        return repository.findTop100ByFaceStateTypOrderByCreatedAt(faceStateTyp);
    }

    public void saveFaceFeatrue(FaceFeature faceFeature)
    {
        faceFeatureRepo.save(faceFeature);
    }

    public void saveFaceFeatrueErr(FaceFeatureErr faceFeatureErr)
    {
        faceFeatureErrRepo.save(faceFeatureErr);
    }


    public Optional<FaceFeature> findFaceFeatrue(Integer faceId, String empCd, String faceFeatureTyp)
    {
        return faceFeatureRepo.findFirstByFaceIdAndEmpCdAndFaceFeatureTyp(faceId, empCd, faceFeatureTyp);
    }

    public Face getFace(Map<String, Object> params) {
        return mapper.getFace(params);
    }

    public List<Face> getFaceFeatureMasknull()
    {
        return mapper.getFaceFeatureMasknull();
    }

    public List<Face> feature2732()
    {
        List<Face> test = new ArrayList<>();
        return test;
    }

    public List<Face> findTop100ByIdGreaterThanOrderByIdAsc(int id){
        return repository.findTop100ByIdGreaterThanOrderByIdAsc(id);
    }

}
