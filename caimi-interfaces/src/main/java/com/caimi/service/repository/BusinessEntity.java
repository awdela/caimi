package com.caimi.service.repository;

import java.time.LocalDateTime;

public interface BusinessEntity {
	
	public String getId();
	
	public String getName();
	
	public void setName(String name);
	
	public String getDesc();
	
	public void setDesc(String desc);

	public int getStatus();
	
	public void setStatus(int status);
	
	public String getNo();
	
	public void setNo(String no);
	
	public String getLables();
	
	public void setLables(String lables);
	
	public String getGroup();
	
	public void setGroup(String group);
	
	public String getAttrs();

    public String getAttr(String attr);

    public void setAttr(String attr, String value);
	
    public LocalDateTime getCreationTime();

    public void setCreationTime(LocalDateTime time);

    public LocalDateTime getLastActivityTime();

    public LocalDateTime touchActivityTime();
    
}
