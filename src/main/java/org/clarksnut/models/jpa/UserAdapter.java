package org.clarksnut.models.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.jpa.entity.UserEntity;

import javax.persistence.EntityManager;

public class UserAdapter implements UserModel, JpaModel<UserEntity>{

    private final EntityManager em;
    private final UserEntity user;

    public UserAdapter(EntityManager em, UserEntity user) {
        this.em = em;
        this.user = user;
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
    public String getProviderType() {
        return user.getProviderType();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getOfflineToken() {
        return user.getOfflineToken();
    }

    @Override
    public void setOfflineToken(String offlineToken) {
        user.setOfflineToken(offlineToken);
    }
}
