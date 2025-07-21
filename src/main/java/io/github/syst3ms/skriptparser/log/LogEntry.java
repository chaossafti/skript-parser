package io.github.syst3ms.skriptparser.log;

import org.jetbrains.annotations.Nullable;

/**
 * An entry in Skript's log.
 */
public class LogEntry {
    private final LogType type;
    private final String message;
    private final int line;
    private final List<ErrorContext> errorContext;
    private final ErrorType errorType;
    private final Script script;

    public LogEntry(String message, LogType verbosity, int line, List<ErrorContext> errorContext, @Nullable ErrorType errorType, Script script) {
        this.type = verbosity;
        this.message = message;
        this.line = line;
        this.errorContext = errorContext;
        this.errorType = errorType;
        this.script = script;
    }

    public Script getScript() {
        return script;
    }

    public String getMessage() {
        return message;
    }

    public LogType getType() {
        return type;
    }

    List<ErrorContext> getErrorContext() {
        return errorContext;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public int getLine() {
        return line;
    }
}
