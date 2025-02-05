package g3.qm.queuemanager.init.services;

import g3.qm.queuemanager.decisioncontroller.dtos.QueueManagerParam;
import g3.qm.queuemanager.init.repositories.cache.ParamCacheRepository;
import g3.qm.queuemanager.init.repositories.state.ParamStateRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateParametersService {
    private final ParamStateRepository paramStateRepository;
    private final ParamCacheRepository paramCacheRepository;
    private final Logger LOGGER = LogManager.getLogger(UpdateParametersService.class);

    public UpdateParametersService(ParamStateRepository paramStateRepository, ParamCacheRepository paramCacheRepository) {
        this.paramStateRepository = paramStateRepository;
        this.paramCacheRepository = paramCacheRepository;
    }

    private List<QueueManagerParam> getRemoteParams() {
        return paramStateRepository.getParams();
    }

    private void setLocalParam(QueueManagerParam remoteParam) {
        paramCacheRepository.addParam(remoteParam.getParamName(), remoteParam.getParamValue());
    }

    public void updateSelfParams() {
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
