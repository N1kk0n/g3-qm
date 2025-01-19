package g3.qm.queuemanager.dtos;

public class DecisionItem {
    private long task_id;
    private int program_id;
    private String device_name;

    public DecisionItem() {
    }

    public DecisionItem(long task_id, int program_id, String device_name) {
        this.task_id = task_id;
        this.program_id = program_id;
        this.device_name = device_name;
    }

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }

    public int getProgram_id() {
        return program_id;
    }

    public void setProgram_id(int program_id) {
        this.program_id = program_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }
}
