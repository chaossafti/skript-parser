package io.github.syst3ms.skriptparser.registration;

import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.SyntaxElement;
import io.github.syst3ms.skriptparser.parsing.ParsingDisallowedException;
import io.github.syst3ms.skriptparser.pattern.PatternElement;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A class containing info about a {@link SyntaxElement} that isn't an {@link Expression} or an {@link SkriptEvent}
 * @param <C> the {@link SyntaxElement} class
 */
public class SyntaxInfo<C> {
    /**
     * Every consumer will be run with a SyntaxInfo whenever {@link #createInstance()} is run.
     * Throwing an {@link ParsingDisallowedException} will stop the new instance from being created.
     * @see ParsingDisallowedException
     */
    public static Set<Consumer<SyntaxInfo<?>>> INIT_VALIDATORS = new HashSet<>();

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

    public C createInstance() throws ParsingDisallowedException {
        INIT_VALIDATORS.forEach(consumer -> consumer.accept(this));

        if(supplier != null) {
            return supplier.get();
        }

        // use reflection to create an instance
        try {
            return c.getDeclaredConstructor()
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            System.err.println("Could not create instance of: " + c);
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
