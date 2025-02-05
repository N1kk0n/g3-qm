package g3.qm.queuemanager.routing.actions;

import g3.qm.queuemanager.routing.dtos.kafka.Content;

public interface Action {
    int execute(Content content);
}
