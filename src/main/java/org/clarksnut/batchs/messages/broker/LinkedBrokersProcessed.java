package org.clarksnut.batchs.messages.broker;

import org.clarksnut.models.jpa.entity.BrokerEntity;
import org.clarksnut.models.jpa.entity.UserEntity;

import java.io.Serializable;
import java.util.Set;

public class LinkedBrokersProcessed implements Serializable {

    private final UserEntity user;
    private final Set<BrokerEntity> added;
    private final Set<BrokerEntity> removed;

    public LinkedBrokersProcessed(UserEntity user, Set<BrokerEntity> added, Set<BrokerEntity> removed) {
        this.user = user;
        this.added = added;
        this.removed = removed;
    }

    public UserEntity getUser() {
        return user;
    }

    public Set<BrokerEntity> getAdded() {
        return added;
    }

    public Set<BrokerEntity> getRemoved() {
        return removed;
    }
}
