package g3.qm.queuemanager.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Device {
    private int device_id;
    private String device_name;
    private String device_type;
    private String device_status;
    private long task_id;
    private long task_priority;

    @Override
    public String toString() {
        return "Device{" +
                "device_id=" + device_id +
                ", device_name='" + device_name + '\'' +
                ", device_type='" + device_type + '\'' +
                ", device_status='" + device_status + '\'' +
                ", task_id=" + task_id +
                ", task_priority=" + task_priority +
                '}';
    }
}
