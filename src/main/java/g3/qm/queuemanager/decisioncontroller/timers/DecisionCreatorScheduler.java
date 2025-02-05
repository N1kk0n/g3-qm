package g3.qm.queuemanager.decisioncontroller.timers;

import g3.qm.queuemanager.decisioncontroller.dtos.DecisionItem;
import g3.qm.queuemanager.decisioncontroller.services.DecisionCreatorService;
import g3.qm.queuemanager.decisioncontroller.services.DecisionUpdaterService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DecisionCreatorScheduler {

    private final DecisionCreatorService decisionCreatorService;
    private final DecisionUpdaterService decisionUpdaterService;
    private final Logger LOGGER = LogManager.getLogger(DecisionCreatorScheduler.class);

    public DecisionCreatorScheduler(DecisionCreatorService decisionCreatorService, DecisionUpdaterService decisionUpdaterService) {
        this.decisionCreatorService = decisionCreatorService;
        this.decisionUpdaterService = decisionUpdaterService;
    }

    @Scheduled(fixedRate = 2000)
    public void createDecision() {
        try {
            if (decisionUpdaterService.decisionUpdateFlag()) {
                LOGGER.info("Update decision");
                decisionUpdaterService.setDecisionUpdateFlagOff();
                List<DecisionItem> decision = decisionCreatorService.createDecision();
                decisionUpdaterService.updateDecision(decision);
            }
        } catch (Exception ex) {
            LOGGER.error("Error while update decision: ", ex);
        }
    }
}
