package org.clarksnut.models.jpa.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cn_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "identity_id")
})
@NamedQueries({
        @NamedQuery(name = "getUserByUsername", query = "select u from UserEntity u where u.username=:username"),
        @NamedQuery(name = "getUserByIdentityID", query = "select u from UserEntity u where u.identityID=:identityID"),
        @NamedQuery(name = "batch_getAllUsers", query = "select u from UserEntity u left join fetch u.brokers l order by u.createdAt"),
        @NamedQuery(name = "batch_getAllUsersWithToken", query = "select u from UserEntity u left join fetch u.brokers l where u.token is not null order by u.createdAt")
})
public class UserEntity implements Serializable {

    @Id
    @Access(AccessType.PROPERTY)
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @Column(name = "identity_id")
    private String identityID;

    @NotNull
    @Column(name = "provider")
    private String provider;

    @NotNull
    @Column(name = "username")
    private String username;

    @Size(max = 2048)
    @Column(name = "token", length = 2048)
    private String token;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<BrokerEntity> brokers = new HashSet<>();

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

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Set<BrokerEntity> getBrokers() {
        return brokers;
    }

    public void setBrokers(Set<BrokerEntity> linkedBrokers) {
        this.brokers = linkedBrokers;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UserEntity)) {
            return false;
        }
        UserEntity other = (UserEntity) obj;
        if (id != null) {
            if (!id.equals(other.id)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
}
