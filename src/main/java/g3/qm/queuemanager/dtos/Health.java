package g3.qm.queuemanager.dtos;

public class Health {
    private String status;

    public Health() {
    }

    public Health(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
