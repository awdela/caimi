package com.caimi.service.repository;

import java.io.Serializable;

/**
 * Business Object
 */
public interface BOEntity {

    public static final String ID_PREFIX_USER = "usr_";

    public static final String ID_PREFIX_ARTICLE = "atc_";

    public static final int KEY_ID = 0;

    public void setId(Serializable id);

    public String getIdAsString();

    public BORepository getRepository();

}
