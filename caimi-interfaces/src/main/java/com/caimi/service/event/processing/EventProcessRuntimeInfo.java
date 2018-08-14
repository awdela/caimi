package com.caimi.service.event.processing;

import com.caimi.service.event.Event;
import com.caimi.util.RuntimeInfo;

public interface EventProcessRuntimeInfo extends RuntimeInfo {

    public Event getEvent();
}
