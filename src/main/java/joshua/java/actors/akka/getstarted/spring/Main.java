package joshua.java.actors.akka.getstarted.spring;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

/**
 * The main entrance for Spring integration example.
 *
 * @author Joshua.Jiang on 2016/3/27.
 */
public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan("joshua.java.actors.akka.getstarted.spring");
        ctx.refresh();

        // create the ActorSystem
        ActorSystem actorSystem = ctx.getBean(ActorSystem.class);
        // use Spring Extension to create props for a named actor bean
        ActorRef counter = actorSystem.actorOf(SpringExtension.SpringExtProvider.get(actorSystem).props("CountingActor"), "counter");

        counter.tell(new CountingActor.Count(), null);
        counter.tell(new CountingActor.Count(), null);
        counter.tell(new CountingActor.Count(), null);

        FiniteDuration duration = FiniteDuration.create(3, TimeUnit.SECONDS);
        Future<Object> result = Patterns.ask(counter, new CountingActor.Get(), Timeout.durationToTimeout(duration));
        try {
            System.out.println("Got back" + Await.result(result, duration));
        } catch (Exception e){
            System.err.println("Failed getting result:" +e.getMessage());
        } finally {
            actorSystem.shutdown();
            actorSystem.awaitTermination();
        }
    }
}
