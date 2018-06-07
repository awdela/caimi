package com.caimi.service.repository;

import com.caimi.service.BORepository;

public interface BOEntity {

	public static final String ID_PREFIX_USER = "usr_";
	
	public static final String ID_PREFIX_ARTICLE = "atc_";
	
	public static final int KEY_ID = 0;
	
    public String getIdAsString();
	
	public BORepository getRepository();
	
}
