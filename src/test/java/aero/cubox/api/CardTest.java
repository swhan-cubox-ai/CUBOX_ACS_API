package aero.cubox.api;

import aero.cubox.api.card.service.CardService;
import aero.cubox.api.emp.service.EmpService;
import aero.cubox.api.instt.service.InsttService;
import aero.cubox.api.mdm.service.MdmService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


@RunWith(SpringRunner.class)
@ActiveProfiles({"local"})
@SpringBootTest
public class CardTest {

    @Autowired
    private EmpService empService;


    @Test
    public void syncCard(){
        empService.syncCard();

    }

}
