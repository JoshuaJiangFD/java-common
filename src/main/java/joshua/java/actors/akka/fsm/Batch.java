package joshua.java.actors.akka.fsm;

import java.util.List;

/**
 * @author Joshua.Jiang on 2016/4/10.
 */
public final class Batch {
    final List<Object> objects;

    public Batch(List<Object> objects) {
        this.objects = objects;
    }
}
