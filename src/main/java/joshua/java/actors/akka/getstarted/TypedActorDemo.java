package joshua.java.actors.akka.getstarted;

import akka.actor.*;
import akka.dispatch.Futures;
import akka.japi.Creator;
import akka.japi.Option;
import akka.routing.RoundRobinGroup;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * The demo for TypedActor usage.
 *
 * @author Joshua.Jiang on 2016/4/9.
 */
public class TypedActorDemo {

    ActorSystem system = null;
    Object someReference = null;

    public static void main(String[] args) {
        TypedActorDemo demo = new TypedActorDemo();
        // test creating TypedActor
        demo.createATypedActor();
    }

    public void mustGetTheTypedActorExtension() {
        try {
            // Returns the Typed Actor Extension
            TypedActorExtension extension = TypedActor.get(system);
            // Returns whether the reference is a Typed Actor Proxy or not
            TypedActor.get(system).isTypedActor(someReference);
            // Returns the backing Akka Actor behind an external Typed Actor Proxy
            TypedActor.get(system).getActorRefFor(someReference);

            // Returns the current ActorContext,
            // method only valid within methods of a TypedActor implementation
            ActorContext context = TypedActor.context();

            // Returns the external proxy of the current Typed Actor,
            // method only valid within methods of a TypedActor implementation
            Squarer sq = TypedActor.<Squarer>self();

            // Returns a contextual instance of the Typed Actor Extension
            // this means that if you create other Typed Actors with this,
            // they will become children to the current Typed Actor.
            TypedActor.get(TypedActor.context());

        } catch (Exception e) {
            //don't care
        }
    }

    public void createATypedActor() {
        try {
            Squarer mySquarer =
                    TypedActor.get(system).typedActorOf(
                            new TypedProps<SquarerImpl>(Squarer.class, SquarerImpl.class));
            Squarer otherSquarer =
                    TypedActor.get(system).typedActorOf(
                            new TypedProps<SquarerImpl>(Squarer.class,
                                    new Creator<SquarerImpl>() {
                                        public SquarerImpl create() {
                                            return new SquarerImpl("foo");
                                        }
                                    }),
                            "name-for-actor");
            mySquarer.squareDontCare(10);
            Future<Integer> fSquare = mySquarer.square(10); //A Future[Int]
            Option<Integer> oSquare = mySquarer.squareNowPlease(10); //Option[Int]
            int iSquare = mySquarer.squareNow(10); //Int
            assert (100 == Await.result(fSquare, Duration.create(3, TimeUnit.SECONDS)));
            assert (100 == oSquare.get());
            assert (100 == iSquare);
            TypedActor.get(system).stop(mySquarer);
            TypedActor.get(system).poisonPill(otherSquarer);
        } catch (Exception e) {
            //Ignore
        }
    }

    public void createHierarchies() {
        try {
            Squarer childSquarer = TypedActor.get(TypedActor.context()).
                    typedActorOf(new TypedProps<SquarerImpl>(Squarer.class, SquarerImpl.class));
            //Use "childSquarer" as a Squarer
        } catch (Exception e) {
            //dun care
        }
    }

    public void proxyAndActorRef() {
        try {
            final ActorRef actorRefToRemoteActor = system.deadLetters();
            Squarer typedActor =
                    TypedActor.get(system).
                            typedActorOf(
                                    new TypedProps<Squarer>(Squarer.class),
                                    actorRefToRemoteActor
                            );
            //Use "typedActor" as a FooBar
        } catch (Exception e) {
            //dun care
        }
    }

    public void typedRouterPattern() {
        try {
            // prepare routees
            TypedActorExtension typed = TypedActor.get(system);

            Named named1 =
                    typed.typedActorOf(new TypedProps<Named>(Named.class));

            Named named2 =
                    typed.typedActorOf(new TypedProps<Named>(Named.class));

            List<Named> routees = new ArrayList<Named>();
            routees.add(named1);
            routees.add(named2);

            List<String> routeePaths = new ArrayList<String>();
            routeePaths.add(typed.getActorRefFor(named1).path().toStringWithoutAddress());
            routeePaths.add(typed.getActorRefFor(named2).path().toStringWithoutAddress());

            // prepare untyped router
            ActorRef router = system.actorOf(new RoundRobinGroup(routeePaths).props(), "router");

            // prepare typed proxy, forwarding MethodCall messages to `router`
            Named typedRouter = typed.typedActorOf(new TypedProps<Named>(Named.class), router);

            System.out.println("actor was: " + typedRouter.name()); // name-243
            System.out.println("actor was: " + typedRouter.name()); // name-614
            System.out.println("actor was: " + typedRouter.name()); // name-243
            System.out.println("actor was: " + typedRouter.name()); // name-614

            typed.poisonPill(named1);
            typed.poisonPill(named2);
            typed.poisonPill(typedRouter);

        } catch (Exception e) {
            //dun care
        }
    }
}

interface Squarer {
    // fire-forget
    void squareDontCare(int i);

    // non-blocking send-request-reply
    Future<Integer> square(int i);

    // blocking send-request-reply
    Option<Integer> squareNowPlease(int i);

    // blocking send-request-reply
    int squareNow(int i);
}

class SquarerImpl implements Squarer {
    private String name;

    public SquarerImpl() {
        this.name = "default";
    }

    public SquarerImpl(String name) {
        this.name = name;
    }

    public void squareDontCare(int i) {
        int sq = i * i; //Nobody cares :(
    }

    public Future<Integer> square(int i) {
        return Futures.successful(i * i);
    }

    public Option<Integer> squareNowPlease(int i) {
        return Option.some(i * i);
    }

    public int squareNow(int i) {
        return i * i;
    }
}

interface HasName {
    String name();
}

class Named implements HasName {
    private int id = new Random().nextInt(1024);

    @Override
    public String name() {
        return "name-" + id;
    }
}
