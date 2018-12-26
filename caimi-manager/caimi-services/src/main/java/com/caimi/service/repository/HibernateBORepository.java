//package com.caimi.service.repository;
//
//import java.util.Collection;
//import java.util.List;
//
//import org.hibernate.SessionFactory;
//
///**
// * 基於Hibernate的BORepository
// */
//public class HibernateBORepository extends AbstractBORepository implements BOEntityAccessor {
//
//    private SessionFactory factory;
//    private BOCacheContainer container;
//
//    public HibernateBORepository(SessionFactory sessionFactory) {
//        this.factory = sessionFactory;
//    }
//
//    @Override
//    public <T> T loadEntity(Class<T> entityClass, Object boId) {
//        return null;
//    }
//
//    @Override
//    public <T> List<T> loadAllEntities(Class<T> entityClass, List boIds) {
//        return null;
//    }
//
//    @Override
//    public <T> T saveEntity(T t) {
//        return null;
//    }
//
//    @Override
//    public <T> List<T> saveAllEntites(List<T> t) {
//        return null;
//    }
//
//    @Override
//    public <T> List<T> searchEntity(Class<T> entityClass, String searcgExpr) {
//        return null;
//    }
//
//    @Override
//    public <T> List<Object[]> searchEntity(Class<T> entityClass, String searchExpr, String[] fields) {
//        return null;
//    }
//
//    @Override
//    public boolean removeEntity(Object o) {
//        return false;
//    }
//
//    @Override
//    public int removeAllEntites(Collection entityInstances) {
//        return 0;
//    }
//
//    @Override
//    public BOEntityAccessor getAccessor(Class entityClass) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    protected BOEntityAccessor getDefaultEntityAccessor() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public <T> void traverse(Class<T> boClass, String searchExpr, BOEntityVisitor<T> visitor) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void beginTransaction(boolean readOnly) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void endTransaction(boolean commit) {
//        // TODO Auto-generated method stub
//
//    }
//
//}
