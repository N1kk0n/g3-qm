package g3.qm.queuemanager.services;

import g3.qm.queuemanager.entites.QueueManagerParam;
import g3.qm.queuemanager.repositories.jpa.JpaQueueManagerParamRepository;
import g3.qm.queuemanager.timers.DecisionCreatorTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Timer;

@Service
public class TimerCreatorService {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private JpaQueueManagerParamRepository jpaQueueManagerParamRepository;

    public void createDecisionCreatorTimer() {
        DecisionCreatorTimer decisionCreatorTimer = applicationContext.getBean(DecisionCreatorTimer.class);
        int timerDelay = getDecisionTimeout();

        Timer timer = new Timer("DECISION_CREATOR", true);
        timer.schedule(decisionCreatorTimer, timerDelay * 1000L);
    }

    private int getDecisionTimeout() {
        Optional<QueueManagerParam> queueManagerParamOptional = jpaQueueManagerParamRepository.findByParamName("DECISION_TIMEOUT_SEC");
        return queueManagerParamOptional
                .map(queueManagerParam -> Integer.parseInt(queueManagerParam.getParamValue()))
                .orElse(-1);
    }
}
