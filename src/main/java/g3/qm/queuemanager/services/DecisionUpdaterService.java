package g3.qm.queuemanager.services;

import g3.qm.queuemanager.dtos.DecisionItem;
import g3.qm.queuemanager.repositories.DecisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
