package g3.qm.queuemanager.decisioncontroller.services;

import g3.qm.queuemanager.decisioncontroller.dtos.DecisionItem;
import g3.qm.queuemanager.decisioncontroller.dtos.Device;
import g3.qm.queuemanager.routing.dtos.kafka.Content;
import g3.qm.queuemanager.init.repositories.cache.ParamCacheRepository;
import g3.qm.queuemanager.routing.services.RouterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DecisionUpdaterService {

    private final ParamCacheRepository paramCacheRepository;
    private final RouterService routerService;

    public DecisionUpdaterService(ParamCacheRepository paramCacheRepository, RouterService routerService) {
        this.paramCacheRepository = paramCacheRepository;
        this.routerService = routerService;
    }

    public boolean decisionUpdateFlag() {
        String s = paramCacheRepository.getParamValue("UPDATE_DECISION_FLAG");
        return Boolean.parseBoolean(s);
    }

    public void setDecisionUpdateFlagOn() {
        paramCacheRepository.setParam("UPDATE_DECISION_FLAG", "true");
    }

    public void setDecisionUpdateFlagOff() {
        paramCacheRepository.setParam("UPDATE_DECISION_FLAG", "false");
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
