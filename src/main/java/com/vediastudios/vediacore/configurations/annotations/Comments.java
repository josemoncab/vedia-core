package com.vediastudios.vediacore.configurations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * List of comments to add to the fiel when we create the file. If it's put in a
 * class, it will add the comments on top of the section in the file
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Comments {

    /**
     * Accepts a single string or a list where each string will be put in a new line
     *
     * @return Comments of the field
     */
    String[] value();
}
