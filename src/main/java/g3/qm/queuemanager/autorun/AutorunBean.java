package g3.qm.queuemanager.autorun;

import g3.qm.queuemanager.services.TimerCreatorService;
import g3.qm.queuemanager.services.UpdateParametersService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AutorunBean {
    @Autowired
    private Environment environment;
    @Autowired
    private TimerCreatorService timerCreatorService;
    @Autowired
    private UpdateParametersService updateParametersService;

    @PostConstruct
    public void init() {
        if (!isParamsCorrect()) {
            System.exit(-1);
        }
        updateParametersService.initQueueManagerParams();
        timerCreatorService.createDecisionCreatorTimer();
    }

    private boolean isParamsCorrect() {
        final String HOST_PORT = "host.port";
        final String ROOT_CERT = "root.cert";
        final String USER_CERT = "user.cert";
        final String USER_KEY = "user.key.pk8";

        String message = """
                Example: "java -jar g3-qm.jar --host.port=<host and port of database>\\
                --root.cert=<path to root certificate file>\\
                 --user.cert=<path to user certificate file>\\
                 --user.key.pk8=<path to key pk8 file>
                """;
        return isParamCorrect(HOST_PORT, message) &&
                isParamCorrect(ROOT_CERT, message) &&
                isParamCorrect(USER_CERT, message) &&
                isParamCorrect(USER_KEY, message);
    }

    private boolean isParamCorrect(String parameter, String message) {
        if (environment.getProperty(parameter) == null || Objects.requireNonNull(environment.getProperty(parameter)).isEmpty()) {
            System.err.println("Check input parameter: " + parameter + ". Parameter is null or empty");
            System.err.println(message);
            return false;
        }
        return true;
    }
}

