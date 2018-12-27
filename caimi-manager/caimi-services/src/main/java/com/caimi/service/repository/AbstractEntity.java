package com.caimi.service.repository;

import javax.persistence.Transient;

/**
 * 继承的entity相互没有从属关系
 */
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

    public void setBORepository(BORepository repository) {
		this.repository = repository;
	}

	@Override
	@Transient
    public abstract String getIdAsString();

	public void onRemove() {}

}
