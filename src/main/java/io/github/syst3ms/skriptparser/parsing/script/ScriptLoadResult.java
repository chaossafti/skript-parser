package io.github.syst3ms.skriptparser.parsing.script;

import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.SyntaxElement;
import io.github.syst3ms.skriptparser.log.LogEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ScriptLoadResult {
    private final List<LogEntry> log;
    @Nullable
    private final Script script;


    public ScriptLoadResult(@Nullable Script script) {
        this.log = null;
        this.script = script;
    }

    public ScriptLoadResult(@NotNull List<LogEntry> log, @Nullable Script script) {
        this.log = log;
        this.script = script;
    }

    public @Nullable Script getScript() {
        return script;
    }

    public Optional<List<LogEntry>> getLog() {
        return Optional.ofNullable(log);
    }

    public boolean hasParsedSuccessfully() {
        return script != null;
    }

}
