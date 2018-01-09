package org.clarksnut.models.jpa;

import org.clarksnut.models.jpa.entity.UserEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

@Stateless
public class JpaUserProvider implements org.clarksnut.models.UserProvider {

    @PersistenceContext
    private EntityManager em;

    @Override
    public UserEntity addUser(String identityID, String providerType, String username, String offlineToken) {
        UserEntity entity = new UserEntity();

        entity.setId(UUID.randomUUID().toString());
        entity.setIdentityID(identityID);
        entity.setProviderType(providerType);
        entity.setUsername(username);
        entity.setOfflineToken(offlineToken);

        em.persist(entity);
        return entity;
    }

    @Override
    public UserEntity getUser(String userId) {
        return em.find(UserEntity.class, userId);
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        TypedQuery<UserEntity> query = em.createNamedQuery("getUserByUsername", UserEntity.class);
        query.setParameter("username", username);
        List<UserEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return entities.get(0);
    }

    @Override
    public UserEntity getUserByIdentityID(String identityID) {
        TypedQuery<UserEntity> query = em.createNamedQuery("getUserByIdentityID", UserEntity.class);
        query.setParameter("identityID", identityID);
        List<UserEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return entities.get(0);
    }
}
