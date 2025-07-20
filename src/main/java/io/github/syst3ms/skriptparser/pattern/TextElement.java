package io.github.syst3ms.skriptparser.pattern;

import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.MatchContext;
import io.github.syst3ms.skriptparser.parsing.ParserState;
import org.jetbrains.annotations.Nullable;

import java.math.MathContext;
import java.util.Optional;

/**
 * Text inside of a pattern. Is case and whitespace insensitive.
 */
public class TextElement implements PatternElement {
    private final String text;

    public TextElement(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof TextElement && text.equalsIgnoreCase(((TextElement) obj).text);
    }

    @Override
    public int match(String s, int index, MatchContext context) {
        if (text.isEmpty()) return index;

        String stripped = text.strip();
        int pos = index;

        // Handle leading whitespace
        if (Character.isWhitespace(text.charAt(0))) {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
                pos++;
            }
        }

        // Check if stripped text fits
        if (stripped.isEmpty()) {
            return pos; // nothing to match, just whitespace
        }

        if (pos + stripped.length() > s.length()) {
            return -1;
        }

        // Main match (case-insensitive)
        if (!s.regionMatches(true, pos, stripped, 0, stripped.length())) {
            return -1;
        }

        pos += stripped.length();

        // Handle trailing whitespace
        if (Character.isWhitespace(text.charAt(text.length() - 1))) {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
                pos++;
            }
        }

        return pos;
    }

    @Override
    public String toString() {
        return text;
    }
}
