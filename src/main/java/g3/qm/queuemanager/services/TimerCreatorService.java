package g3.qm.queuemanager.services;

import g3.qm.queuemanager.entites.QueueManagerParam;
import g3.qm.queuemanager.repositories.jpa.JpaQueueManagerParamRepository;
import g3.qm.queuemanager.timers.DecisionCreatorTimer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private final Logger LOGGER = LogManager.getLogger(TimerCreatorService.class);

    public void createDecisionCreatorTimer() {
        DecisionCreatorTimer decisionCreatorTimer = applicationContext.getBean(DecisionCreatorTimer.class);
        try {
            int timerDelay = getDecisionTimeout();

            Timer timer = new Timer("DECISION_CREATOR", true);
            timer.schedule(decisionCreatorTimer, timerDelay * 1000L);
        } catch (Exception ex) {
            LOGGER.error("Error while create decision creator timer. Exception message: ", ex);
            System.exit(1);
        }
    }

    private int getDecisionTimeout() {
        Optional<QueueManagerParam> queueManagerParamOptional = jpaQueueManagerParamRepository.findByParamName("DECISION_TIMEOUT_SEC");
        return queueManagerParamOptional
                .map(queueManagerParam -> Integer.parseInt(queueManagerParam.getParamValue()))
                .orElse(-1);
    }
}
