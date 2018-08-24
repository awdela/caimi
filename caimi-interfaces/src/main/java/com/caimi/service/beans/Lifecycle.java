package com.caimi.service.beans;

public interface Lifecycle {

    public void init(BeansContainer beansContainer);

    public void destroy();

}
