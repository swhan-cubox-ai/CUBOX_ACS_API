package aero.cubox.api;

import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class KafkaTest {

    private String message = "";

    @Test
    public void kafkaTest() {
        try {

            //kafka 브로커
//            String kafkaBootstrapServer = "bpdev01.smartgbmo.go.kr:6667,bpdev02.smartgbmo.go.kr:6667,bpdev03.smartgbmo.go.kr:6667";
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
            System.out.println(String.format("kafka test subscribe... topic consumer try.. kafkaBootstrapServer = [%s], topic = [%s], group = [%s]", kafkaBootstrapServer, topics.toString(), groupId));
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
            consumer.subscribe(topics);
            System.out.println(String.format("kafka test subscribe... topic consumer started.. kafkaBootstrapServer = [%s], topic = [%s], group = [%s]", kafkaBootstrapServer, topics.toString(), groupId));

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
                                System.out.println(String.format("kafaka read %d : topic [%s] offset [%d] message : %s", messageIndex, record.topic(), record.offset(), message));
                                //topic 별 메세지 처리
                            }
                        }else {
                            System.out.println(String.format("kafaka read none, index = %d", messageIndex));
                        }
                    }
                }
            } catch(Exception e1) {
                System.out.println(String.format("kafaka read 에러 : %s", e1.getMessage()));
            } finally {
                consumer.close();
            }

        }catch(Exception e) {
            this.message = String.format("test error : %s",  e.getMessage());
            System.out.println(this.message);
        }
    }

}
