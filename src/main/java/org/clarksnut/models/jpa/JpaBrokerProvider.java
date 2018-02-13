package org.clarksnut.models.jpa;

import org.clarksnut.models.BrokerModel;
import org.clarksnut.models.BrokerProvider;
import org.clarksnut.models.jpa.entity.BrokerEntity;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
