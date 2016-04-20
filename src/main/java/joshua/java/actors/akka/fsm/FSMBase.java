package joshua.java.actors.akka.fsm;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.ArrayList;
import java.util.List;

/**
 * The base class for Finite State Machine Actor.
 *
 * 所有的可修改的状态字段应该放在一起，并提供一组定义良好的接口来操作这些状态字段。<br>
 * 并且所有的修改方法都是protected修饰符，仅供子类访问。
 *
 * FSMBase的功能是同一存放了所有的状态和修改方法。
 * 作为Actor的功能是收集所有的Message到Queue中，
 *
 * @author Joshua.Jiang on 2016/4/10.
 */
public abstract class FSMBase extends UntypedActor {

    /**
     * This is the mutable state of this state machine.
     */
    protected enum State {
        IDLE, ACTIVE
    }

    private State state = State.IDLE;
    private ActorRef target;
    private List<Object> queue;

    /*
     * Then come all the mutator methods:
     */
    protected void init(ActorRef target) {
        this.target = target;
        queue = new ArrayList<Object>();
    }

    protected void setState(State s) {
        if (state != s) {
            transition(state, s);
            state = s;
        }
    }

    protected void enqueue(Object o) {
        if (queue != null)
            queue.add(o);
    }

    protected List<Object> drainQueue() {
        final List<Object> q = queue;
        if (q == null)
            throw new IllegalStateException("drainQueue(): not yet initialized");
        queue = new ArrayList<Object>();
        return q;
    }

    /*
     * Here are the interrogation methods:
     */
    protected boolean isInitialized() {
        return target != null;
    }

    protected State getState() {
        return state;
    }

    protected ActorRef getTarget() {
        if (target == null)
            throw new IllegalStateException("getTarget(): not yet initialized");
        return target;
    }

    /*
     * And finally the callbacks (only one in this example: react to state change)
     */
    abstract protected void transition(State old, State next);
}
