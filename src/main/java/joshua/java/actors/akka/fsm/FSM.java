package joshua.java.actors.akka.fsm;

import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * @author Joshua.Jiang on 2016/4/10.
 */
public class FSM extends FSMBase {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    // message to drain the queue and send all the messages into target actor
    public final Object flush = new Object();

    @Override
    public void onReceive(Object o) {
        // 这里的逻辑分为两层，状态检查和消息检查，先检查状态称为state-based, 否则为event-centric.
        // 一般策略是去取决于state和event的维度，如果event类型很少，但是内部需要维护的状态较多，会选择event-centric.
        // i.e. 先判断event类型，再判断内部状态
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
            // send message to target actor
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
