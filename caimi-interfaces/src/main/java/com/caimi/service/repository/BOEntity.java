package com.caimi.service.repository;

import com.caimi.service.BORepository;

/**
 * 该接口是为了给ignite使用
 */
public interface BOEntity {

    public static final String ID_PREFIX_USER = "usr_";

    public static final String ID_PREFIX_ARTICLE = "atc_";

    public static final int KEY_ID = 0;

    public String getIdAsString();

    public BORepository getRepository();

}
