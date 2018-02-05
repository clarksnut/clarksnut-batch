package org.clarksnut.core;

import org.clarksnut.core.exceptions.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class JpaStorage extends AbstractJpaStorage implements IStorage {

    private static Logger logger = LoggerFactory.getLogger(JpaStorage.class);

    /**
     * Constructor.
     */
    public JpaStorage() {
    }

    @Override
    public void beginTx() throws StorageException {
        super.beginTx();
    }

    @Override
    public void commitTx() throws StorageException {
        super.commitTx();
    }

    @Override
    public void rollbackTx() {
        super.rollbackTx();
    }

    @Override
    public void initialize() {
        // No-Op for JPA
    }

}
