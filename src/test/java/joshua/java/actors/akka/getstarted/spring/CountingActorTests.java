package joshua.java.actors.akka.getstarted.spring;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static joshua.java.actors.akka.getstarted.spring.CountingActor.*;
import static joshua.java.actors.akka.getstarted.spring.SpringExtension.*;
import static org.junit.Assert.assertEquals;


/**
 * @author Joshua.Jiang on 2016/3/27.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
    classes = {AppConfiguration.class})
public class CountingActorTests {

    @Autowired
    private ActorSystem actorSystem;

    @Test
    public void testCountingActor() throws Exception {
        ActorRef counter = actorSystem.actorOf(SpringExtProvider.get(actorSystem).props("CountingActor"),"counter");
        counter.tell(new Count(), null);
        counter.tell(new Count(), null);
        counter.tell(new Count(), null);

        FiniteDuration duration = FiniteDuration.create(3, TimeUnit.SECONDS);
        Future<Object> result = Patterns.ask(counter, new Get(), Timeout.durationToTimeout(duration));
        assertEquals(3, Await.result(result, duration));

        actorSystem.shutdown();
        actorSystem.awaitTermination();
    }
}
