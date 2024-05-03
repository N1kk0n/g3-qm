package g3.qm.queuemanager.repositories;

import g3.qm.queuemanager.responces.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DecisionInfoRepository {
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public DecisionInfoRepository(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    public List<Device> getDeviceList() {
        String sql = """
            select device_id, device_name, device_type, dds.constant_value as device_status, manager_name, task_id, task_priority
            from device d left join resource_manager rm on d.manager_id = rm.manager_id
                          left join dict_device_status dds on d.device_status = dds.constant_status
            where task_id not in (select distinct task_id
                                  from task_profile tp left join dict_task_profile_status dtps on dtps.constant_status = tp.profile_status
                                  where dtps.constant_value in ('DEPLOY_IN_PROGRESS'))
              and device_online = true
              and rm.manager_online = true
            order by task_priority
        """;
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource();

        return template.query(sql, sqlParameterSource, (resultSet, rowNum) -> {
            Device device = new Device();
            device.setDevice_id(resultSet.getLong("DEVICE_ID"));
            device.setDevice_name(resultSet.getString("DEVICE_NAME"));
            device.setDevice_type(resultSet.getString("DEVICE_TYPE"));
            device.setDevice_status(resultSet.getString("DEVICE_STATUS"));
            device.setManager_name(resultSet.getString("MANAGER_NAME"));
            device.setTask_id(resultSet.getLong("TASK_ID"));
            device.setTask_priority(resultSet.getLong("TASK_PRIORITY"));
            return device;
        });
    }
}

