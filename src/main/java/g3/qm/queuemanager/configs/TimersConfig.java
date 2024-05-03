package g3.qm.queuemanager.configs;

import g3.qm.queuemanager.timers.DecisionCreatorTimer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class TimersConfig {
    @Bean
    @Scope("prototype")
    DecisionCreatorTimer checkDeviceTimer() {
        return new DecisionCreatorTimer();
    }
}
