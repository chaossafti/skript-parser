package io.github.syst3ms.skriptparser.parsing.script;

import io.github.syst3ms.skriptparser.lang.SyntaxElement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ScriptLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

public class Script {
    private final Path path;
    private final String name;

    private Set<Trigger> triggers;
    private boolean isLoaded = true;

    public Script(@Nullable Set<Trigger> triggers, @NotNull Path path, @NotNull String name) {
        this.triggers = triggers;
        if(triggers == null) isLoaded = false;
        this.path = path;
        this.name = name;
    }

    /**
     * Unloads this script. That means, it will invoke {@link SyntaxElement#onUnload()} for every syntax element it can locate.
     * This exact instance will still be able to be found using {@link io.github.syst3ms.skriptparser.parsing.ScriptLoader#getScript(Path)}
     */
    public void unload() {
        if(!isLoaded) {
            throw new IllegalStateException("Tried unload loaded script: " + path);
        }

        triggers.forEach(SyntaxElement::onUnload);
        isLoaded = false;
        triggers = null;

    }

    public ScriptLoadResult reload() {
        if(isLoaded) {
            unload();
        }

        SkriptLogger logger = new SkriptLogger();
        return ScriptLoader.loadScript(this, logger);

    }


    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Path getPath() {
        return path;
    }

    @Nullable
    public Set<Trigger> getTriggers() {
        return triggers;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Sets the Triggers (Structures/Events) of this script.
     *
     * @param triggers The triggers to load into this Script.
     * @throws IllegalStateException when the script is loaded
     */
    @ApiStatus.Internal
    public void load(@NotNull Set<Trigger> triggers) throws IllegalStateException {
        Objects.requireNonNull(triggers);
        if(isLoaded()) {
            throw new IllegalStateException("Tried providing an already loaded script with a new set of triggers!");
        }

        this.triggers = triggers;

    }
}
