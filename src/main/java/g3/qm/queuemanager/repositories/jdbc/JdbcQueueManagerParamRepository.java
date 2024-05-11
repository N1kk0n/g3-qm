package g3.qm.queuemanager.repositories.jdbc;

import g3.qm.queuemanager.dtos.QueueManagerParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcQueueManagerParamRepository {
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public JdbcQueueManagerParamRepository(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    public List<QueueManagerParam> getParams() {
        String sql = """
            select param_name, param_value
            from queue_manager_param
        """;
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource();

        return template.query(sql, sqlParameterSource, (resultSet, rowNum) -> {
            QueueManagerParam param = new QueueManagerParam();
            param.setParamName(resultSet.getString("PARAM_NAME"));
            param.setParamValue(resultSet.getString("PARAM_VALUE"));
            return param;
        });
    }
}
