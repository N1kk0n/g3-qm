package g3.qm.queuemanager.dtos;

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

    public TaskProfile() {
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

    public String getProgram_name() {
        return program_name;
    }

    public void setProgram_name(String program_name) {
        this.program_name = program_name;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public long getProfile_priority() {
        return profile_priority;
    }

    public void setProfile_priority(long profile_priority) {
        this.profile_priority = profile_priority;
    }

    public String getProfile_status() {
        return profile_status;
    }

    public void setProfile_status(String profile_status) {
        this.profile_status = profile_status;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public int getDevice_count() {
        return device_count;
    }

    public void setDevice_count(int device_count) {
        this.device_count = device_count;
    }

    public boolean isProfile_static() {
        return profile_static;
    }

    public void setProfile_static(boolean profile_static) {
        this.profile_static = profile_static;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

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
