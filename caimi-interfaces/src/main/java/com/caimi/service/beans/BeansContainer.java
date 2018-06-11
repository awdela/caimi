package com.caimi.service.beans;

import java.util.Map;

/**
 * java对象容器接口，函数从Spring ApplicationContext复制过来
 *
 */
public interface BeansContainer {
	
	public<T> T getBean(Class<T> clazz);
	
	public<T> T getBean(Class<T> clazz, String urposeOrId);
	
	public<T> Map<String, T> getBeansOfType(Class<T> clazz);

}
