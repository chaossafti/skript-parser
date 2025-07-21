package io.github.syst3ms.skriptparser;

import io.github.syst3ms.skriptparser.registration.SkriptRegistration;

@Deprecated
public class Parser {
    private static final SkriptRegistration REGISTRATION = new SkriptRegistration(new Skript());


    public static SkriptRegistration getMainRegistration() {
        return REGISTRATION;
    }

}
