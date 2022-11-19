package aero.cubox.api;

import aero.cubox.api.face.service.FaceService;
import net.minidev.json.parser.ParseException;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ActiveProfiles({"local"})
@SpringBootTest
public class FaceTest {
    @Value("${cuboxacs.upload_directory}")
    String upload_directory;
    @Value("${cuboxacs.move_directory}")
    String move_directory;


    @Autowired
    private FaceService faceService;

    @Test
    public void insertFace() throws Exception {
        faceService.insertFace();
    }


    @Test
    public void getFeatures() throws  JSONException{
        faceService.getFeatures();
    }

}



