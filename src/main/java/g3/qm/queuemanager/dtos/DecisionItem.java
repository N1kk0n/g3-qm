package g3.qm.queuemanager.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DecisionItem {
    private long task_id;
    private int program_id;
    private String device_name;

    public DecisionItem(long task_id, int program_id, String device_name) {
        this.task_id = task_id;
        this.program_id = program_id;
        this.device_name = device_name;
    }
}
