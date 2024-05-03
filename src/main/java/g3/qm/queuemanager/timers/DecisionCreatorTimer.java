package g3.qm.queuemanager.timers;

import g3.qm.queuemanager.repositories.DecisionInfoRepository;
import g3.qm.queuemanager.services.DecisionInfoService;
import g3.qm.queuemanager.services.TimerCreatorService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.TimerTask;

public class DecisionCreatorTimer extends TimerTask {
    @Autowired
    private TimerCreatorService timerCreatorService;
    @Autowired
    private DecisionInfoService decisionInfoService;

    @Override
    public void run() {
        System.out.println("Create new Decision: " + new Date());
        System.out.println(decisionInfoService.getDeviceList());
        timerCreatorService.createDecisionCreatorTimer();
    }
}
