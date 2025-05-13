package com.vediastudios.vediacore.configurations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate a class that will be linked to a file and hold configuration that
 * can be edited by the users. Uses the language configured in the "Lang"
 * value in the settings or fallback to en_us by default
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LanguageSource {

    /**
     * Language code for the localization. By default, is "en_us" and will fall back
     * to this if the provided code has no file inside the jar
     *
     * @return lang value
     */
    String value() default "en_us";
}

