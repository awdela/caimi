package com.caimi.util;

/**
 * 运行时信息
 */
public interface RuntimeInfo extends JSONEnabled {

    /**
     * 唯一id
     */
    public String getId();

    /**
     * 优先级
     */
    public int getPriority();

    /**
     * 是否禁用
     */
    // public boolean isDisabled();

    /**
     * 最后匹配时间
     */
    public long getLastMatchTime();

    /**
     * 累计匹配次数
     */
    public long getTotalMatchSize();

    /**
     * 错误原因
     */
    public String getErrorReason();

}
