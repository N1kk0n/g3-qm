package g3.qm.queuemanager.services;

import g3.qm.queuemanager.dtos.DecisionItem;
import g3.qm.queuemanager.dtos.Device;
import g3.qm.queuemanager.dtos.kafka.Content;
import g3.qm.queuemanager.repositories.inner.InnerParamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DecisionUpdaterService {

    private final InnerParamRepository innerParamRepository;
    private final RouterService routerService;

    public DecisionUpdaterService(InnerParamRepository innerParamRepository, RouterService routerService) {
        this.innerParamRepository = innerParamRepository;
        this.routerService = routerService;
    }

    public boolean decisionUpdateFlag() {
        String s = innerParamRepository.getParamValue("UPDATE_DECISION_FLAG");
        return Boolean.parseBoolean(s);
    }

    public void setDecisionUpdateFlagOn() {
        innerParamRepository.setParam("UPDATE_DECISION_FLAG", "true");
    }

    public void setDecisionUpdateFlagOff() {
        innerParamRepository.setParam("UPDATE_DECISION_FLAG", "false");
    }

    public void updateDecision(List<DecisionItem> decision) {
        for (DecisionItem item : decision) {
            List<String> deviceNameList = item.getDevice_list()
                    .stream()
                    .map(Device::getDevice_name)
                    .toList();
            Content params = new Content();
            params.setDevice_name_list(deviceNameList);
            params.setTask_id(item.getTask_id());
            params.setProgram_id(item.getProgram_id());
            routerService.createRoute("CREATE-SESSION", params);
        }
    }
}
