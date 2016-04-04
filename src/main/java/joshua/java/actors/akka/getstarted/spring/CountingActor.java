package joshua.java.actors.akka.getstarted.spring;

import akka.actor.UntypedActor;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * An actor that can count using an injected CountingService.
 *
 * @author Joshua.Jiang on 2016/3/27.
 */
@Named("CountingActor")
@Scope("prototype")
public class CountingActor extends UntypedActor {

    public static class Count {}
    public static class Get {}

    private final CountingService countingService;
    private int count = 0;

    @Inject
    public CountingActor(@Named("CountingService")CountingService countingService) {
        this.countingService = countingService;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Count) {
            count  = countingService.increment(count);
        } else if (message instanceof  Get) {
            getSender().tell(count, getSelf());
        } else {
            unhandled(message);
        }
    }
}
