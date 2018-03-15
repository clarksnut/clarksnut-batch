package org.clarksnut.models.jpa;

import org.hibernate.Session;

import javax.persistence.EntityManager;

public abstract class AbstractHibernateProvider {

    protected abstract EntityManager getEntityManager();

    protected Session getSession() {
        return getEntityManager().unwrap(Session.class);
    }

}
