package com.rcpl.platform.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Guards a controller method by the persona's permission on a screen. {@code manage=true}
 * requires the "manage" (write) permission; otherwise "view" is enough. Enforced by
 * {@link ScreenSecurityAspect}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireScreen {
    String value();
    boolean manage() default false;
}
