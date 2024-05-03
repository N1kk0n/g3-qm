package g3.qm.queuemanager.services;

import g3.qm.queuemanager.timers.DecisionCreatorTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Timer;

@Service
public class TimerCreatorService {
    @Autowired
    private ApplicationContext applicationContext;

    public void createDecisionCreatorTimer() {
        DecisionCreatorTimer decisionCreatorTimer = applicationContext.getBean(DecisionCreatorTimer.class);
        //TODO: get this value from database
        int timerDelay = 15;

        Timer timer = new Timer("DECISION_CREATOR", true);
        timer.schedule(decisionCreatorTimer, timerDelay * 1000);
    }
}
