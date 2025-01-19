package g3.qm.queuemanager.services;

import g3.qm.queuemanager.dtos.DecisionItem;
import g3.qm.queuemanager.repositories.inner.InnerParamRepository;
import g3.qm.queuemanager.repositories.state.DecisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DecisionUpdaterService {

    private final DecisionRepository decisionRepository;
    private final InnerParamRepository innerParamRepository;

    public DecisionUpdaterService(DecisionRepository decisionRepository, InnerParamRepository innerParamRepository) {
        this.decisionRepository = decisionRepository;
        this.innerParamRepository = innerParamRepository;
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
        decisionRepository.deleteDecision();
        decisionRepository.insertDecision(decision);
    }
}
