package io.github.syst3ms.skriptparser.parsing;

import io.github.syst3ms.skriptparser.registration.SyntaxInfo;

/**
 * Should be thrown by an INIT_VALIDATOR registered in {@link io.github.syst3ms.skriptparser.registration.SyntaxInfo#INIT_VALIDATORS}.
 * When thrown by whatever reason, instantiation of the Syntax Element will stop and an error will be logged.
 */
public class ParsingDisallowedException extends Exception {
    private final SyntaxInfo<?> syntaxInfo;

    public ParsingDisallowedException(SyntaxInfo<?> syntaxInfo, String message) {
        super(message);
        this.syntaxInfo = syntaxInfo;
    }

    public SyntaxInfo<?> getSyntaxInfo() {
        return syntaxInfo;
    }

}
