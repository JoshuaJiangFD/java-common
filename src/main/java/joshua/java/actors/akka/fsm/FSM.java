package joshua.java.actors.akka.fsm;

import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * @author Joshua.Jiang on 2016/4/10.
 */
public class FSM extends FSMBase {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public final Object flush = new Object();

    @Override
    public void onReceive(Object o) {
        if (getState() == State.IDLE) {
            if (o instanceof SetTarget) {
                init(((SetTarget) o).ref);
            } else {
                whenUnhandled(o);
            }

        } else if (getState() == State.ACTIVE) {
            if (o == flush) {
                setState(State.IDLE);
            } else {
                whenUnhandled(o);
            }
        }
    }

    @Override
    public void transition(State old, State next) {
        if (old == State.ACTIVE) {
            getTarget().tell(new Batch(drainQueue()), getSelf());
        }
    }

    private void whenUnhandled(Object o) {
        if (o instanceof Queue && isInitialized()) {
            enqueue(((Queue) o).o);
            setState(State.ACTIVE);
        } else {
            log.warning("received unknown message {} in state {}", o, getState());
        }
    }
}
