package org.clarksnut.core;

import org.clarksnut.core.exceptions.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.RollbackException;

public abstract class AbstractJpaStorage {

    private static Logger logger = LoggerFactory.getLogger(AbstractJpaStorage.class);

    @PersistenceContext
    private EntityManager entityManager;

    protected void beginTx() throws StorageException {
        entityManager.getTransaction().begin();
    }

    protected void commitTx() throws StorageException {
        try {
            entityManager.getTransaction().commit();
        } catch (EntityExistsException e) {
            throw new StorageException(e);
        } catch (RollbackException e) {
            logger.error(e.getMessage(), e);
            throw new StorageException(e);
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            throw new StorageException(t);
        }
    }

    protected void rollbackTx() {
        if (entityManager.getTransaction().isActive()/* && entityManager.getTransaction().getRollbackOnly()*/) {
            try {
                entityManager.getTransaction().rollback();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
