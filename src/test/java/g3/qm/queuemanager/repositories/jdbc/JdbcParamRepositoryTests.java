package g3.qm.queuemanager.repositories.jdbc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

public class JdbcParamRepositoryTests {
    @Test
    public void ParamRepository_getParamsList_ReturnsList() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:tables.sql")
                .build();
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        JdbcParamRepository jdbcParamRepository = new JdbcParamRepository(jdbcTemplate);

        Assertions.assertEquals(1, jdbcParamRepository.getParams().size());
        Assertions.assertEquals("30", jdbcParamRepository.getParams().get(0).getParamValue());
    }
}
