package io.github.syst3ms.skriptparser.registration;

import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.SyntaxElement;
import io.github.syst3ms.skriptparser.pattern.PatternElement;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A class containing info about a {@link SyntaxElement} that isn't an {@link Expression} or an {@link SkriptEvent}
 * @param <C> the {@link SyntaxElement} class
 */
public class SyntaxInfo<C> {
    private final Class<C> c;
    private final List<PatternElement> patterns;
    private final int priority;
    private final SkriptAddon registerer;
    protected final Map<String, Object> data;
    @Nullable
    private final Supplier<C> supplier;

    public SyntaxInfo(SkriptAddon registerer, Class<C> c, int priority, List<PatternElement> patterns) {
        this(registerer, c, priority, patterns, null);
    }

    public SyntaxInfo(SkriptAddon registerer, Class<C> c, int priority, List<PatternElement> patterns, @Nullable Supplier<C> supplier) {
        this(registerer, c, priority, patterns, new HashMap<>(), supplier);
    }

    public SyntaxInfo(SkriptAddon registerer, Class<C> c, int priority, List<PatternElement> patterns, Map<String, Object> data, @Nullable Supplier<C> supplier) {
        this.c = c;
        this.patterns = patterns;
        this.priority = priority;
        this.registerer = registerer;
        this.data = data;
        this.supplier = supplier;
    }

    public SkriptAddon getRegisterer() {
        return registerer;
    }

    public Class<C> getSyntaxClass() {
        return c;
    }

    public int getPriority() {
        return priority;
    }

    public List<PatternElement> getPatterns() {
        return patterns;
    }

    public @Nullable Supplier<C> getSupplier() {
        return supplier;
    }

    public C createInstance() {
        if(supplier != null) {
            return supplier.get();
        }

        // use reflection to create an instance
        try {
            //noinspection deprecation
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a data instance by its identifier.
     * @param identifier the identifier
     * @param type the expected data type
     * @return the data instance
     */
    public <T> T getData(String identifier, Class<T> type) {
        return type.cast(data.get(identifier));
    }
}
