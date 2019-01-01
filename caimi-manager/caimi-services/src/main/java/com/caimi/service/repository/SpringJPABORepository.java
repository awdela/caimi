package com.caimi.service.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

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

import com.caimi.service.beans.BeansContainer;
import com.caimi.util.StringUtil;

@Service
public class SpringJPABORepository extends AbstractBORepository implements BORepository, BOEntityAccessor {

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
     * spring事务状态 ThreadLocal:为每一个线程开启一个线程安全的事务
     */
    private ThreadLocal<TransactionStatus> currTxnStatus = new ThreadLocal<>();

    @Override
    @PostConstruct
    public void init() {
        setExecutor(appContext.getBean(Executor.class));
        setBeansContainer(appContext.getBean(BeansContainer.class));
        setExecutorService(asyncExecutor);
        super.init();
    }

    @Override
    protected BOEntityAccessor getDefaultEntityAccessor() {
        return this;
    }

    @Override
    public void beginTransaction(boolean readOnly) {
        // 由于是线程共享的,所以查看所有线程中有没有事务
        TransactionStatus txnStatus = currTxnStatus.get();
        if (txnStatus != null) {
            if (txnStatus.isCompleted()) {
                // 当前事务已结束
                currTxnStatus.remove();
                txnStatus = null;
            } else if (txnStatus.isRollbackOnly() != readOnly) {
                throw new RuntimeException("Unable to start new transaction with out commit/rollback current");
            } else {
                // 完全一致,do nothing
                if (logger.isDebugEnabled()) {
                    logger.debug("beginTransaction(\"+readOnly+\") is the same with current txn, do nothing");
                }
                return;
            }
        } else {
            DefaultTransactionDefinition txnDef = new DefaultTransactionDefinition();
            txnDef.setReadOnly(readOnly);
            txnStatus = tm.getTransaction(txnDef);
            currTxnStatus.set(txnStatus);
            if (logger.isDebugEnabled()) {
                logger.debug("beginTransaction( " + readOnly + " )");
            }
        }
    }

    @Override
    public boolean endTransaction(boolean commit) {
        if (currTxnStatus.get() == null) {
            return false;
        }
        TransactionStatus txnStatus = currTxnStatus.get();
        if (logger.isDebugEnabled()) {
            logger.debug("endTransaction(" + commit + ") on current(rollbackOnly=" + txnStatus.isRollbackOnly() + ")");
        }
        currTxnStatus.remove();
        if (commit) {
            tm.commit(txnStatus);
        } else {
            tm.rollback(txnStatus);
        }
        return true;
    }

    @Override
    public <T> T saveEntity(T entity) {
        if (((AbstractEntity) entity).getIdAsString() == null) {
            em.persist(entity);
        } else {
            try {
                entity = em.merge(entity);
            } catch (Exception e) {
                em.persist(entity);
            }
        }
        return entity;
    }

    @Override
    public <T> List<T> saveAllEntites(List<T> t) {
        List<T> result = new ArrayList<T>(t.size());
        for (T o : t) {
            result.add(saveEntity(o));
        }
        return result;
    }

    @Override
    public boolean removeEntity(Object entityInstance) {
        // 调用此方法前必须根据id查找到对应的entityInstance
        try {
            em.merge(entityInstance);
        } catch (Exception e) {
        }
        em.remove(entityInstance);
        return true;
    }

    @Override
    public int removeAllEntites(@SuppressWarnings("rawtypes") Collection entityInstances) {
        for (Object entityInstance : entityInstances) {
            remove(entityInstance);
        }
        return entityInstances.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T loadEntity(Class<T> entityClass, Object boId) {
        AbstractEntity entity = (AbstractEntity) em.find(entityClass, boId);
        if (entity != null) {
            entity.setBORepository(this);
        }
        return (T) entity;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <T> List<T> loadAllEntities(Class<T> entityClass, List boIds) {
        if (boIds == null) {
            StringBuilder query = new StringBuilder(128);
            query.append("SELECT a FROM ").append(entityClass.getSimpleName()).append(" a ");
            List<? extends AbstractEntity> result = (List<? extends AbstractEntity>) em
                    .createQuery(query.toString(), entityClass).getResultList();
            for (AbstractEntity ae : result) {
                ae.setBORepository(this);
            }
            return (List<T>) result;
        }
        List<T> result = new ArrayList<>(boIds.size());
        for (Object boId : boIds) {
            AbstractEntity entity = (AbstractEntity) em.find(entityClass, boId);
            if (entity != null) {
                entity.setBORepository(this);
            }
            result.add((T) entity);
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List searchEntity(Class entityClass, String searchExpr) {
        StringBuilder query = new StringBuilder(128);
        query.append("SELECT a FROM ").append(entityClass.getSimpleName()).append(" a ");
        if (!StringUtil.isEmpty(searchExpr)) {
            query.append("WHERE ").append(searchExpr);
        }
        List<? extends AbstractEntity> result = em.createQuery(query.toString(), entityClass).getResultList();
        for (AbstractEntity ae : result) {
            ae.setBORepository(this);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<Object[]> searchEntity(Class<T> entityClass, String searchExpr, String[] fields) {
        StringBuilder query = new StringBuilder(128);
        query.append("SELECT a FROM ").append(entityClass.getSimpleName()).append(" a ");
        boolean needsComma = false;
        for (String field : fields) {
            query.append(" a.").append(field);
            if (needsComma) {
                query.append(", ");
            } else {
                query.append(" ");
            }
            needsComma = true;
        }
        query.append(" FROM ").append(entityClass.getSimpleName()).append(" a ");
        if (!StringUtil.isEmpty(searchExpr)) {
            query.append("WHERE ").append(searchExpr);
        }
        List<Object[]> result = em.createQuery(query.toString()).getResultList();
        return result;
    }

}
