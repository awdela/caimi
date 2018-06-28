package com.caimi.service.repository;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.caimi.service.BORepository;
import com.caimi.service.beans.BeansContainer;

@Service
public class SpringJPABORepository extends AbstractBORepository implements BORepository, BOEntityAccessor{

	@Autowired
	private ApplicationContext appContext;
	private Executor asyncExecutor;
	
	@Override
	public void init(BOCacheContainer container) {
		setBeansContainer(appContext.getBean(BeansContainer.class));
		setExecutor(asyncExecutor);
	}
	
	@Override
	public BOEntityAccessor getAccessor(Class entityClass) {
		return null;
	}

	@Override
	public <T> BOCacheKeeper<T> getCacheKeeper(Class<T> boClass) {
		return null;
	}

	@Override
	public Collection<Class> getBOClass() {
		return null;
	}

	@Override
	public Class detectBOClassById(String idOrPrefix) {
		return null;
	}

	@Override
	public Lock getLock(Class boClass) {
		return null;
	}

	@Override
	public <T> Object getIdBy(Class<T> boClass, int key, Object keyId) {
		return null;
	}

	@Override
	public void save(Object entity) {
		
	}

	@Override
	public void saveAll(List<Object> entities) {
		
	}

	@Override
	public void remove(Object entity) {
		
	}

	@Override
	public <T> List<T> search(Class<T> boClass, String searchExpr) {
		return null;
	}

	@Override
	public <T> List<Object[]> search(Class<T> boClass, String searchExpr, String[] fields) {
		return null;
	}

	@Override
	public void beginTransaction(boolean readOnly) {
		
	}

	@Override
	public void endTransaction(boolean commit) {
		
	}

	@Override
	public void asyncUpdate(Runnable asyncTask) {
		
	}

	@Override
	public <T> List<T> reloadAll(Class<T> boClass, List<Object> boIds) {
		return null;
	}

	@Override
	public <T> T loadEntity(Class<T> entityClass, Object boId) {
		return null;
	}

	@Override
	public <T> List<T> loadAllEntities(Class<T> entityClass, List boIds) {
		return null;
	}

	@Override
	public <T> T saveEntity(T t) {
		return null;
	}

	@Override
	public <T> List<T> saveAllEntites(List<T> t) {
		return null;
	}

	@Override
	public <T> List<T> searchEntity(Class<T> entityClass, String searcgExpr) {
		return null;
	}

	@Override
	public <T> List<Object[]> searchEntity(Class<T> entityClass, String searchExpr, String[] fields) {
		return null;
	}

	@Override
	public boolean removeEntity(Object o) {
		return false;
	}

	@Override
	public int removeAllEntites(Collection entityInstances) {
		return 0;
	}

}
