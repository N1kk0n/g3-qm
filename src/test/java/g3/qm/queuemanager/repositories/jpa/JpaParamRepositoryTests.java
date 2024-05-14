package g3.qm.queuemanager.repositories.jpa;

import g3.qm.queuemanager.entites.QueueManagerParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class JpaParamRepositoryTests {
    @Autowired
    private JpaParamRepository jpaParamRepository;

    @Test
    public void ParamRepository_findByParameterName_ReturnsValue() {
        QueueManagerParam managerParam = new QueueManagerParam();
        managerParam.setParamName("name");
        managerParam.setParamValue("value");

        jpaParamRepository.save(managerParam);

        Optional<QueueManagerParam> namedParamOptional = jpaParamRepository.findByParamName("name");
        Assertions.assertTrue(namedParamOptional.isPresent());
        Assertions.assertEquals("value", namedParamOptional.get().getParamValue());
    }
}
