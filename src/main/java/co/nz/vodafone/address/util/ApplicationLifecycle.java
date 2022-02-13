package co.nz.organizationfone.address.util;

import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.InputStream;
import java.util.Properties;

@ApplicationScoped
public class ApplicationLifecycle {

    private final Logger log = LoggerFactory.getLogger(ApplicationLifecycle.class);

    void onStart(@Observes StartupEvent ev) {
        try {
            InputStream confFile = ApplicationLifecycle.class.getClassLoader().getResourceAsStream("git.properties");
            Properties prop = new Properties();
            prop.load(confFile);
            prop.forEach((k, v) -> {
                log.info("GITINFO -> {} : {}", k, v);
            });
        } catch (Exception ex) {
            log.info("GITINFO -> Unable to get git.properties file {} ", ex.getMessage());
        }
    }
}
