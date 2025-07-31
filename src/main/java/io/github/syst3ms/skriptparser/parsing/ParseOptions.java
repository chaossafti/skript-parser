package io.github.syst3ms.skriptparser.parsing;

public class ParseOptions {
    private boolean debug;
    private boolean dry;

    public ParseOptions() {

    }

    public ParseOptions withDry() {
        this.dry = true;
        return this;
    }

    public ParseOptions dry(boolean dry) {
        this.dry = dry;
        return this;
    }

    public ParseOptions debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public ParseOptions withDebug() {
        this.debug = true;
        return this;
    }


    public boolean isDry() {
        return dry;
    }

    public boolean isDebug() {
        return debug;
    }
}
