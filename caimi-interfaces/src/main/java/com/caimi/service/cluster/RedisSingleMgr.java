package com.caimi.service.cluster;

/**
 * Redis单机版
 */
public interface RedisSingleMgr {

    String get(String key);

    String set(String key, String value);

    // 获取存储结构是hashMap类型的操作
    String hget(String hkey, String key);

    long hset(String hkey, String key, String value);

    long incr(String key);

    // 设置缓存时间
    long expire(String key, int second);

    // 删除数据
    long del(String key);

    long hdel(String hkey, String key);

}
