package org.clarksnut.common.jpa;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RequestScoped
public class EntityManagerFactory {

    @PersistenceContext
    private EntityManager em;

    @Produces
    public EntityManager createEntityManager() {
        return em;
    }

}
