package org.clarksnut.models.jpa;

import org.clarksnut.models.BrokerModel;
import org.clarksnut.models.BrokerProvider;
import org.clarksnut.models.BrokerType;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.jpa.entity.BrokerEntity;
import org.hibernate.Session;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Stateless
public class JpaBrokerProvider extends AbstractHibernateProvider implements BrokerProvider {

    @PersistenceContext
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public BrokerModel addBroker(UserModel user, BrokerType type, String email) {
        BrokerEntity entity = new BrokerEntity();

        entity.setId(UUID.randomUUID().toString());
        entity.setType(type);
        entity.setEmail(email);
        entity.setCreatedAt(Calendar.getInstance().getTime());
        entity.setUser(UserAdapter.toEntity(user, em));

        Session session = getSession();
        session.persist(entity);
        return new BrokerAdapter(session, entity);
    }

    @Override
    public BrokerModel getBroker(String id) {
        Session session = getSession();

        BrokerEntity entity = session.find(BrokerEntity.class, id);
        if (entity == null) return null;
        return new BrokerAdapter(session, entity);
    }

    @Override
    public BrokerModel getBrokerByEmail(String email) {
        Session session = getSession();

        TypedQuery<BrokerEntity> query = session.createNamedQuery("getBrokerByEmail", BrokerEntity.class);
        query.setParameter("email", email);
        List<BrokerEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return new BrokerAdapter(session, entities.get(0));
    }

    @Override
    public List<BrokerModel> getBrokers(UserModel user) {
        Session session = getSession();

        TypedQuery<BrokerEntity> query = session.createNamedQuery("getAllBrokersByUserId", BrokerEntity.class);
        query.setParameter("userId", user.getId());
        return query.getResultList().stream()
                .map(f -> new BrokerAdapter(session, f))
                .collect(Collectors.toList());

    }

}
