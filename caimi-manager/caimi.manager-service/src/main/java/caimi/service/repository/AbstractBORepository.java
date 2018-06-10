package caimi.service.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caimi.service.BORepository;
import com.caimi.service.beans.BeansContainer;
import com.caimi.service.repository.BOCacheContainer;
import com.caimi.service.repository.BOCacheKeeper;
import com.caimi.service.repository.BOEntityAccessor;

@SuppressWarnings("rawtypes") 
public class AbstractBORepository implements BORepository, BOCacheContainer{

	private static final Logger logger = LoggerFactory.getLogger(AbstractBORepository.class);
	
	private BeansContainer beansContainer;
	
	
	@Override
	public <T> T getBean(Class<T> clazz) {
		return null;
	}

	@Override
	public <T> T getBean(Class<T> clazz, String urposeOrId) {
		return null;
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> clazz) {
		return null;
	}

	@Override
	public BORepository getRepository() {
		return null;
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

	public void setBeansContainer(BeansContainer beansContainer) {
		this.beansContainer = beansContainer;
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

}
