package com.caimi.service.event.processing;

import org.json.JSONObject;

import com.caimi.service.event.NovelEvent;

public class NovelEventProcessRuntimeInfoImpl extends AbsEventProcessingRuntimeInfo implements EventProcessRuntimeInfo {

    private NovelEvent event;
    private String novelName;
    private String novelAuth;
    private String characterName;
    private String characterContent;

    @Override
    public NovelEvent getEvent() {
        return event;
    }

    public void update(NovelEvent event) {
        this.event = event;
        setId(event.getId());
        setDisable(true);
        setPriority(event.getPriority());
        errorReason = null;
        novelName = event.getNovelName();
        novelAuth = event.getNovelAuth();
        characterName = event.getCharacterName();
        characterContent = event.getCharacterContent();

    }

    public String getNovelName() {
        return novelName;
    }

    public String getNovelAuth() {
        return novelAuth;
    }

    public String getCharacterName() {
        return characterName;
    }

    public String getCharacterContent() {
        return characterContent;
    }

    public void touchLastMatchTime() {
        lastMatchTime = System.currentTimeMillis();
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = super.toJSONObject();
        json.put("event", event.toString());
        return json;
    }

    @Override
    public String toString() {
        return "NovelEventProcessRuntimeInfoImpl [event=" + event + ", novelName=" + novelName + ", novelAuth="
                + novelAuth + ", characterName=" + characterName + ", characterContent=" + characterContent + "]";
    }

}
