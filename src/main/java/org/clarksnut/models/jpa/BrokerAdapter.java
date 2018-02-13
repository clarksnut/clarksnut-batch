package org.clarksnut.models.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.BrokerModel;
import org.clarksnut.models.BrokerType;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.jpa.entity.BrokerEntity;

import javax.persistence.EntityManager;
import java.util.Date;

public class BrokerAdapter implements BrokerModel, JpaModel<BrokerEntity> {

    private final EntityManager em;
    private final BrokerEntity broker;

    public BrokerAdapter(EntityManager em, BrokerEntity broker) {
        this.em = em;
        this.broker = broker;
    }

    public static BrokerEntity toEntity(BrokerModel model, EntityManager em) {
        if (model instanceof BrokerAdapter) {
            return ((BrokerAdapter) model).getEntity();
        }
        return em.getReference(BrokerEntity.class, model.getId());
    }

    @Override
    public BrokerEntity getEntity() {
        return broker;
    }

    @Override
    public String getId() {
        return broker.getId();
    }

    @Override
    public BrokerType getType() {
        return broker.getType();
    }

    @Override
    public String getEmail() {
        return broker.getEmail();
    }

    @Override
    public Date getLastTimeSynchronized() {
        return broker.getLastTimeSynchronized();
    }

    @Override
    public boolean isEnabled() {
        return broker.isEnabled();
    }

    @Override
    public UserModel getUser() {
        return new UserAdapter(em, broker.getUser());
    }

}
