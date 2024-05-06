package g3.qm.queuemanager.services;

import g3.qm.queuemanager.dtos.TaskItem;
import g3.qm.queuemanager.repositories.DecisionInfoRepository;
import g3.qm.queuemanager.dtos.Device;
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

    public List<TaskItem> getTaskProfileList() {
        return decisionInfoRepository.getTaskProfileList();
    }
}
