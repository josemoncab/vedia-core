package com.vediastudios.vediacore.utils;

import com.vediastudios.vediacore.VediaPlugin;
import com.vediastudios.vediacore.annotations.ConfigurationHolder;
import com.vediastudios.vediacore.annotations.LanguageHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public final class ReflectionUtils {

    public static List<Class<?>> getClasses(Class<?> toSearch) {
        final List<Class<?>> classes = new ArrayList<>();

        final Pattern anonymousClassPattern = Pattern.compile("\\w+\\$[0-9]$");

        try (final JarFile jarFile = new JarFile(VediaPlugin.getInstance().getFile())) {

            for (final Enumeration<JarEntry> entry = jarFile.entries(); entry.hasMoreElements(); ) {
                final JarEntry jar = entry.nextElement();
                final String name = jar.getName().replace("/", ".");

                if (!name.endsWith(".class")) {
                    continue;
                }

                final String className = name.substring(0, name.length() - 6);
                Class<?> clazz = null;

                try {
                    clazz = VediaPlugin.class.getClassLoader().loadClass(className);

                } catch (final ClassFormatError | VerifyError | NoClassDefFoundError | ClassNotFoundException |
                               IncompatibleClassChangeError error) {
                    continue;
                }

                if (!clazz.isAnonymousClass() && (clazz.isAnnotationPresent(ConfigurationHolder.class) || clazz.isAnnotationPresent(LanguageHolder.class))) {
                    classes.add(clazz);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return classes;
    }
}
