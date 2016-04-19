package joshua.java.actors.akka.getstarted.spring;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import org.springframework.context.ApplicationContext;

/**
 * An actor provider to delegate actor creation to Spring framework.
 *
 * @author Joshua.Jiang on 2016/3/26.
 */
public class SpringActorProvider implements IndirectActorProducer{

    final ApplicationContext applicationContext;
    final String actorBeanName;

    public SpringActorProvider(ApplicationContext applicationContext, String actorBeanName) {
        this.applicationContext = applicationContext;
        this.actorBeanName = actorBeanName;
    }

    /**
     * The factory method called every time when creating new actor instance.
     *
     * @return
     */
    @Override
    public Actor produce() {
        // delegate creating Actor instance to Spring.
        return (Actor)applicationContext.getBean(actorBeanName);
    }

    /**
     * Return the type of Actor that will be created.
     *
     * @return
     */
    @Override
    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>)applicationContext.getType(actorBeanName);
    }
}
