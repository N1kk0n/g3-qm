package g3.qm.queuemanager.timers;

import g3.qm.queuemanager.services.DecisionCreatorService;
import g3.qm.queuemanager.services.TimerCreatorService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.TimerTask;

public class DecisionCreatorTimer extends TimerTask {
    @Autowired
    private TimerCreatorService timerCreatorService;
    @Autowired
    DecisionCreatorService decisionCreatorService;

    @Override
    public void run() {
        decisionCreatorService.createDecision();
//        timerCreatorService.createDecisionCreatorTimer();
    }
}
