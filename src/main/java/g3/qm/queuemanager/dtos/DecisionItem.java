package g3.qm.queuemanager.dtos;

public class DecisionItem {
    private long task_id;
    private String device_name;
    private String manager_name;

    public DecisionItem() {
    }

    public DecisionItem(long task_id, String device_name, String manager_name) {
        this.task_id = task_id;
        this.device_name = device_name;
        this.manager_name = manager_name;
    }

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getManager_name() {
        return manager_name;
    }

    public void setManager_name(String manager_name) {
        this.manager_name = manager_name;
    }
}
