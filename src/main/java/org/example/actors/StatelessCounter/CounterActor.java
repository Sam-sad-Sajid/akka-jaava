package org.example.actors.StatelessCounter;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;

public class CounterActor {

    public interface Command {}

    enum IncrementCounter implements Command {
        INCREMENT
    }

    enum DecrementCounter implements Command {
        DECREMENT
    }

    enum PrintCounterValue implements Command {
        PRINT
    }

    private final ActorContext<Command> context;

    private CounterActor(ActorContext<Command> context) {

        this.context = context;
    }

    public static Behavior<Command> setUpBehavior() {

        return Behaviors.setup(context -> new CounterActor(context).initActor());
    }

    private Behavior<Command> initActor() {

        return Behaviors.receive(Command.class)
                .onMessage(IncrementCounter.class, msg -> incrementCounter(0))
                .onMessage(IncrementCounter.class, msg -> decrementCounter(0))
                .onMessage(IncrementCounter.class, msg -> printCounterValue(0))
                .build();
    }

    private Behavior<Command> incrementCounter(int counter) {
        int newCounter = counter + 1;

        return Behaviors.receive(Command.class)
                .onMessage(IncrementCounter.class, msg -> incrementCounter(newCounter))
                .onMessage(DecrementCounter.class, msg -> decrementCounter(newCounter))
                .onMessage(PrintCounterValue.class, msg -> printCounterValue(newCounter))
                .build();
    }

    private Behavior<Command> decrementCounter(int counter) {
        int newCounter = counter - 1;

        return Behaviors.receive(Command.class)
                .onMessage(IncrementCounter.class, msg -> incrementCounter(newCounter))
                .onMessage(DecrementCounter.class, msg -> decrementCounter(newCounter))
                .onMessage(PrintCounterValue.class, msg -> printCounterValue(newCounter))
                .build();
    }

    private Behavior<Command> printCounterValue(int counter) {

        context.getLog().info("Counter value {}", counter);

        return Behaviors.receive(Command.class)
                .onMessage(IncrementCounter.class, msg -> incrementCounter(counter))
                .onMessage(DecrementCounter.class, msg -> decrementCounter(counter))
                .onMessage(PrintCounterValue.class, msg -> printCounterValue(counter))
                .build();
    }
}
