package com.caimi.service.repository;

/**
 * 可以根据缓存类型来进行查询
 */
public interface BOCacheKeeperMetaData {

    /**
     * 是否缓存所有对象，不完全缓存不支持search函数
     */
    public boolean supportsFullCache();

    /**
     * 是否支持search函数
     */
    public boolean supportsSearch();

    /**
     * 是否缓存了实体Id
     */
    public boolean supportsBOId();

    /**
     * 是否缓存了对象实例
     */
    public boolean supportsBOInstance();

    /**
     * 是否现在可以ReLoad
     */
    public boolean supportsReloadAll();

}
