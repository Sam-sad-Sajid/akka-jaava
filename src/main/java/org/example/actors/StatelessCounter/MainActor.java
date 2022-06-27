package org.example.actors.StatelessCounter;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;

import java.util.List;
import java.util.stream.IntStream;

public class MainActor {
    public static void main(String[] args) {
        ActorSystem.create(MainActor.setUpBehavior(), "MainActor");
    }

    private static Behavior<NotUsed> setUpBehavior() {

        return Behaviors.setup(context -> new MainActor(context).behavior());
    }

    private final ActorContext<NotUsed> context;

    private MainActor(ActorContext<NotUsed> context) {
        this.context = context;
    }

    private Behavior<NotUsed> behavior() {

        var counterActor = this.context.spawn(CounterActor.setUpBehavior(), "Counter");

        IntStream.range(1, 10).forEach(i -> counterActor.tell(CounterActor.IncrementCounter.INCREMENT));

        counterActor.tell(CounterActor.PrintCounterValue.PRINT);

        IntStream.range(1, 10).forEach(i -> counterActor.tell(CounterActor.DecrementCounter.DECREMENT));

        counterActor.tell(CounterActor.PrintCounterValue.PRINT);

        return Behaviors.empty();
    }
}
