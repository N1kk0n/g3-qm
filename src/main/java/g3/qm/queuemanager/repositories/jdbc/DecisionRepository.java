package g3.qm.queuemanager.repositories.jdbc;

import g3.qm.queuemanager.dtos.DecisionItem;
import g3.qm.queuemanager.dtos.Device;
import g3.qm.queuemanager.dtos.TaskItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DecisionRepository {
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public DecisionRepository(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    public List<Device> getDeviceList() {
        String sql = """
            select device_id, device_name, device_type, dds.constant_value as device_status, manager_name, task_id, task_priority
            from device d left join resource_manager rm on d.manager_id = rm.manager_id
                          left join dict_device_status dds on d.device_status = dds.constant_status
            where task_id not in (select distinct task_id
                                  from task_profile tp left join dict_task_profile_status dtps on dtps.constant_status = tp.profile_status
                                  where dtps.constant_value in ('DOWNLOAD_IN_PROGRESS', 'DEPLOY_IN_PROGRESS', 'IN_WORK_PROTECTED', 'COLLECT_IN_PROGRESS', 'UPLOAD_IN_PROGRESS'))
              and device_online = true
              and rm.manager_online = true
            order by task_priority
        """;
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource();

        return template.query(sql, sqlParameterSource, (resultSet, rowNum) -> {
            Device device = new Device();
            device.setDevice_id(resultSet.getInt("DEVICE_ID"));
            device.setDevice_name(resultSet.getString("DEVICE_NAME"));
            device.setDevice_type(resultSet.getString("DEVICE_TYPE"));
            device.setDevice_status(resultSet.getString("DEVICE_STATUS"));
            device.setManager_name(resultSet.getString("MANAGER_NAME"));
            device.setTask_id(resultSet.getLong("TASK_ID"));
            device.setTask_priority(resultSet.getLong("TASK_PRIORITY"));
            return device;
        });
    }

    public List<TaskItem> getTaskProfileList(int queuePage) {
        final int pageLimit = 1000;
        int pageOffset = 0;
        if (queuePage > 1) {
            pageOffset = (queuePage - 1) * pageLimit;
        }

        String sql = """
            select task_limit.task_id,
                   pr.program_id,
                   pr.program_name,
                   profile_name,
                   profile_priority,
                   dtps.constant_value as profile_status,
                   p.device_type, device_count,
                   profile_static, device_name
            from (
                select distinct task_id, max(profile_priority)
                from task_profile
                where task_id not in (select distinct task_id
                                      from task_profile tp left join dict_task_profile_status dtps on dtps.constant_status = tp.profile_status
                                      where dtps.constant_value in ('DOWNLOAD_IN_PROGRESS', 'DEPLOY_IN_PROGRESS', 'IN_WORK_PROTECTED', 'COLLECT_IN_PROGRESS', 'UPLOAD_IN_PROGRESS'))
                group by task_id
                order by max(profile_priority) desc
                limit :pageLimit offset :pageOffset
                ) task_limit left join task t on task_limit.task_id = t.task_id
                             left join task_profile tp on t.task_id = tp.task_id
                             left join profile p on p.profile_id = tp.profile_id
                             left join program pr on pr.program_id = t.program_id
                             left join device d on p.device_id = d.device_id
                             left join dict_task_profile_status dtps on tp.profile_status = dtps.constant_status
            where profile_active = true
            order by profile_priority desc, task_id desc
        """;
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("pageLimit", pageLimit)
                .addValue("pageOffset", pageOffset);

        return template.query(sql, sqlParameterSource, (resultSet, rowNum) -> {
            TaskItem taskItem = new TaskItem();
            taskItem.setTask_id(resultSet.getLong("TASK_ID"));
            taskItem.setProgram_id(resultSet.getInt("PROGRAM_ID"));
            taskItem.setProgram_name(resultSet.getString("PROGRAM_NAME"));
            taskItem.setProfile_name(resultSet.getString("PROFILE_NAME"));
            taskItem.setProfile_priority(resultSet.getLong("PROFILE_PRIORITY"));
            taskItem.setProfile_status(resultSet.getString("PROFILE_STATUS"));
            taskItem.setDevice_type(resultSet.getString("DEVICE_TYPE"));
            taskItem.setDevice_count(resultSet.getInt("DEVICE_COUNT"));
            taskItem.setProfile_static(resultSet.getBoolean("PROFILE_STATIC"));
            taskItem.setDevice_name(resultSet.getString("DEVICE_NAME"));
            return taskItem;
        });
    }

    public void insertDecision(List<DecisionItem> decision) {
        String insertSql = """
            insert into decision(task_id, program_id, device_name, manager_name)
            values (:task_id, :program_id, :device_name, :manager_name)
        """;
        template.batchUpdate(insertSql, SqlParameterSourceUtils.createBatch(decision));
    }

    public void deleteDecision() {
        String deleteSql = """
            delete from decision
        """;
        template.update(deleteSql, new MapSqlParameterSource());
    }
}

