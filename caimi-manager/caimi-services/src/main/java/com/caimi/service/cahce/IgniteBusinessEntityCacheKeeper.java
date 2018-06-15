package com.caimi.service.cahce;

import java.util.List;

import com.caimi.service.repository.BOEntity;
import com.caimi.service.repository.entity.AbstractBusinessEntity;

public abstract class IgniteBusinessEntityCacheKeeper<T extends AbstractBusinessEntity> extends IgniteCacheKeeper<T> {

	public IgniteBusinessEntityCacheKeeper(Class<T> mainClasses, String keyCacheLoadNode, String igniteLockName) {
		super(mainClasses, keyCacheLoadNode, igniteLockName);
	}

	@Override
	protected T get0(int key, Object keyId) {
		switch (key) {
		case KEY_ID:
		{
			T result = getMainCache().get(keyId);
			return result;
		}
		case AbstractBusinessEntity.KEY_NAME:
		{
			List<T> result = search(BOEntity.class, "name='"+keyId+"'");
			if (result.size()>0) {
				return result.get(0);
			}
			return null;
		}
		case AbstractBusinessEntity.KEY_NO:
		{
			List<T> result = search(BOEntity.class, "no='"+keyId+"'");
			if (result.size()>0) {
				return result.get(0);
			}
			return null;
		}
		default:
			return null;
		}

	}

	@Override
	protected Object getId0(int key, Object keyId) {
		AbstractBusinessEntity result = get(key, keyId);
		if (result != null) {
			return result;
		}
		return null;
	}

}
