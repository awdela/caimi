package com.caimi.service.repository;

import java.time.LocalDateTime;

public interface BusinessEntity {
	
    public LocalDateTime getCreationTime();

    public void setCreationTime(LocalDateTime time);

    public LocalDateTime getLastActivityTime();

    public LocalDateTime touchActivityTime();
    
}
