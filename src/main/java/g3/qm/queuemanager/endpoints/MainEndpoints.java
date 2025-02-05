package g3.qm.queuemanager.endpoints;

import g3.qm.queuemanager.decisioncontroller.services.DecisionUpdaterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainEndpoints {

    private final DecisionUpdaterService decisionUpdaterService;

    public MainEndpoints(DecisionUpdaterService decisionUpdaterService) {
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
