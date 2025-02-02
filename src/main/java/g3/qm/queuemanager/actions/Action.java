package g3.qm.queuemanager.actions;

import g3.qm.queuemanager.dtos.kafka.Content;

public interface Action {
    int execute(Content content);
}
