package org.clarksnut.models.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.BrokerModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.jpa.entity.UserEntity;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UserAdapter implements UserModel, JpaModel<UserEntity> {

    private final EntityManager em;
    private final UserEntity user;

    public UserAdapter(EntityManager em, UserEntity user) {
        this.em = em;
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
                .map(f -> new BrokerAdapter(em, f))
                .collect(Collectors.toList());
    }

}
