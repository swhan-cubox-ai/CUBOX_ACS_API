package aero.cubox.api;

import aero.cubox.api.deptemp.repository.FaceFeatureRepository;
import aero.cubox.api.deptemp.service.FaceFeatureService;
import aero.cubox.api.domain.entity.FaceFeature;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RunWith(SpringRunner.class)
@ActiveProfiles({"dev"})
@SpringBootTest
public class CardTest {

//    @Autowired
//    private EmpService empService;
//
//
//    @Test
//    public void syncCard(){
//        empService.syncCard();
//
//    }

    @Autowired
    private FaceFeatureRepository faceFeatureRepository;
    @Autowired
    private FaceFeatureService faceFeatureService;

    @Test
    public void testTmp() {
        List<String> cardList = new ArrayList<>();
        cardList.add("012345678");
        cardList.add("1234");
        cardList.add("00000001154");
        cardList.add("000000000");
        cardList.add("0001");
        cardList.add("1740110-14143");
        for(int i=0; i<cardList.size(); i++){
            String originalCard = cardList.get(i);
            String cardNo = cardList.get(i);
            cardNo = cardNo.replaceFirst("^0+(?!$)", "");
            if (cardNo.length() < 8) {
                cardNo = StringUtils.leftPad(cardNo, 8, "0");
            }

            System.out.println(originalCard+ " >>>>> " + cardNo);
        }
    }

    @Test
    public void testTmp2() {
        int id = 88;
        Optional<FaceFeature> oFaceFeature = faceFeatureRepository.findById(id);
        if (oFaceFeature.isPresent()){
            FaceFeature faceFeature = oFaceFeature.get();
            String result = faceFeatureService.getLandmarkScore(faceFeature);

            if("ok".equals(result)){
                //성공시 facefeature 갱신
                System.out.println("성공@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            } else if("fail".equals(result)){
                //실패시 feature 삭제, 페이스상태 실패로 갱신
                System.out.println("실패@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                faceFeature.setFeature("");
                faceFeature.setFeatureMask(null);
                faceFeature.setUpdatedAt(new Timestamp(new Date().getTime()));
            }
        }
    }
}
