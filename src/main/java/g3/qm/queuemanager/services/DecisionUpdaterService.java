package g3.qm.queuemanager.services;

import g3.qm.queuemanager.dtos.DecisionItem;
import g3.qm.queuemanager.repositories.jdbc.DecisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DecisionUpdaterService {
    @Autowired
    private DecisionRepository decisionRepository;

    public void updateDecision(List<DecisionItem> decision) {
        decisionRepository.deleteDecision();
        decisionRepository.insertDecision(decision);
    }
}
