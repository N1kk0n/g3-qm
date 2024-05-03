package g3.qm.queuemanager.services;

import g3.qm.queuemanager.repositories.DecisionInfoRepository;
import g3.qm.queuemanager.responces.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DecisionInfoService {
    @Autowired
    private DecisionInfoRepository decisionInfoRepository;

    public List<Device> getDeviceList() {
        return decisionInfoRepository.getDeviceList();
    }
}
