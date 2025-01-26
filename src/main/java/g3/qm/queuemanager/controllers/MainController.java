package g3.qm.queuemanager.controllers;

import g3.qm.queuemanager.services.DecisionUpdaterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final DecisionUpdaterService decisionUpdaterService;

    public MainController(DecisionUpdaterService decisionUpdaterService) {
        this.decisionUpdaterService = decisionUpdaterService;
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
}
