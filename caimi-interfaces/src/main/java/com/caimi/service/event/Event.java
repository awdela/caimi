package com.caimi.service.event;

import com.caimi.util.JSONEnabled;

/**
 * 所有可以消费的字段
 */
public interface Event extends JSONEnabled {

    public static final String NOVEL_NAME = "novel_name";

    public static final String NOVEL_auth = "novel_auth";

    public static final String NOVEL_character_name = "character_name";

    public static final String NOVEL_content = "character_content";

}
