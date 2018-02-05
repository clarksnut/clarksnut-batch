package org.clarksnut.core;

import org.clarksnut.core.exceptions.StorageException;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class JpaStorage extends AbstractJpaStorage implements IStorage {

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
