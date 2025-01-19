package g3.qm.queuemanager.consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UpdateDecisionConsumer {

    private final String TOPIC_NAME = "qm-topic";

    @KafkaListener(topics = TOPIC_NAME, groupId = "qm")
    public void getUpdateMessage() {
        //TODO: 
    }
}
