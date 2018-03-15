package org.clarksnut.models.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.BrokerModel;
import org.clarksnut.models.BrokerType;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.jpa.entity.BrokerEntity;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.util.Date;

public class BrokerAdapter implements BrokerModel, JpaModel<BrokerEntity> {

    private final Session session;
    private final BrokerEntity broker;

    public BrokerAdapter(Session session, BrokerEntity broker) {
        this.session = session;
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
    public UserModel getUser() {
        return new UserAdapter(session, broker.getUser());
    }

    @Override
    public void setUser(UserModel user) {
        broker.setUser(UserAdapter.toEntity(user, session));
    }

    @Override
    public Date getCreatedAt() {
        return broker.getCreatedAt();
    }

    @Override
    public String getToken() {
        return broker.getRefreshToken();
    }

    @Override
    public void setToken(String token) {
        broker.setRefreshToken(token);
    }

}
