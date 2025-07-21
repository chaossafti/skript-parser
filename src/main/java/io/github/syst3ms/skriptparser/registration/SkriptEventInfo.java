package io.github.syst3ms.skriptparser.registration;

import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.pattern.PatternElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A class containing info about an {@link SkriptEvent event} syntax
 * @param <E> the {@link SkriptEvent} class
 */
public class SkriptEventInfo<E extends SkriptEvent> extends SyntaxInfo<E> {
    private final Set<Class<? extends TriggerContext>> contexts;

    public SkriptEventInfo(SkriptAddon registerer, Class<E> c, Set<Class<? extends TriggerContext>> handledContexts, int priority, List<PatternElement> patterns, Map<String, Object> data, @Nullable Supplier<E> supplier) {
        super(registerer, c, priority, patterns, data, supplier);
        this.contexts = handledContexts;
    }

    /**
     * @return the set of all {@link TriggerContext}s this event is able to handle.
     */
    public Set<Class<? extends TriggerContext>> getContexts() {
        return contexts;
    }
}
