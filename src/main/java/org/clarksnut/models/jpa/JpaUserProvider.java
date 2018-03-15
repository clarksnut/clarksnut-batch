package org.clarksnut.models.jpa;

import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.models.jpa.entity.UserEntity;
import org.hibernate.Session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Stateless
public class JpaUserProvider extends AbstractHibernateProvider implements UserProvider {

    @PersistenceContext
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public UserModel addUser(String identityID, String providerType, String username) {
        UserEntity entity = new UserEntity();

        entity.setId(UUID.randomUUID().toString());
        entity.setIdentityID(identityID);
        entity.setProvider(providerType);
        entity.setUsername(username);
        entity.setCreatedAt(Calendar.getInstance().getTime());

        Session session = getSession();
        session.persist(entity);
        return new UserAdapter(session, entity);
    }

    @Override
    public UserModel getUser(String userId) {
        Session session = getSession();

        UserEntity entity = session.find(UserEntity.class, userId);
        if (entity == null) return null;
        return new UserAdapter(session, entity);
    }

    @Override
    public UserModel getUserByUsername(String username) {
        Session session = getSession();

        TypedQuery<UserEntity> query = session.createNamedQuery("getUserByUsername", UserEntity.class);
        query.setParameter("username", username);
        List<UserEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return new UserAdapter(session, entities.get(0));
    }

    @Override
    public UserModel getUserByIdentityID(String identityID) {
        Session session = getSession();

        TypedQuery<UserEntity> query = session.createNamedQuery("getUserByIdentityID", UserEntity.class);
        query.setParameter("identityID", identityID);
        List<UserEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return new UserAdapter(session, entities.get(0));
    }

}
