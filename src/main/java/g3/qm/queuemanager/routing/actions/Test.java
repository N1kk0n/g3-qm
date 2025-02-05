package g3.qm.queuemanager.routing.actions;

import g3.qm.queuemanager.routing.dtos.kafka.Content;

public class Test implements Action {
    @Override
    public int execute(Content content) {
        return 2;
    }
}
