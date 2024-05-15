package g3.qm.queuemanager.timers;

import g3.qm.queuemanager.dtos.DecisionItem;
import g3.qm.queuemanager.services.DecisionCreatorService;
import g3.qm.queuemanager.services.DecisionUpdaterService;
import g3.qm.queuemanager.services.TimerCreatorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.TimerTask;

public class DecisionCreatorTimer extends TimerTask {
    @Autowired
    private TimerCreatorService timerCreatorService;
    @Autowired
    private DecisionCreatorService decisionCreatorService;
    @Autowired
    private DecisionUpdaterService decisionUpdaterService;

    private final Logger LOGGER = LogManager.getLogger(DecisionCreatorTimer.class);

    @Override
    public void run() {
        try {
            List<DecisionItem> decision = decisionCreatorService.createDecision();
            decisionUpdaterService.updateDecision(decision);
        } catch (Exception ex) {
            LOGGER.error("Error while update decision: ", ex);
        } finally {
            timerCreatorService.createDecisionCreatorTimer();
        }
    }
}
