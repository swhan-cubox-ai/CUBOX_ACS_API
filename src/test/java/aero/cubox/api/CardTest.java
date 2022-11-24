package aero.cubox.api;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@ActiveProfiles({"local"})
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

    @Test
    public void testTmp() {
        List<String> cardList = new ArrayList<>();
        cardList.add("012345678");
        cardList.add("1234");
        cardList.add("00000001154");
        cardList.add("000000000");
        cardList.add("0001");
        cardList.add("12345678999848");
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
}
