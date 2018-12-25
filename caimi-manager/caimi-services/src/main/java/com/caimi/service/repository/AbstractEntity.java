package com.caimi.service.repository;

import java.io.Serializable;

import javax.persistence.Transient;

import com.caimi.service.repository.BOEntity;

public abstract class AbstractEntity implements BOEntity{

	@Transient
	protected BORepository repository;
	
	public AbstractEntity() {}
	
	public AbstractEntity(BORepository repository) {
		this.repository = repository;
	}

	@Override
	public BORepository getRepository() {
		return repository;
	}
	
	public void setRepository(BORepository repository) {
		this.repository = repository;
	}
	
//	public abstract void setId(Serializable id);
	
	@Override
	@Transient
    public abstract String getIdAsString();
	
	public void onRemove() {}
	
}
