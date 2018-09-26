package com.caimi.service.repository;

/**
 * entity 遍历的会调接口 现未实现
 */
public interface BOEntityVisitor<T> {

    /**
     * entity对象回调函数
     *
     * @return true 继续, false 退出
     */
    public boolean visit(T t);
}
