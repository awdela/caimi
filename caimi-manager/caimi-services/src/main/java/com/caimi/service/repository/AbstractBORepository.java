package com.caimi.service.repository;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caimi.service.beans.BeansContainer;
import com.caimi.service.repository.BORepositoryChangeListener.Operation;
import com.caimi.service.repository.entity.AbstarctEntity;

@SuppressWarnings("rawtypes")
public abstract class AbstractBORepository implements BORepository, BOCacheContainer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractBORepository.class);

    private static final String ENTITY_SUFFIX = "Entity";
    private static final String CACHEKEEPER_SUFFIX = "CacheKeeper";
    private static final String IDGENERATOR_SUFFIX = "BusinessEntityIdGenerator";

    protected final Map<Class, EntityInfo> entityInfos = new HashMap<>();

    /**
     * 对每一个实体类的操作进行封装 后续通过类名在map中直接查找
     */
    protected static class EntityInfo {
        public Class interfacesClasses;
        // 判断id的类型(int or string)
        public Class idType;
        // 判断id的字段
        public Field idField;
        public String idPrefix;
        public Class<? extends AbstractEntity> entityClass;
        public BOCacheKeeper cacheKeeper;
        public BOEntityIdGenerator idGenerator;
        public BOEntityAccessor accessor;
        List<BORepositoryChangeListener> changeListeners = new ArrayList<>();
        private Instant lastUpdateTime;

        EntityInfo() {
            lastUpdateTime = Instant.now();
        }

        public Object getEntityId(Object entityInstance) {
            try {
                return idField.get(entityInstance);
            } catch (Throwable e) {
                logger.error("Get id for " + entityInstance + " failed", e);
            }
            return null;
        }

        public void touchLastUpdateTime() {
            lastUpdateTime = Instant.now();
        }

    }

    protected ReentrantLock updateLock = new ReentrantLock();
    private BeansContainer beansContainer;
    private Executor asyncexecutor;

    public AbstractBORepository() {

    }

    protected abstract BOEntityAccessor getDefaultEntityAccessor();

    public void setBeansContainer(BeansContainer beansContainer) {
        this.beansContainer = beansContainer;
    }

    public void setExecutor(Executor asyncExecutor) {
        this.asyncexecutor = asyncExecutor;
    }

    protected void init() {
        // 初始化所有的实体类,并将所有实体类放入entityInfos中
        initEntityClasses();
    }

    private void initEntityClasses() {
        try {
            List<String> packages = new ArrayList<>();
            packages.add("com.caimi.service.repository.entity");
            packages.add("com.caimi.service.repository.entity.cache");
            packages.add("com.caimi.service.repository.entity.accessor");
            // List<Map<String, String>> info = new ArrayList<>();
            EntityInfo entityInfo = loadEntityClassInfo(packages, "USER", BOEntity.ID_PREFIX_USER);
            entityInfos.put(entityInfo.entityClass, entityInfo);
        } catch (Exception e) {
        }
    }

    @SuppressWarnings("unchecked")
    private EntityInfo loadEntityClassInfo(List<String> packages, String InterfaceClassName, String ipPrefix)
            throws Exception {
        EntityInfo entityInfo = new EntityInfo();
        // user entityInfo
        String entityClass = InterfaceClassName + ENTITY_SUFFIX;
        String entityCacheKeeper = InterfaceClassName + CACHEKEEPER_SUFFIX;
        ClassLoader classLoader = getClass().getClassLoader();
        entityInfo.entityClass = loadClass(packages, entityClass, classLoader);
        Class entityCacheClass = loadClass(packages, entityCacheKeeper, classLoader);
        entityInfo.cacheKeeper = (BOCacheKeeper) entityCacheClass.newInstance();
        if (entityInfo.cacheKeeper instanceof BORepositoryChangeListener) {
            entityInfo.changeListeners.add((BORepositoryChangeListener) entityInfo.cacheKeeper);
        }
        Class entityIdGenerator = loadClass(packages, IDGENERATOR_SUFFIX, classLoader);
        if (entityIdGenerator != null) {
            entityInfo.idGenerator = (BOEntityIdGenerator) entityIdGenerator.newInstance();
        }
        entityInfo.accessor = getDefaultEntityAccessor();
        boolean foundId = false;
        Class currentEntityClass = entityInfo.entityClass;
        while (currentEntityClass != null) {
            // 获取实体类中所有字段
            for (Field field : currentEntityClass.getDeclaredFields()) {
                // 可以获取private字段
                field.setAccessible(true);
                // 获取字段中所有的注解
                Annotation[] annos = field.getAnnotations();
                if (annos != null && annos.length > 0) {
                    for (Annotation anno : annos) {
                        // 获取注解为id的字段作为
                        if (anno.annotationType() != Id.class) {
                            continue;
                        }
                        foundId = true;
                        break;
                    }
                }
                if (foundId) {
                    entityInfo.idField = field;
                    entityInfo.idType = field.getType();
                    break;
                }
            }
            if (foundId) {
                break;
            }
            // 如果没有找到就从父类中找
            currentEntityClass = currentEntityClass.getSuperclass();
        }
        if (!foundId) {
            throw new Exception("Entity " + InterfaceClassName + " has no id field");
        }
        return entityInfo;
    }

    @SuppressWarnings("unchecked")
    protected EntityInfo getEntityInfo(Class boClass) {
        EntityInfo result = entityInfos.get(boClass);
        if (result == null) {
            for (Map.Entry<Class, EntityInfo> entry : entityInfos.entrySet()) {
                Class clazz = entry.getKey();
                if (clazz.isAssignableFrom(boClass)) {
                    return entry.getValue();
                }
            }
        }
        if (result == null) {
            throw new RuntimeException("Entity class " + boClass + " is not registered in repository");
        }
        return result;
    }

    protected EntityInfo getEntityInfo(String entityClass) {
        for (Map.Entry<Class, EntityInfo> entry : entityInfos.entrySet()) {
            Class clazz = entry.getKey();
            if (entityClass.equals(clazz.getSimpleName())) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return beansContainer.getBean(clazz);
    }

    @Override
    public <T> T getBean(Class<T> clazz, String urposeOrId) {
        return beansContainer.getBean(clazz, urposeOrId);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return beansContainer.getBeansOfType(clazz);
    }

    @Override
    public BORepository getRepository() {
        return this;
    }

    // @Override
    // public BOEntityAccessor getAccessor(Class entityClass) {
    // return null;
    // }

    @SuppressWarnings("unchecked")
    @Override
    public <T> BOCacheKeeper<T> getCacheKeeper(Class<T> boClass) {
        EntityInfo entityInfo = getEntityInfo(boClass);
        return entityInfo.cacheKeeper;
    }

    @Override
    public Collection<Class> getBOClasses() {
        Set<Class> result = new TreeSet<>(new Comparator<Class>() {
            @Override
            public int compare(Class c0, Class c1) {
                return c0.getSimpleName().compareTo(c1.getSimpleName());
            }

        });
        for (EntityInfo entityInfo : entityInfos.values()) {
            result.addAll(Arrays.asList(entityInfo.interfacesClasses));
        }
        return result;
    }

    @Override
    public Class detectBOClassById(String idOrPrefix) {
        String ipPrefix = idOrPrefix;
        int underScoreIdx = idOrPrefix.indexOf("_");
        if (underScoreIdx > 0) {
            ipPrefix = idOrPrefix.substring(0, underScoreIdx);
        }
        for (EntityInfo entityInfo : entityInfos.values()) {
            if (ipPrefix.equals(entityInfo.idPrefix)) {
                return entityInfo.interfacesClasses;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Lock getLock(Class boClass) {
        Lock result = null;
        if (boClass != null) {
            BOCacheKeeper cacheKeeper = getCacheKeeper(boClass);
            if (cacheKeeper != null) {
                result = cacheKeeper.getLock();
            }
        }
        if (result == null) {
            result = updateLock;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> boClass, Object keyId) {
        EntityInfo entityInfo = getEntityInfo(boClass);
        // 判断keyId的类型
        Object boId = convertId(entityInfo, keyId);
        Class<T> entityClass = (Class<T>) entityInfo.entityClass;
        BOCacheKeeper cacheKeeper = entityInfo.cacheKeeper;
        T result = null;
        if (cacheKeeper != null) {
            // 从缓存中查询
            AbstractEntity r = (AbstractEntity) cacheKeeper.get(keyId);
            if (r != null) {
                r.setRepository(this);
            }
            result = (T) r;
            if (result == null) {
                // 从数据库中查询
                result = entityInfo.accessor.loadEntity(entityClass, boId);
                cacheKeeper.put(result);
            }
        } else {
            // 从数据库中查询
            result = entityInfo.accessor.loadEntity(entityClass, boId);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("get: " + boClass.getName() + " id " + keyId + " returns: " + result);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBy(Class<T> boClass, int key, Object keyId) {
        T result = null;
        EntityInfo entityInfo = getEntityInfo(boClass);
        BOCacheKeeper cacheKeeper = getCacheKeeper(boClass);
        if (cacheKeeper != null) {
            AbstarctEntity r = (AbstarctEntity) cacheKeeper.get(keyId);
            if (r != null) {
                r.setRepository(this);
            }
            result = (T) r;
        } else {
            result = (T) entityInfo.accessor.loadEntity(entityInfo.entityClass, keyId);
        }
        return result;
    }

    @Override
    public <T> Object getIdBy(Class<T> boClass, int key, Object keyId) {
        Object result = null;
        BOCacheKeeper cacheKeeper = getCacheKeeper(boClass);
        if (cacheKeeper != null) {
            result = cacheKeeper.getId(key, keyId);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getIdBy: " + boClass.getName() + " key " + key + " keyId " + keyId + " returns " + result);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void save(Object entity) {
        if (logger.isDebugEnabled()) {
            logger.debug("save : " + entity);
        }
        EntityInfo entityInfo = getEntityInfo(entity.getClass());
        // 先存数据库再存缓存
        entity = entityInfo.accessor.saveEntity(entity);
        BOCacheKeeper cacheKeeper = entityInfo.cacheKeeper;
        if (cacheKeeper != null) {
            cacheKeeper.put(entity);
        }
        Object boId = entityInfo.getEntityId(entity);

        // 异步通知
        publishChangeEvent(BORepositoryChangeListener.Operation.Update, entityInfo,
                Arrays.asList(new Object[] { boId }));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void saveAll(List<Object> entities) {
        if (logger.isDebugEnabled()) {
            logger.debug("save : " + entities);
        }
        EntityInfo entityInfo = getEntityInfo(entities.get(0).getClass());
        List<Object> entities0 = entityInfo.accessor.saveAllEntites(entities);
        BOCacheKeeper cacheKeeper = entityInfo.cacheKeeper;
        if (cacheKeeper != null) {
            cacheKeeper.putAll(entities0);
        }
        List<Object> boIds = new ArrayList<>(entities.size());
        for (Object boId : entities) {
            boIds.add(boId);
        }
        publishChangeEvent(BORepositoryChangeListener.Operation.Update, entityInfo, boIds);
    }

    @Override
    public void remove(Object entity) {
        if (logger.isDebugEnabled()) {
            logger.debug("remove : " + entity);
        }
        EntityInfo entityInfo = getEntityInfo(entity.getClass());
        Object boId = entityInfo.getEntityId(entity);
        BOCacheKeeper cacheKeeper = entityInfo.cacheKeeper;
        if (cacheKeeper != null) {
            cacheKeeper.remove(boId);
        }
        try {
            entity = entityInfo.accessor.removeEntity(entity);
        } finally {
            publishChangeEvent(BORepositoryChangeListener.Operation.Remove, entityInfo,
                    Arrays.asList(new Object[] { boId }));
        }
    }

    @Override
    public void remove(Class boClass, Object boId) {
        if (logger.isDebugEnabled()) {
            logger.debug("remove : " + boClass.getName() + boId);
        }
        EntityInfo entityInfo = getEntityInfo(boClass);
        BOCacheKeeper cacheKeeper = entityInfo.cacheKeeper;
        Object entity = null;
        if (cacheKeeper != null) {
            entity = cacheKeeper.remove(boId);
        }
        try {
            if (entity == null) {
                entity = get(boClass, boId);
            }
            if (entity != null) {
                entityInfo.accessor.removeEntity(entity);
            }
        } finally {
            publishChangeEvent(BORepositoryChangeListener.Operation.Remove, entityInfo,
                    Arrays.asList(new Object[] { boId }));
        }
    }

    @Override
    public void removeAll(Class boClass, Collection<Object> boIds) {
        if (logger.isDebugEnabled()) {
            logger.debug("remove : " + boClass.getName() + boIds);
        }
        EntityInfo entityInfo = getEntityInfo(boClass);
        BOCacheKeeper cacheKeeper = entityInfo.cacheKeeper;
        Object entity = null;
        try {
            for (Object boId : boIds) {
                if (cacheKeeper != null) {
                    entity = cacheKeeper.remove(boId);
                }
                if (entity == null) {
                    entity = get(boClass, boId);
                }
                if (entity != null) {
                    entityInfo.accessor.removeEntity(entity);
                }
            }
        } finally {
            publishChangeEvent(BORepositoryChangeListener.Operation.Remove, entityInfo, boIds);
        }
    }

    @Override
    public <T> List<T> search(Class<T> boClass, String searchExpr) {
        EntityInfo entityInfo = getEntityInfo(boClass);
        BOCacheKeeper cacheKeeper = entityInfo.cacheKeeper;
        if (cacheKeeper != null && cacheKeeper.getMetaData().supportsSearch()) {
            return cacheKeeper.search(boClass, searchExpr);
        }
        return entityInfo.accessor.searchEntity(boClass, searchExpr);
    }

    @Override
    public <T> List<Object[]> search(Class<T> boClass, String searchExpr, String[] fields) {
        EntityInfo entityInfo = getEntityInfo(boClass);
        BOCacheKeeper cacheKeeper = entityInfo.cacheKeeper;
        if (cacheKeeper != null && cacheKeeper.getMetaData().supportsSearch()) {
            return cacheKeeper.search(boClass, searchExpr, fields);
        }
        return entityInfo.accessor.searchEntity(boClass, searchExpr, fields);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> reloadAll(Class<T> boClass, List<Object> boIds) {
        if (logger.isDebugEnabled()) {
            logger.debug("reLoadAll " + boClass + " id: " + boIds);
        }
        if (boIds == null || boIds.isEmpty()) {
            logger.debug("ids is empty or null");
            return new ArrayList();
        }
        EntityInfo entityInfo = getEntityInfo(boClass);
        BOCacheKeeper cacheKeeper = entityInfo.cacheKeeper;
        List<T> data = new ArrayList<>();
        boolean reloadAll = false;
        if (boIds.contains(ID_STAR)) {
            reloadAll = true;
        }
        // 没有被缓存的不需要reload
        if (cacheKeeper != null) {
            Class<T> entityClass = (Class<T>) entityInfo.entityClass;
            if (reloadAll) {
                if (!cacheKeeper.getMetaData().supportsReloadAll()) {
                    throw new RuntimeException(
                            entityInfo.interfacesClasses.getSimpleName() + " can not reload all at this time");
                }
                cacheKeeper.reloadAll();
            } else {
                // 根据EntityInfo信息，转换boId为正确类型
                for (int index = 0; index < boIds.size(); index++) {
                    boIds.set(index, convertId(entityInfo, boIds.get(index)));
                }
                data = entityInfo.accessor.loadAllEntities(entityClass, boIds);
                cacheKeeper.reload(data);
                List boIdsLoaded = new ArrayList();
                for (T t : data) {
                    if (t == null) {
                        continue;
                    }
                    String id = ((BOEntity) t).getIdAsString();
                    boIdsLoaded.add(id);
                }
                boIds = boIdsLoaded;
            }
        }
        publishChangeEvent(reloadAll ? BORepositoryChangeListener.Operation.ReloadAll
                : BORepositoryChangeListener.Operation.Update, entityInfo, boIds);
        return data;
    }

    /**
     * 待实现
     */
    @Override
    public <T> void traverse(Class<T> boClass, String searchExpr, BOEntityVisitor<T> visitor) {
    };

    /**
     * 未实现
     */
    @Override
    public void asyncUpdate(Runnable asyncTask) {
        asyncexecutor.execute(() -> {

        });
    }

    @Override
    public <T> void registerChangeListener(Class<T> boClass, BORepositoryChangeListener<T> listener) {
        EntityInfo entityInfo = getEntityInfo(boClass);
        if (!entityInfo.changeListeners.contains(listener)) {
            entityInfo.changeListeners.add(listener);
        }
    }

    @Override
    public <T> void deregisterChangeListener(Class<T> boClass, BORepositoryChangeListener<T> listener) {
        EntityInfo entityInfo = getEntityInfo(boClass);
        if (entityInfo.changeListeners.contains(listener)) {
            entityInfo.changeListeners.remove(listener);
        }
    }

    protected void publishChangeEvent(BORepositoryChangeListener.Operation operation, EntityInfo entityInfo,
            Collection<Object> boIds) {
        entityInfo.touchLastUpdateTime();
        asyncNotifyListener(entityInfo, operation, boIds);
    }

    private void asyncNotifyListener(EntityInfo entityInfo, Operation operation, Collection<Object> boIds) {
        List<BORepositoryChangeListener> listeners = entityInfo.changeListeners;
        if (listeners == null) {
            return;
        }
        asyncexecutor.execute(() -> {
            // 多个注册了通知事件的
            for (BORepositoryChangeListener listener : listeners) {
                try {
                    listener.onChange(this, operation, entityInfo.entityClass, boIds);
                } catch (Throwable t) {
                    logger.error("Notify repository listener " + listener + " operation " + operation + " bo ids: "
                            + boIds + " failed" + t);
                }
            }
        });
    }

    private static Class loadClass(List<String> packages, String className, ClassLoader classLoader) throws Exception {
        if (packages.isEmpty()) {
            return null;
        }
        try {
            return Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException e) {
        }
        try {
            for (String package0 : packages) {
                return Class.forName(package0.trim() + "." + className, true, classLoader);
            }
        } catch (ClassNotFoundException e) {

        }
        throw new Exception("Repository class " + className + " is not found");
    }

    protected Object convertId(EntityInfo entityInfo, Object boId) {
        if (boId == null) {
            return null;
        }
        Class idType = entityInfo.idType;
        if (boId.getClass() == idType) {
            return boId;
        }
        if (idType == String.class) {
            return boId.toString();
        } else if (idType == int.class || idType == Integer.class) {
            if (boId instanceof Number) {
                return ((Number) boId).intValue();
            }
            return Integer.parseInt(boId.toString());
        } else if (idType == Long.class || idType == long.class) {
            return Long.parseLong(boId.toString());
        } else {
            return boId;
        }
    }
}
