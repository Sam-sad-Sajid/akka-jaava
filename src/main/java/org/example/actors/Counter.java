package org.example.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.UUID;

class CounterActor extends AbstractBehavior<CounterActor.CounterCommand> {

    public static Behavior<CounterActor.CounterCommand> init() {
        return Behaviors.setup(CounterActor::new);
    }
    private int counter = 0;

    public static final class CounterCommand {

        enum Command {
            INCREMENT,
            DECREMENT,
            PRINT
        }

        private Command givenCommand;

        public CounterCommand(Command command) {
            this.givenCommand = command;
        }
    }

    private CounterActor(ActorContext<CounterCommand> context) {
        super(context);
    }

    @Override
    public Receive<CounterCommand> createReceive() {
        return newReceiveBuilder().onMessage(CounterCommand.class, this::onMessage).build();
    }

    private Behavior<CounterCommand> onMessage(CounterCommand counterCommand) {
        switch (counterCommand.givenCommand) {
            case INCREMENT -> this.counter++;
            case DECREMENT -> this.counter--;
            case PRINT -> System.out.println(this.counter);
        }

        return this;
    }
}

class GuardianCounterActor extends AbstractBehavior<CounterActor.CounterCommand> {

    private final ActorRef<CounterActor.CounterCommand> actorRef = getContext().spawn(CounterActor.init(), UUID.randomUUID().toString());

    static Behavior<CounterActor.CounterCommand> init() {
        return Behaviors.setup(GuardianCounterActor::new);
    }

    private GuardianCounterActor(ActorContext<CounterActor.CounterCommand> context) {
        super(context);
    }

    @Override
    public Receive<CounterActor.CounterCommand> createReceive() {

        return newReceiveBuilder().onMessage(CounterActor.CounterCommand.class, this::messageHandler).build();
    }

    private Behavior<CounterActor.CounterCommand> messageHandler(CounterActor.CounterCommand command) {

        actorRef.tell(command);

        return Behaviors.same();
    }
}

public class Counter {
    public static void main(String[] args) {
        final ActorSystem<CounterActor.CounterCommand> actorSystem = ActorSystem.create(GuardianCounterActor.init(), "CounterSystem");

        CounterActor.CounterCommand.Command increment = CounterActor.CounterCommand.Command.INCREMENT;
        CounterActor.CounterCommand.Command decrement = CounterActor.CounterCommand.Command.DECREMENT;
        CounterActor.CounterCommand.Command print = CounterActor.CounterCommand.Command.PRINT;

        actorSystem.tell(new CounterActor.CounterCommand(increment));
        actorSystem.tell(new CounterActor.CounterCommand(print));

        actorSystem.tell(new CounterActor.CounterCommand(increment));
        actorSystem.tell(new CounterActor.CounterCommand(print));

        actorSystem.tell(new CounterActor.CounterCommand(decrement));
        actorSystem.tell(new CounterActor.CounterCommand(print));
    }
}
