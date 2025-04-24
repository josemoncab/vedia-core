package com.vediastudios.vediacore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Path of the config in the file. If is inside a class annotated with
 * {@link ConfigSection}, we automatically add the section before the path.
 *
 * <p>Example: Class annotated with {@code @ConfigSection("Test")} and a field inside
 * annotated with {@code @ConfigPath("Lang")}, the resulted path will be "Test.Lang"
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigPath {

    /**
     * Path separated with ".". We recommend using {@link ConfigSection} instead
     * of defining sections with the dot notation ("One.Two")
     */
    String[] value();
}
