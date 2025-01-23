package g3.qm.queuemanager.services;

import g3.qm.queuemanager.dtos.QueueManagerParam;
import g3.qm.queuemanager.repositories.inner.InnerParamRepository;
import g3.qm.queuemanager.repositories.state.StateParamRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateParametersService {

    private final StateParamRepository stateParamRepository;
    private final InnerParamRepository innerParamRepository;
    private final Logger LOGGER = LogManager.getLogger(UpdateParametersService.class);

    public UpdateParametersService(StateParamRepository stateParamRepository, InnerParamRepository innerParamRepository) {
        this.stateParamRepository = stateParamRepository;
        this.innerParamRepository = innerParamRepository;
    }

    private List<QueueManagerParam> getRemoteParams() {
        return stateParamRepository.getParams();
    }

    private void setLocalParam(QueueManagerParam remoteParam) {
        innerParamRepository.addParam(remoteParam.getParamName(), remoteParam.getParamValue());
    }

    public void initSelfParams() {
        try {
            for (QueueManagerParam managerParam : getRemoteParams()) {
                setLocalParam(managerParam);
            }
        } catch (Exception ex) {
            LOGGER.error("Error while init queue manager params. Exception message: ", ex);
            System.exit(1);
        }
    }
}
