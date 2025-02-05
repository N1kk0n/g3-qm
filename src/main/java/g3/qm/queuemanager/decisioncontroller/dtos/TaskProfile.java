package g3.qm.queuemanager.decisioncontroller.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskProfile {
    private long task_id;
    private int program_id;
    private String program_name;
    private String profile_name;
    private long profile_priority;
    private String profile_status;
    private String device_type;
    private int device_count;
    private boolean profile_static;
    private String device_name;

    @Override
    public String toString() {
        return "TaskItem{" +
                "task_id=" + task_id +
                ", program_name=" + program_name +
                ", profile_name='" + profile_name + '\'' +
                ", profile_priority=" + profile_priority +
                ", profile_status='" + profile_status + '\'' +
                ", device_type='" + device_type + '\'' +
                ", device_count=" + device_count +
                ", profile_static=" + profile_static +
                ", device_name=" + device_name +
                '}';
    }
}
