package g3.qm.queuemanager.timers;

import g3.qm.queuemanager.dtos.DecisionItem;
import g3.qm.queuemanager.services.DecisionCreatorService;
import g3.qm.queuemanager.services.DecisionUpdaterService;
import g3.qm.queuemanager.services.TimerCreatorService;
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

    @Override
    public void run() {
        List<DecisionItem> decision = decisionCreatorService.createDecision();
        decisionUpdaterService.updateDecision(decision);
        timerCreatorService.createDecisionCreatorTimer();
    }
}
