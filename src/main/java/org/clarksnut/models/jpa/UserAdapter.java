package org.clarksnut.models.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.BrokerModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.jpa.entity.UserEntity;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UserAdapter implements UserModel, JpaModel<UserEntity> {

    private final Session session;
    private final UserEntity user;

    public UserAdapter(Session session, UserEntity user) {
        this.session = session;
        this.user = user;
    }

    public static UserEntity toEntity(UserModel model, EntityManager em) {
        if (model instanceof UserAdapter) {
            return ((UserAdapter) model).getEntity();
        }
        return em.getReference(UserEntity.class, model.getId());
    }

    @Override
    public UserEntity getEntity() {
        return user;
    }

    @Override
    public String getId() {
        return user.getId();
    }

    @Override
    public String getIdentityID() {
        return user.getIdentityID();
    }

    @Override
    public void setIdentityID(String identityID) {
        user.setIdentityID(identityID);
    }

    @Override
    public String getProvider() {
        return user.getProvider();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getToken() {
        return user.getToken();
    }

    @Override
    public void setToken(String token) {
        user.setToken(token);
    }

    @Override
    public Date getCreatedAt() {
        return user.getCreatedAt();
    }

    @Override
    public List<BrokerModel> getLinkedBrokers() {
        return user.getBrokers().stream()
                .map(f -> new BrokerAdapter(session, f))
                .collect(Collectors.toList());
    }

}
