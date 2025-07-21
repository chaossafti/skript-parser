package io.github.syst3ms.skriptparser.lang.event;

import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class TriggerEventHandler implements SkriptEventHandler {
    private final Trigger trigger;
    private final Predicate<TriggerContext> supportPredicate;
    private final SkriptEventManager attachedEventManager;

    public TriggerEventHandler(Trigger trigger, SkriptEventManager attachedEventManager) {
        this(trigger, context -> true, attachedEventManager);
    }

    public TriggerEventHandler(@NotNull Trigger trigger, @NotNull Predicate<TriggerContext> supportPredicate, SkriptEventManager attachedEventManager) {
        this.trigger = trigger;
        this.supportPredicate = supportPredicate;
        this.attachedEventManager = attachedEventManager;
    }

    @NotNull
    public Trigger getTrigger() {
        return trigger;
    }

    @Override
    public void handle(TriggerContext context) {
        Statement.runAll(trigger, context);
    }


    @Override
    public boolean supports(TriggerContext context) {
        return supportPredicate.test(context);
    }

    public SkriptEventManager getAttachedEventManager() {
        return attachedEventManager;
    }
}
