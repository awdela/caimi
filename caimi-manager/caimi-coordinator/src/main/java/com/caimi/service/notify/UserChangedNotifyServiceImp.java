package com.caimi.service.notify;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caimi.service.repository.BORepository;
import com.caimi.service.repository.BORepositoryChangeListener;
import com.caimi.service.repository.BORepositoryChangeListener.Operation;
import com.caimi.service.repository.entity.UserEntity;

/**
 * this is a simple demo
 */
@Service
public class UserChangedNotifyServiceImp {

    @Autowired
    private BORepository repository;

    @PostConstruct
    public void init() {
        repository.registerChangeListener(UserEntity.class, new BORepositoryChangeListener<UserEntity>() {

            @Override
            public void onChange(BORepository repository, Operation oper, Class<UserEntity> boClass,
                    Collection<Object> boIds) {
                userChange(oper, boIds);
            }

        });
    }

    // 用户名一有改变就调用此方法
    private void userChange(Operation oper, Collection<Object> boIds) {

    }

}
