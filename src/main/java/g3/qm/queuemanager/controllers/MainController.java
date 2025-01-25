package g3.qm.queuemanager.controllers;

import g3.qm.queuemanager.message.KafkaMessage;
import g3.qm.queuemanager.message.MessageContent;
import g3.qm.queuemanager.producers.MessageProducerService;
import g3.qm.queuemanager.services.DecisionUpdaterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/test_message/{count}")
    public ResponseEntity<String> testMessage(@PathVariable Integer count) {
        for (long i = 1; i <= count / 2; i++) {
            for (long j = 1; j <= 2; j++) {
                MessageContent content = new MessageContent();
                content.setRoute_id(i);
                content.setGraph_id(10 * i);
                content.setOperation("test-" + j);
                content.setTask_id(100 * i);
                content.setSession_id(1000 * i);

                KafkaMessage message = new KafkaMessage();
                message.setRoute_id(content.getRoute_id());
                message.setProducer("qm");
                message.setConsumer("rm");
                message.setIs_received(false);
                message.setContent(MessageContent.json(content));

                messageProducerService.sendMessage("rm-topic", message);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
