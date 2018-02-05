package org.clarksnut.core;

import org.clarksnut.core.exceptions.StorageException;

public interface IStorage {

    /*
     * Transaction related methods
     */

    void beginTx() throws StorageException;

    void commitTx() throws StorageException;

    void rollbackTx();

    void initialize();

}
