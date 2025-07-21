package io.github.syst3ms.skriptparser.lang.event;

import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.util.MultiMap;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class SkriptEventManager {
    public static final SkriptEventManager GLOBAL_EVENT_MANAGER = new SkriptEventManager();

    private final MultiMap<String, SkriptEventHandler> events = new MultiMap<>();

    public SkriptEventManager() {

    }

    /**
     * Wraps a Trigger into an EventHandler and registers it.
     * @param clazz The event class
     * @param trigger The trigger to run when this even executes
     * @return the created event handler
     */
    public TriggerEventHandler registerTrigger(@NotNull Class<? extends SkriptEvent> clazz, @NotNull Trigger trigger) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(trigger);

        TriggerEventHandler triggerEventHandler = new TriggerEventHandler(trigger, this);
        events.putOne(clazz.getName(), triggerEventHandler);
        return triggerEventHandler;
    }

    /**
     * Wraps a Trigger into an EventHandler and registers it.
     * @param clazz The event class
     * @param trigger The trigger to run when this even executes
     * @param contextPredicate The trigger will only be run when this predicate returns true.
     * @return the created event handler
     */
    public TriggerEventHandler registerTrigger(@NotNull Class<? extends SkriptEvent> clazz, @NotNull Trigger trigger, Predicate<TriggerContext> contextPredicate) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(trigger);
        TriggerEventHandler triggerEventHandler = new TriggerEventHandler(trigger, contextPredicate, this);
        events.putOne(clazz.getName(), triggerEventHandler);
        return triggerEventHandler;
    }

    public void registerEventHandler(@NotNull Class<? extends SkriptEvent> clazz, @NotNull SkriptEventHandler eventHandler) {
        Objects.requireNonNull(eventHandler);
        Objects.requireNonNull(clazz);

        events.putOne(clazz.getName(), eventHandler);
    }

    public void registerEventHandler(@NotNull String eventName, @NotNull SkriptEventHandler eventHandler) {
        Objects.requireNonNull(eventHandler);
        Objects.requireNonNull(eventName);

        events.putOne(eventName, eventHandler);
    }


    public void removeEventHandler(@NotNull Class<? extends SkriptEvent> clazz, @NotNull SkriptEventHandler eventHandler) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(eventHandler);

        List<SkriptEventHandler> registeredEvents = events.get(clazz.getName());
        registeredEvents.removeIf(skriptEventInfo -> skriptEventInfo == eventHandler);
    }


    public void callEvent(@NotNull Class<? extends SkriptEvent> clazz, TriggerContext context) {
        callEvent(clazz.getName(), context);
    }

    public void callEvent(@NotNull String eventName, TriggerContext context) {
        List<SkriptEventHandler> registeredHandlers = events.get(eventName);
        if(registeredHandlers == null) {
            return;
        }
        for (SkriptEventHandler eventHandler : registeredHandlers) {
            if(eventHandler.supports(context)) {
                eventHandler.handle(context);
            }
        }
    }


}
