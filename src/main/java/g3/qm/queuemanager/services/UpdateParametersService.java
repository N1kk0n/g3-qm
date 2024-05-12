package g3.qm.queuemanager.services;

import g3.qm.queuemanager.dtos.QueueManagerParam;
import g3.qm.queuemanager.repositories.jdbc.JdbcQueueManagerParamRepository;
import g3.qm.queuemanager.repositories.jpa.JpaQueueManagerParamRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateParametersService {
    @Autowired
    private JdbcQueueManagerParamRepository jdbcParamRepository;
    @Autowired
    private JpaQueueManagerParamRepository jpaParamRepository;

    private final Logger LOGGER = LogManager.getLogger(UpdateParametersService.class);

    public void initQueueManagerParams() {
        try {
            for (QueueManagerParam queueManagerParam : jdbcParamRepository.getParams()) {
                g3.qm.queuemanager.entites.QueueManagerParam param = new g3.qm.queuemanager.entites.QueueManagerParam();
                param.setParamName(queueManagerParam.getParamName());
                param.setParamValue(queueManagerParam.getParamValue());
                jpaParamRepository.save(param);
            }
        } catch (Exception ex) {
            LOGGER.error("Error while init queue params. Exception message: ", ex);
            System.exit(1);
        }
    }
}
