package g3.qm.queuemanager.controllers;

import g3.qm.queuemanager.message.KafkaMessage;
import g3.qm.queuemanager.message.MessageContent;
import g3.qm.queuemanager.producers.MessageProducerService;
import g3.qm.queuemanager.repositories.inner.InnerParamRepository;
import g3.qm.queuemanager.services.DecisionUpdaterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final DecisionUpdaterService decisionUpdaterService;
    private final MessageProducerService messageProducerService;

    public MainController(DecisionUpdaterService decisionUpdaterService, MessageProducerService messageProducerService) {
        this.decisionUpdaterService = decisionUpdaterService;
        this.messageProducerService = messageProducerService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/update_decision")
    public ResponseEntity<String> updateDecision() {
        decisionUpdaterService.setDecisionUpdateFlagOn();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/test_message")
    public ResponseEntity<String> testMessage() {
        MessageContent content = new MessageContent();
        content.setRoute_id(1L);
        content.setGraph_id(11L);
        content.setOperation("test");
        content.setTask_id(111L);
        content.setSession_id(1111L);

        KafkaMessage message = new KafkaMessage();
        message.setRoute_id(content.getRoute_id());
        message.setProducer("qm");
        message.setConsumer("qm");
        message.setContent(MessageContent.json(content));

        messageProducerService.sendMessage("qm-topic", message);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
