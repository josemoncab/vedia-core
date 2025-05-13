package com.vediastudios.vediacore.configurations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate a class that will be linked to a file and hold configuration
 * that can be edited by the users.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Source {

    /**
     * The relative path of the file in the disk based on the plugin folder
     * {@code ([serverRoot]/plugins/[plugin name])}.
     */
    String value();
}