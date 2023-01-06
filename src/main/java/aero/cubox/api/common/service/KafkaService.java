package aero.cubox.api.common.service;

import aero.cubox.api.common.mapper.TerminalMapper;
import aero.cubox.api.common.repository.FireHistRepository;
import aero.cubox.api.deptemp.mapper.CardMapper;
import aero.cubox.api.deptemp.repository.CardRepository;
import aero.cubox.api.domain.entity.Card;
import aero.cubox.api.domain.entity.FireHist;
import aero.cubox.api.domain.vo.ResultVo;
import aero.cubox.api.service.AbstractService;
import aero.cubox.api.sync.vo.DoorAlarmVO;
import aero.cubox.api.util.DigitalTwinUtil;
import com.github.pagehelper.util.StringUtil;
import com.google.gson.JsonObject;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;

@Service
@Slf4j
public class KafkaService  {

    @Autowired
    private FireHistRepository fireHistRepository;

    @Autowired
    private TerminalMapper terminalMapper;

    @Value("${cuboxacs.linx_host}")
    String linx_host;

    private String message = "";

    public void runKafka() {
        try {
            //kafka 브로커
            String kafkaBootstrapServer = "bpcta3.gbmo.go.kr:6667,bpcta4.gbmo.go.kr:6667,bpcta5.gbmo.go.kr:6667,bpcta6.gbmo.go.kr:6667,bpcta7.gbmo.go.kr:6667";

            //kafka topic -- 필요한 Tag 만 Add
            ArrayList<String> topics = new ArrayList<String>();
            //opics.add("smartgbmo-cityhub-tag-info") ; // Tag 기준 정보
            //topics.add("smartgbmo-cityhub-tag-value"); // Tag Value
            topics.add("smartgbmo-cityhub-alarm"); // alarm 정보

            //kafka consumer group
            // groupId는 kafka-group-시스템ID
            //String groupId = "kafka-group-bp";
            String groupId = "kafka-group-ac"; // ac 출입관리

            //kafka 연결 속성
            Properties properties = new Properties();
            properties.put("bootstrap.servers", kafkaBootstrapServer);
            properties.put("group.id", groupId);
            properties.put("enable.auto.commit", "true");
            properties.put("auto.offset.reset", "latest"); //메시지 Offset 정의
            properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

            //kafka consumer 생성 및 subscribe
            log.debug(String.format("kafka test subscribe... topic consumer try.. kafkaBootstrapServer = [%s], topic = [%s], group = [%s]", kafkaBootstrapServer, topics.toString(), groupId));
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
            consumer.subscribe(topics);
            log.debug(String.format("kafka test subscribe... topic consumer started.. kafkaBootstrapServer = [%s], topic = [%s], group = [%s]", kafkaBootstrapServer, topics.toString(), groupId));

            String message = null;
            int messageIndex = 0;
            try {
                while (true) {
                    // kafka 메시지 읽기
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(2000)); //2초 단위로 polling
                    if(records != null) {
                        if(records.count()>0) {
                            for (ConsumerRecord<String, String> record : records) {
                                message = record.value();
                                messageIndex ++;
                                log.debug(String.format("kafaka read %d : topic [%s] offset [%d] message : %s", messageIndex, record.topic(), record.offset(), message));
                                //topic 별 메세지 처리
                                messageProc(message);
                            }
                        }else {
                            log.debug(String.format("kafaka read none, index = %d", messageIndex));
                        }
                    }
                }
            } catch(Exception e1) {
                log.debug(String.format("kafaka read 에러 : %s", e1.getMessage()));
            } finally {
                consumer.close();
            }

        }catch(Exception e) {
            this.message = String.format("test error : %s",  e.getMessage());
            log.debug(this.message);
        }
    }


    public void messageProc(String message){
        if(message.contains("-")){
            //동 - 층 - 공정명 - 시설/장비 - 장비번호 - 상태유형 _ 00_00_00
            // 03-03-FI-HYD-01-AL_TEST
            String[] messages = message.split("-");
            String buildingCd = "";
            String floorCd = "";
            String process = "";
            String facility = "";
            String equipNo = "";
            String statusMsg = "";
            FireHist fileHist = new FireHist();

            // 메세지만 저장
            fileHist = FireHist.builder()
                    .msg(message)
                    .fireYn("N")
                    .createdAt(new Timestamp(new Date().getTime()))
                    .build();

            if(messages.length >= 6){
                buildingCd = messages[0];
                floorCd = messages[1];
                process = messages[2];
                facility = messages[3];
                equipNo = messages[4];
                statusMsg = messages[5];

                if("FI".equals(process) && //3번째가 FI이면서
                 ("HYD".equals(facility) || "FLS".equals(facility) || "SMD".equals(facility)|| "HTD".equals(facility)) && //4번째가 HYD(소화전) or FLS(불꽃감지기) or SMD(연기감지기) or HTD(열감지기)이면서
                   statusMsg.startsWith("AL") //6번째가 AL이면 소방 신호로 판단하시면 됩니다.
                ){
                    System.out.println("소방신호발생");
                    // 메시지 저장
                    fileHist = FireHist.builder()
                            .f1(buildingCd)
                            .f2(floorCd)
                            .f3(process)
                            .f4(facility)
                            .f5(equipNo)
                            .f6(statusMsg)
                            .msg(message)
                            .fireYn("Y")
                            .createdAt(new Timestamp(new Date().getTime()))
                            .build();
                    // 단말기에 해당동 오픈고정 시행
                    openCuboxTerminal(buildingCd);
                    // 링스로 오픈고정 시행
                    openLinxTerminal(buildingCd);
                }
            }
            fireHistRepository.save(fileHist);

        }

    }


    public void openCuboxTerminal(String buildingCd){
        List<String> ipAddrList = terminalMapper.getCUBOXIpAddrList(buildingCd);
        String port = ":5000";
        String api = "/fire?fireOn=true";
        for(String ip : ipAddrList){
            String uri = ip + port + api;
            if("172.29.7.5".equals(ip) || "172.29.7.4".equals(ip)){
                continue;
            }
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, null , String.class);
        }
    }



    public void openLinxTerminal(String buildingCd){
        List<String> ipAddrList = terminalMapper.getLINXIpAddrList(buildingCd);

        String api = "/push/pushCommand";
        for(String ip : ipAddrList){
            String uri = linx_host + api;

            Map<String, Object> params = new HashMap<>();
            params.put("terminalIp", ip);
            params.put("commandId", "0012");
            params.put("sub", 1);

            RestTemplate restTemplate = new RestTemplate();
            JsonObject properties = new JsonObject();
            properties.addProperty("fireOn", true);

//            ResponseEntity<String> response = restTemplate.getForEntity(uri , String.class, params);
        }


    }

}
