package joshua.java.actors.akka.getstarted.spring;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.context.ApplicationContext;

/**
 * An Akka Extension to provide access to Spring managed Actor beans.
 * An extension can be thought of as a per actor system singleton.
 *
 * @author Joshua.Jiang on 2016/3/26.
 */
public class SpringExtension extends AbstractExtensionId<SpringExtension.SpringExt>{

    /**
     * The singleton identifier used to access the SpringExtension.
     */
    public static SpringExtension SpringExtProvider = new SpringExtension();

    @Override
    public SpringExt createExtension(ExtendedActorSystem extendedActorSystem) {
        return new SpringExt();
    }

    public static class SpringExt implements Extension {
        private volatile ApplicationContext applicationContext;

        public void intialize(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        public Props props(String actorBeanName) {
            return Props.create(SpringActorProvider.class, applicationContext, actorBeanName);
        }
    }
}
