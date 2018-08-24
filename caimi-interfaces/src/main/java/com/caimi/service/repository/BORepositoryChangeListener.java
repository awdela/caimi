package com.caimi.service.repository;

import java.util.Collection;

/**
 * 业务数据
 */
@FunctionalInterface
public interface BORepositoryChangeListener<T> {

    public static enum Operation{
        /**
         * 新增或更新
         */
        Update
        /**
         * 删除
         */
        ,Remove
        /**
         * 全部加载
         */
        ,ReloadAll
        
    }
    
    public void onChange(BORepository repository, Operation oper, Class<T> boClass, final Collection<Object> boIds);
}
