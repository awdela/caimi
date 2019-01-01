package com.caimi.service.repository.entity;

import java.time.LocalDateTime;

import com.caimi.service.repository.BOEntity;

/**
 * 实现此接口的entity互相有从属关系
 */
public interface BusinessEntity extends BOEntity{

	public static final int KEY_NAME    = 1;
    public static final int KEY_NO      = 2;

    public String getId();

    public String getName();

    public void setName(String name);
    
    public String getDesc();
    
    public String getLabels();

    public String getNo();
    
    public String getGroup();

	public String getAttrs();
	
    public String getAttr(String attr);

    public void setAttr(String attr, String value);
    
    public String getAttr();

    public int getStatus();

    public LocalDateTime getCreationTime();

    public void setCreationTime(LocalDateTime time);

    public LocalDateTime getLastActivityTime();

    public LocalDateTime touchActivityTime();

}
