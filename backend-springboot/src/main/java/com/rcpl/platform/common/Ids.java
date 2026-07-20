package com.rcpl.platform.common;

import java.util.UUID;

/** Short unique id generation for entities whose ids are client-facing strings. */
public final class Ids {

    private Ids() {}

    /** e.g. newId("n") -> "n-3f9a2c…". Stable, collision-safe, readable prefix. */
    public static String newId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
