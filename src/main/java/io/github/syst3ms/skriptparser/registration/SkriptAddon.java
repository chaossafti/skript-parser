package io.github.syst3ms.skriptparser.registration;

import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.parsing.ParseContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The base for all addons, modules that hook into the API to register syntax and handle triggers.
 */
public abstract class SkriptAddon {

    private final List<Class<? extends SkriptEvent>> handledEvents = new ArrayList<>();
    private static final List<SkriptAddon> ADDONS = new ArrayList<>();

    {
        ADDONS.add(this);
    }

    /**
     * Returns unmodifiable list of all SkriptAddons that are registered globally.
     * 
     * @return SkriptAddons that are registered.
     */
    public static List<SkriptAddon> getAddons() {
        return Collections.unmodifiableList(ADDONS);
    }

    /**
     * When a {@linkplain Trigger} is successfully parsed, it is "broadcasted" to all addons through this method,
     * in the hopes that one of them will be able to handle it.
     * @param trigger the trigger to be handled
     * @see #canHandleEvent(SkriptEvent)
     * @deprecated Use {@link io.github.syst3ms.skriptparser.lang.SyntaxElement#init(Expression[], int, ParseContext)}
     */
    @Deprecated
    public abstract void handleTrigger(Trigger trigger);

    /**
     * Is called when a script has finished loading. Optionally overridable.
     */
    public void finishedLoading() {}

    /**
     * Checks to see whether the given event has been registered by this SkriptAddon ; a basic way to filter out
     * triggers you aren't able to deal with in {@link SkriptAddon#handleTrigger(Trigger)}.
     * @param event the event to check
     * @return whether the event can be handled by the addon or not
     * @deprecated Don't use this. No replacement.
     */
    @Deprecated
    public final boolean canHandleEvent(SkriptEvent event) {
        return handledEvents.contains(event.getClass());
    }

    void addHandledEvent(Class<? extends SkriptEvent> event) {
        handledEvents.add(event);
    }

}
