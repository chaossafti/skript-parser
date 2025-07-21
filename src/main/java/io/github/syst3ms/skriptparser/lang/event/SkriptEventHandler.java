package io.github.syst3ms.skriptparser.lang.event;

import io.github.syst3ms.skriptparser.lang.TriggerContext;

public interface SkriptEventHandler {

    /**
     * Invoked when an event of that type is called.
     * @param context The event context
     */
    void handle(TriggerContext context);

    /**
     * Checks if this event handler supports trigger contexts of this type
     * @param context The context to check.
     * @return true if the context is supported, false otherwise.
     */
    default boolean supports(TriggerContext context) {
        return true;
    }

}
