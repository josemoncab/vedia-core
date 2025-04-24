package com.vediastudios.vediacore.logger;

import com.vediastudios.vediacore.VediaPlugin;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public final class Log {

    //TODO: Change to adventure components

    private static final ComponentLogger logger = VediaPlugin.getInstance().getComponentLogger();
    private static final String PREFIX = "Core";

    public static void info(String msg) {
        logger.info("[{}] {}", PREFIX, msg);
    }

    public static void warn(String msg) {
        logger.warn("[{}] {}", PREFIX, msg);
    }

    public static void error(String msg) {
        logger.error("[{}] {}", PREFIX, msg);
    }

    public static void debug(String msg) {
        logger.info("[{}] {}", PREFIX, msg);
    }
}
