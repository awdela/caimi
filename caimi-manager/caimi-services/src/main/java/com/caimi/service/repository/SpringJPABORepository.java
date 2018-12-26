package com.caimi.service.repository;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

import com.caimi.service.beans.BeansContainer;
@Service
public class SpringJPABORepository extends AbstractBORepository implements BORepository, BOEntityAccessor{

	private static final Logger logger = LoggerFactory.getLogger(SpringJPABORepository.class);
	
	@Autowired
	private ApplicationContext appContext;

	@Autowired
    private ExecutorService asyncExecutor;
	
	@Autowired
	private EntityManager em;
	
	// spring事务管理器
	@Autowired
	private PlatformTransactionManager tm;
	
	/**
	 *  spring事务状态
	 *  为每一个数据库操作产生一个线性安全的事务
	 */
	private ThreadLocal<TransactionStatus> currTxnStatus = new ThreadLocal<>(); 

	@Override
    @PostConstruct
    protected void init() {
		setExecutor( appContext.getBean(Executor.class) );
		setBeansContainer( appContext.getBean(BeansContainer.class) );
		setExecutorService( asyncExecutor );
        super.init();
    }

    @Override
    protected BOEntityAccessor getDefaultEntityAccessor() {
        return this;
    }

	@Override
	public void beginTransaction(boolean readOnly) {
		TransactionStatus txnStatus = currTxnStatus.get();
		if (txnStatus!=null) {
			if (txnStatus.isCompleted()) {
				// 当前事务已结束
				currTxnStatus.remove();
				txnStatus = null;
			}else if (txnStatus.isRollbackOnly()!=readOnly) {
				throw new RuntimeException("Unable to start new transaction with out commit/rollback current");
			}else {
				// 完全一致,do nothing
				if (logger.isDebugEnabled()){
					logger.debug("beginTransaction(\"+readOnly+\") is the same with current txn, do nothing");
				}
				return;
			}
		}else {
			DefaultTransactionDefinition txnDef = new DefaultTransactionDefinition();
			txnDef.setReadOnly(readOnly);
			txnStatus = tm.getTransaction(txnDef);
			currTxnStatus.set(txnStatus);
			if (logger.isDebugEnabled()) {
				logger.debug("beginTransaction( "+readOnly+" )");
			}
		}
	}

	@Override
	public boolean endTransaction(boolean commit) {
		if (currTxnStatus.get()==null) {
			return false;
		}
		TransactionStatus txnStatus = currTxnStatus.get();
		if (logger.isDebugEnabled()) {
			logger.debug("endTransaction("+commit+") on current(rollbackOnly="+txnStatus.isRollbackOnly()+")");
		}
		currTxnStatus.remove();
		if (commit) {
			tm.commit(txnStatus);
		}else {
			tm.rollback(txnStatus);
		}
		return true;
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
	public <T> T saveEntity(T entity) {
		if (((AbstractEntity)entity).getIdAsString()==null) {
			em.persist(entity);
		}else {
			try {
				entity = em.merge(entity);
			}catch(Exception e) {
				em.persist(entity);
			}
		}
		return entity;
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

    @Override
    public BOEntityAccessor getAccessor(Class entityClass) {
        return null;
    }

}
