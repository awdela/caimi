package caimi.service.repository;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caimi.service.BORepository;
import com.caimi.service.beans.BeansContainer;
import com.caimi.service.repository.BOCacheContainer;
import com.caimi.service.repository.BOCacheKeeper;
import com.caimi.service.repository.BOEntityAccessor;

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

}
