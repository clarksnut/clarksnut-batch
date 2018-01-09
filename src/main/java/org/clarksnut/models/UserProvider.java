package org.clarksnut.models;

import org.clarksnut.models.jpa.entity.UserEntity;

public interface UserProvider {

    /**
     * @param identityID   a unique identity string that the user generate
     * @param providerType provider of identityID
     * @param username     username of the user
     * @return user created
     */
    UserEntity addUser(String identityID, String providerType, String username, String offlineToken);

    /**
     * @param id auto generated unique identity
     * @return user, in case is not found return null
     */
    UserEntity getUser(String id);

    /**
     * @param username username of the user
     * @return user, in case is not found return null
     */
    UserEntity getUserByUsername(String username);

    /**
     * @param identityID a unique identity string that the user generate
     * @return user, in case is not found return null
     */
    UserEntity getUserByIdentityID(String identityID);

}
