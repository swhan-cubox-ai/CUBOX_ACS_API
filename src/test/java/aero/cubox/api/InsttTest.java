package aero.cubox.api;

import aero.cubox.api.instt.service.InsttService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ActiveProfiles({"local"})
@SpringBootTest
public class InsttTest {

    @Autowired
    private InsttService insttService;


    @Test
    public void syncInstt() throws Exception {
        insttService.syncInstt();

    }

}
