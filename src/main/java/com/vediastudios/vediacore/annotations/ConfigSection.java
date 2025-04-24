package com.vediastudios.vediacore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a local class as a section inside a file. The path will be added
 * automatically to the start of the path of its fields
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigSection {

    /**
     * Path of the section inside a file
     */
    String value();
}
