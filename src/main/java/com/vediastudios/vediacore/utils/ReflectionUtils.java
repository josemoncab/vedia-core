package com.vediastudios.vediacore.utils;

import com.vediastudios.vediacore.VediaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ReflectionUtils {

    private static final List<Class<?>> classesCache = new ArrayList<>();

    public static List<Class<?>> getClasses() {
        if (!classesCache.isEmpty()) {
            return classesCache;
        }

        try (final JarFile jarFile = new JarFile(VediaPlugin.getInstance().getFile())) {

            for (final Enumeration<JarEntry> entry = jarFile.entries(); entry.hasMoreElements(); ) {
                final JarEntry jar = entry.nextElement();
                final String name = jar.getName().replace("/", ".");

                if (!name.endsWith(".class")) {
                    continue;
                }

                final String className = name.substring(0, name.length() - 6);
                Class<?> clazz;

                try {
                    clazz = VediaPlugin.class.getClassLoader().loadClass(className);

                } catch (final ClassFormatError | VerifyError | NoClassDefFoundError | ClassNotFoundException |
                               IncompatibleClassChangeError error) {
                    continue;
                }

                if (!clazz.isAnonymousClass()) {
                    classesCache.add(clazz);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return classesCache;
    }
}
