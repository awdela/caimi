package com.caimi.service.repository.entity.cache;

import java.time.LocalDateTime;

import com.caimi.service.repository.entity.AbstractBusinessEntity;

public class UserEntity extends AbstractBusinessEntity{

	@Override
	public LocalDateTime touchActivityTime() {
		return null;
	}

}
