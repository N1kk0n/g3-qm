package g3.qm.queuemanager.services;

import g3.qm.queuemanager.dtos.QueueManagerParam;
import g3.qm.queuemanager.repositories.jdbc.JdbcParamRepository;
import g3.qm.queuemanager.repositories.jpa.JpaParamRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateParametersService {
    @Autowired
    private JdbcParamRepository jdbcParamRepository;
    @Autowired
    private JpaParamRepository jpaParamRepository;

    private final Logger LOGGER = LogManager.getLogger(UpdateParametersService.class);

    private List<QueueManagerParam> getRemoteParams() {
        return jdbcParamRepository.getParams();
    }

    private void setLocalParam(QueueManagerParam remoteParam) {
        g3.qm.queuemanager.entites.QueueManagerParam param = new g3.qm.queuemanager.entites.QueueManagerParam();
        param.setParamName(remoteParam.getParamName());
        param.setParamValue(remoteParam.getParamValue());
        jpaParamRepository.save(param);
    }

    public void initQueueManagerParams() {
        try {
            for (QueueManagerParam managerParam : getRemoteParams()) {
                setLocalParam(managerParam);
            }
        } catch (Exception ex) {
            LOGGER.error("Error while init queue params. Exception message: ", ex);
            System.exit(1);
        }
    }
}
