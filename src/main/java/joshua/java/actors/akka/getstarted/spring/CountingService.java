package joshua.java.actors.akka.getstarted.spring;

import javax.inject.Named;

/**
 * A simple service that can increment a number.
 * A example for dependency injection into {@link joshua.java.actors.akka.getstarted.spring.CountingActor}
 *
 * @author Joshua.Jiang on 2016/3/27.
 */
@Named("CountingService")
public class CountingService {

    public int increment(int count) {
        return count+1;
    }
}
