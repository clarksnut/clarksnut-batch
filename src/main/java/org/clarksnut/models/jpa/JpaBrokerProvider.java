package org.clarksnut.models.jpa;

import org.clarksnut.models.BrokerModel;
import org.clarksnut.models.BrokerProvider;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.models.jpa.entity.BrokerEntity;
import org.clarksnut.models.jpa.entity.UserEntity;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

@RequestScoped
public class JpaBrokerProvider implements BrokerProvider {

    @PersistenceContext
    private EntityManager em;

    @Override
    public BrokerModel getBroker(String id) {
        BrokerEntity entity = em.find(BrokerEntity.class, id);
        if (entity == null) return null;
        return new BrokerAdapter(em, entity);
    }
}
