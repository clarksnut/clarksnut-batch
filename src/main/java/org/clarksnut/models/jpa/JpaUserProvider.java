package org.clarksnut.models.jpa;

import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.models.jpa.entity.UserEntity;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

@RequestScoped
public class JpaUserProvider implements UserProvider {

    @PersistenceContext
    private EntityManager em;

    @Override
    public UserModel addUser(String identityID, String providerType, String username) {
        UserEntity entity = new UserEntity();

        entity.setId(UUID.randomUUID().toString());
        entity.setIdentityID(identityID);
        entity.setProviderType(providerType);
        entity.setUsername(username);

        em.persist(entity);
        return new UserAdapter(em, entity);
    }

    @Override
    public UserModel getUser(String userId) {
        UserEntity entity = em.find(UserEntity.class, userId);
        if (entity == null) return null;
        return new UserAdapter(em, entity);
    }

    @Override
    public UserModel getUserByUsername(String username) {
        TypedQuery<UserEntity> query = em.createNamedQuery("getUserByUsername", UserEntity.class);
        query.setParameter("username", username);
        List<UserEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return new UserAdapter(em, entities.get(0));
    }

    @Override
    public UserModel getUserByIdentityID(String identityID) {
        TypedQuery<UserEntity> query = em.createNamedQuery("getUserByIdentityID", UserEntity.class);
        query.setParameter("identityID", identityID);
        List<UserEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return new UserAdapter(em, entities.get(0));
    }
}
