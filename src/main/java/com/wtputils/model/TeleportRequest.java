package com.wtputils.model;

import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class TeleportRequest {

    private final UUID senderId;
    private final UUID targetId;
    private final RequestType type;
    private final long createdAt;
    private BukkitTask expiryTask;

    public TeleportRequest(UUID senderId, UUID targetId, RequestType type) {
        this.senderId = senderId;
        this.targetId = targetId;
        this.type = type;
        this.createdAt = System.currentTimeMillis();
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public RequestType getType() {
        return type;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setExpiryTask(BukkitTask expiryTask) {
        this.expiryTask = expiryTask;
    }

    public void cancelExpiryTask() {
        if (expiryTask != null && !expiryTask.isCancelled()) {
            expiryTask.cancel();
        }
    }
}
