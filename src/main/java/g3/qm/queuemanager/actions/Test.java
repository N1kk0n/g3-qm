package g3.qm.queuemanager.actions;

import g3.qm.queuemanager.dtos.kafka.Content;

public class Test implements Action {
    @Override
    public int execute(Content content) {
        return 2;
    }
}
