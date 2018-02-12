package org.clarksnut.batchs.messages.broker;

import org.clarksnut.models.jpa.entity.BrokerEntity;

import java.io.Serializable;
import java.util.Set;

public class LinkedBrokersProcessed implements Serializable {

    private final Set<BrokerEntity> added;
    private final Set<BrokerEntity> removed;

    public LinkedBrokersProcessed(Set<BrokerEntity> added, Set<BrokerEntity> removed) {
        this.added = added;
        this.removed = removed;
    }

    public Set<BrokerEntity> getAdded() {
        return added;
    }

    public Set<BrokerEntity> getRemoved() {
        return removed;
    }
}
