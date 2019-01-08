package com.caimi.service.cache;

import java.util.List;

/**
 * Redis单机版
 */
public interface RedisCacheManager<K, V> {

    // 放入缓存
    public boolean set(K key, V entity);

    // 放入缓存并设置时间
    public boolean set(K key, V entity, long time);

    public V get(K key);

    // 获取所有的entity
    public List<Object> hvals(K hkey);

    public boolean hset(K hkey, K key, V entity);

    // 获取存储结构是hashMap类型的操作
    public V hget(K hkey, K entity);

    public long decr(K key, long delta);

    public long incr(K key);

    // 设置缓存时间
    public boolean expire(K key, long time);

    // 全部删除
    public void del(K key);

    // 删除一条数据
    public void hdel(K hkey, K key);

    // 判断hash表中是否有该项的值
    public boolean hHasKey(K hkey, K key);

    public boolean getStatus();

}
