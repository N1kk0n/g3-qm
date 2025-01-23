package g3.qm.queuemanager.consumers;

import g3.qm.queuemanager.message.KafkaMessage;
import g3.qm.queuemanager.message.MessageContent;
import g3.qm.queuemanager.repositories.state.TopicMessageRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MessageConsumerService {

    private final TopicMessageRepository topicMessageRepository;
    private final String TOPIC_NAME = "qm-topic";
    private final Logger LOGGER = LogManager.getLogger(MessageConsumerService.class);

    public MessageConsumerService(TopicMessageRepository topicMessageRepository) {
        this.topicMessageRepository = topicMessageRepository;
    }

    @KafkaListener(topics = TOPIC_NAME, groupId = "qm")
    public void receiveMessage(UUID message_uuid) {
        KafkaMessage message = topicMessageRepository.getMessage(message_uuid);

        LOGGER.info("Message received: " +  message);
        MessageContent content = KafkaMessage.getContentObject(message);
        LOGGER.info("Message content: " + content);


        topicMessageRepository.commitReceiveMessage(message_uuid);
    }
}
