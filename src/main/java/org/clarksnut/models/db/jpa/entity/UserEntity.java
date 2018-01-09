package org.clarksnut.models.db.jpa.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cl_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "identity_id")
}, indexes = {
        @Index(columnList = "username", unique = true),
        @Index(columnList = "identity_id", unique = true)
})
@NamedQueries({
        @NamedQuery(name = "getAllUsers", query = "select u from UserEntity u order by u.username"),
        @NamedQuery(name = "getUserByUsername", query = "select u from UserEntity u where u.username = :username"),
        @NamedQuery(name = "getUserByIdentityID", query = "select u from UserEntity u where u.identityID = :identityID"),
        @NamedQuery(name = "getUserWithOfflineToken", query = "select u from UserEntity u left join fetch u.linkedBrokers b where u.offlineToken is not null order by u.username")
})
public class UserEntity {

    @Id
    @Access(AccessType.PROPERTY)
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @Column(name = "identity_id")
    private String identityID;

    @NotNull
    @Column(name = "provider_type")
    private String providerType;

    @NotNull
    @Column(name = "username")
    private String username;

    @NotNull
    @Size(max = 2048)
    @Column(name = "offline_token", length = 2048)
    private String offlineToken;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserLinkedBrokerEntity> linkedBrokers = new HashSet<>();

    @Version
    @Column(name = "version")
    private int version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdentityID() {
        return identityID;
    }

    public void setIdentityID(String identityID) {
        this.identityID = identityID;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOfflineToken() {
        return offlineToken;
    }

    public void setOfflineToken(String offlineToken) {
        this.offlineToken = offlineToken;
    }

    public Set<UserLinkedBrokerEntity> getLinkedBrokers() {
        return linkedBrokers;
    }

    public void setLinkedBrokers(Set<UserLinkedBrokerEntity> linkedBrokers) {
        this.linkedBrokers = linkedBrokers;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
