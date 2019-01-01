package com.caimi.service.repository;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import com.caimi.service.repository.AbstractBORepository.EntityInfo;

public class AbstractBORepositoryTest{

	@Test
	public void test() {
		SpringJPABORepository jpa = new SpringJPABORepository();
		jpa.init();
		Map<Class, EntityInfo> entityInfo =  jpa.entityInfos;
		Iterator iter = entityInfo.keySet().iterator();
		String className = null;
		while(iter.hasNext()) {
			className = (String) iter.next();
		}
		assertTrue("UserEntity".equals(className));
	}

}
