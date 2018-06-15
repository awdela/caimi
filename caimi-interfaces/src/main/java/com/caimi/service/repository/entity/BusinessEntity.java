package com.caimi.service.repository.entity;

import java.time.LocalDateTime;

import com.caimi.service.repository.BOEntity;

public interface BusinessEntity extends BOEntity{

	public static final int KEY_NAME    = 1;
    public static final int KEY_NO      = 2;
    
    public String getId();

    public String getName();

    public String getDesc();

    public void setName(String name);

    public String getLabels();

    public String getNo();

    public void setNo(String no);

    public int getStatus();

    public void setStatus(int status);

    public LocalDateTime getCreationTime();

    public void setCreationTime(LocalDateTime time);

    public LocalDateTime getLastActivityTime();

    public LocalDateTime touchActivityTime();

    
}