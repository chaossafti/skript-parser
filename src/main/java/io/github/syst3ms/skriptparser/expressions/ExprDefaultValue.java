package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

/**
 * A shorthand expression for giving things a default value. If the first thing isn't set, the second thing will be returned.
 *
 * @name Default Value
 * @pattern %objects% (otherwise|?) %objects%
 * @since ALPHA
 * @author Olyno
 */
public class ExprDefaultValue implements Expression<Object> {
    static {
        Parser.getMainRegistration().addExpression(
                ExprDefaultValue.class,
                Object.class,
                false,
                "%objects% (otherwise|?) %objects%"
        );
    }

    private Expression<Object> firstValue, secondValue;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        firstValue = (Expression<Object>) expressions[0];
        secondValue = (Expression<Object>) expressions[1];
        return true;
    }

    @Override
    public Object[] getValues(@NotNull TriggerContext ctx) {
        return firstValue.getValues(ctx).length != 0
                ? firstValue.getValues(ctx)
                : secondValue.getValues(ctx);
    }

    @Override
    public boolean isSingle() {
        return firstValue.isSingle() && secondValue.isSingle();
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return firstValue.toString(ctx, debug) + " otherwise " + secondValue.toString(ctx, debug);
    }
}
