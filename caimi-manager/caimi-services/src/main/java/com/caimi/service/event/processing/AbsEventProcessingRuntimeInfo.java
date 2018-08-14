package com.caimi.service.event.processing;

import org.json.JSONObject;

import com.caimi.util.RuntimeInfo;

public abstract class AbsEventProcessingRuntimeInfo implements RuntimeInfo {

    protected String id;
    protected int priority;
    protected boolean disable;
    protected long totalMatchedSize;
    protected long lastMatchTime;
    protected String errorReason;

    public AbsEventProcessingRuntimeInfo setId(String id) {
        this.id = id;
        return this;
    }

    public AbsEventProcessingRuntimeInfo setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public AbsEventProcessingRuntimeInfo setDisable(boolean disable) {
        this.disable = disable;
        return this;
    }

    public AbsEventProcessingRuntimeInfo setTotalMatchedSize(long totalMatchedSize) {
        this.totalMatchedSize = totalMatchedSize;
        return this;
    }

    public AbsEventProcessingRuntimeInfo setLastMatchTime(long lastMatchTime) {
        this.lastMatchTime = lastMatchTime;
        return this;
    }

    public AbsEventProcessingRuntimeInfo setErrorReason(String errorReason) {
        this.errorReason = errorReason;
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public long getLastMatchTime() {
        return lastMatchTime;
    }

    @Override
    public long getTotalMatchSize() {
        return totalMatchedSize;
    }

    @Override
    public String getErrorReason() {
        return errorReason;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("priority", priority);
        json.put("lastMatchTime", lastMatchTime);
        json.put("totalMatchedSize", totalMatchedSize);
        json.put("errorReason", errorReason);
        return json;
    }

}
