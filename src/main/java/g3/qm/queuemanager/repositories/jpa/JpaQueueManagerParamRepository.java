package g3.qm.queuemanager.repositories.jpa;

import g3.qm.queuemanager.entites.QueueManagerParam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaQueueManagerParamRepository extends JpaRepository<QueueManagerParam, Integer> {
    Optional<QueueManagerParam> findByParamName(String paramName);
}
