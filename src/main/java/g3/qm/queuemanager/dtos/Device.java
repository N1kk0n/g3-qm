package g3.qm.queuemanager.dtos;

public class Device {
    private int device_id;
    private String device_name;
    private String device_type;
    private String device_status;
    private String manager_name;
    private long task_id;
    private long task_priority;

    public Device() {
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_status() {
        return device_status;
    }

    public void setDevice_status(String device_status) {
        this.device_status = device_status;
    }

    public String getManager_name() {
        return manager_name;
    }

    public void setManager_name(String manager_name) {
        this.manager_name = manager_name;
    }

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }

    public long getTask_priority() {
        return task_priority;
    }

    public void setTask_priority(long task_priority) {
        this.task_priority = task_priority;
    }

    @Override
    public String toString() {
        return "Device{" +
                "device_id=" + device_id +
                ", device_name='" + device_name + '\'' +
                ", device_type='" + device_type + '\'' +
                ", device_status='" + device_status + '\'' +
                ", manager_name='" + manager_name + '\'' +
                ", task_id=" + task_id +
                ", task_priority=" + task_priority +
                '}';
    }
}
