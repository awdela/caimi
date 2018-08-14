package com.caimi.service.event;

public interface NovelEvent extends Event {

    public String getId();

    public int getPriority();

    public String getNovelName();

    public String getNovelAuth();

    public String getCharacterName();

    public String getCharacterContent();
}
