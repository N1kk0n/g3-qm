package g3.qm.queuemanager.decisioncontroller.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Device {
    private int device_id;
    private String device_name;
    private String device_type;
    private String device_status;
    private long task_id;
    private long task_priority;
}
