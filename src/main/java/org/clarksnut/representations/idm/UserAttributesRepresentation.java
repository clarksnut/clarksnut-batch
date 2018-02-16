package org.clarksnut.representations.idm;

import java.util.Date;
import java.util.List;

public class UserAttributesRepresentation {

    // The id of the corresponding User
    private String userID;

    // The id of the corresponding Identity
    private String identityID;

    // The date of creation of the user

    private Date createdAt;

    // The date of update of the user

    private Date updatedAt;

    // The username
    private String username;

    // Whether the registration has been completed
    private Boolean registrationCompleted;

    // The IDP provided this identity
    private String providerType;

    private List<BrokerRepresentation.Data> brokers;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getIdentityID() {
        return identityID;
    }

    public void setIdentityID(String identityID) {
        this.identityID = identityID;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getRegistrationCompleted() {
        return registrationCompleted;
    }

    public void setRegistrationCompleted(Boolean registrationCompleted) {
        this.registrationCompleted = registrationCompleted;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public List<BrokerRepresentation.Data> getBrokers() {
        return brokers;
    }

    public void setBrokers(List<BrokerRepresentation.Data> brokers) {
        this.brokers = brokers;
    }
}
