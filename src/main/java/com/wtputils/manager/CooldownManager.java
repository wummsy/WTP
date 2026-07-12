package com.wtputils.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Generic per-player, per-category cooldown tracker (used separately for tpa and rtp).
 */
public class CooldownManager {

    private final Map<UUID, Long> nextAllowed = new HashMap<>();

    /** Returns remaining seconds on cooldown, or 0 if none. */
    public long getRemainingSeconds(UUID playerId) {
        Long expiry = nextAllowed.get(playerId);
        if (expiry == null) return 0;
        long remainingMillis = expiry - System.currentTimeMillis();
        if (remainingMillis <= 0) {
            nextAllowed.remove(playerId);
            return 0;
        }
        return (remainingMillis / 1000) + 1;
    }

    public void applyCooldown(UUID playerId, int seconds) {
        if (seconds <= 0) return;
        nextAllowed.put(playerId, System.currentTimeMillis() + (seconds * 1000L));
    }

    public void clear(UUID playerId) {
        nextAllowed.remove(playerId);
    }
}
