package org.clarksnut.batchs.messages.broker;

import org.clarksnut.models.jpa.entity.UserLinkedBrokerEntity;

import java.io.Serializable;
import java.util.Set;

public class LinkedBrokersProcessed implements Serializable {

    private final Set<UserLinkedBrokerEntity> added;
    private final Set<UserLinkedBrokerEntity> removed;

    public LinkedBrokersProcessed(Set<UserLinkedBrokerEntity> added, Set<UserLinkedBrokerEntity> removed) {
        this.added = added;
        this.removed = removed;
    }

    public Set<UserLinkedBrokerEntity> getAdded() {
        return added;
    }

    public Set<UserLinkedBrokerEntity> getRemoved() {
        return removed;
    }
}
