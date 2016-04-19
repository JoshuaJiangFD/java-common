package joshua.java.actors.akka.getstarted.spring;

import akka.actor.ActorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static joshua.java.actors.akka.getstarted.spring.SpringExtension.SpringExtProvider;

/**
 * The application configuration.
 *
 * @author Joshua.Jiang on 2016/3/26.
 */
@Configuration
@ComponentScan(value = {"joshua.java.actors.akka.getstarted.spring"})
public class AppConfiguration {

    /**
     * The application context is needed to initialize the Akka Spring extension.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * ActorSystem singleton for this application.
     * @return
     */
    @Bean
    public ActorSystem actorSystem() {
        ActorSystem system = ActorSystem.create("AkkaJavaSpring");
        SpringExtProvider.get(system).intialize(applicationContext);
        return system;
    }
}
