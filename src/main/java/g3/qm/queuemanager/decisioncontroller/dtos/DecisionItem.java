package g3.qm.queuemanager.decisioncontroller.dtos;

import g3.qm.queuemanager.decisioncontroller.dtos.Device;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DecisionItem {
    private long task_id;
    private int program_id;
    private List<Device> device_list;

    public DecisionItem(long task_id, int program_id, List<Device> device_list) {
        this.task_id = task_id;
        this.program_id = program_id;
        this.device_list = new LinkedList<>(device_list);
    }
}
