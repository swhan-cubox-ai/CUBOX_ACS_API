package aero.cubox.api.deptemp.service;

import aero.cubox.api.domain.entity.Emp;
import aero.cubox.api.domain.entity.Face;
import aero.cubox.api.domain.entity.FaceFeature;
import aero.cubox.api.domain.entity.FaceFeatureErr;
import aero.cubox.api.util.CuboxTerminalUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
@EnableScheduling
@Profile("prod")
public class FaceScheduleService {

    @Autowired
    private FaceService faceService;


    @Autowired
    private EmpService empService;

    @Value("${cuboxacs.upload_directory}")
    String upload_directory;
    @Value("${cuboxacs.move_directory}")
    String move_directory;

    @Value("${cuboxacs.cubox_frs_host}")
    String cubox_host;

    @Value("${cuboxacs.archera_api_host}")
    String archera_host;

    @Scheduled(cron = "0/10 * * * * *")
    public void insertFace() throws Exception {

        log.info("insertFace....");

        File uploadFile = new File(upload_directory);
        File[] fileList = uploadFile.listFiles();

        log.info("upload:"  + upload_directory);

        for(int i=0; i<fileList.length; i++){

            File file = fileList[i];
            String fileName = file.getName();

            //파일명 => 직원코드 . 확장자
            int index = fileName.lastIndexOf(".");

            if ( index < 1 )
            {
                continue;
            }

            String extension = fileName.substring(index + 1);
            String emp_cd = fileName.substring(0,index);

            byte[] face_img = Files.readAllBytes(file.toPath());

            Face face = Face.builder()
                    .empCd(emp_cd)
                    .faceImg(face_img)
                    .faceStateTyp("FST001") // 대기상태
                    .createdAt(new Timestamp(new Date().getTime()))
                    .updatedAt(new Timestamp(new Date().getTime()))
                    .build();
            Optional<Emp> oEmp = empService.findByEmpCd(emp_cd);
            if ( oEmp.isPresent())
            {
                face.setEmpId(oEmp.get().getId());
            }
            face = faceService.save(face);

            // 기존에 등록된 사진이 있다면 SKIP
//            if ( oEmp.isPresent() &&  oEmp.get().getFaceId() != null )
//            {
//                //skip
//            } else {
//                if ( oEmp.isPresent() ) {
//                    face.setEmpId(oEmp.get().getId());
//                }
//                face = faceService.save(face);
//            }

            // 파일 동기화 처리 이후 파일 이동. window
//            String filePath = file.getPath();
//            Path resourcePath = Paths.get(filePath);
//            Path backupPath = Paths.get(move_directory+"/" + fileName);
            //Files.move(resourcePath, backupPath, StandardCopyOption.REPLACE_EXISTING);

            file.renameTo(new File(move_directory+"/" + fileName));

            // TO-DO
            // ERROR

        }
    }




}
