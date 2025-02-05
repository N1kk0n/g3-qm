package g3.qm.queuemanager.init.repositories.state;

import g3.qm.queuemanager.decisioncontroller.dtos.QueueManagerParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ParamStateRepository {
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public ParamStateRepository(@Qualifier("stateJdbcTemplate") NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    public List<QueueManagerParam> getParams() {
        String sql = """
            select param_name, param_value
            from state_schema.queue_manager_param
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
