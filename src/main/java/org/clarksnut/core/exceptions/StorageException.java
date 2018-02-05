package org.clarksnut.core.exceptions;

/**
 * Thrown if a storage problem occurs while storing a bean.
 */
public class StorageException extends Exception {

    private static final long serialVersionUID = -2331516263436545223L;
    
    /**
     * Constructor.
     */
    public StorageException() {
    }
    
    /**
     * Constructor.
     * @param message the exception message
     */
    public StorageException(String message) {
        super(message);
    }
    
    /**
     * Constructor.
     * @param cause the exception cause the exception cause
     */
    public StorageException(Throwable cause) {
        super(cause);
    }

}
