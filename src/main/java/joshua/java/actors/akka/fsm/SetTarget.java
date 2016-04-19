package joshua.java.actors.akka.fsm;

import akka.actor.ActorRef;

/**
 * @author Joshua.Jiang on 2016/4/10.
 */
public final class SetTarget {

    final ActorRef ref;

    public SetTarget(ActorRef ref) {
        this.ref = ref;
    }
}
