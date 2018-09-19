package com.caimi.service.repository;

import java.io.Serializable;

public interface BOEntityIdGenerator {

    public Serializable generateId(Object entity);

}
