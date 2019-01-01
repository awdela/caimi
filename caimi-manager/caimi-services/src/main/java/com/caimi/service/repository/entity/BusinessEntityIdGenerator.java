package com.caimi.service.repository.entity;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caimi.service.repository.BOEntityIdGenerator;
import com.caimi.util.UUIDUtil;

/**
 * 为实体生成对应的id
 */
public class BusinessEntityIdGenerator implements IdentifierGenerator, BOEntityIdGenerator{

	private static final Logger logger = LoggerFactory.getLogger(BusinessEntityIdGenerator.class);
	
	@Override
	public Serializable generateId(Object entity) {
		return generate(null, entity);
	}

	@Override
	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
		if (object instanceof User) {
			return BusinessEntity.ID_PREFIX_USER+UUIDUtil.genId();
		}
		return UUIDUtil.genId();
	}
	

}
