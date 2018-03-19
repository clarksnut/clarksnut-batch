package org.clarksnut.models.jpa.entity;

import org.clarksnut.models.BrokerType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cn_broker")
@NamedQueries({
        @NamedQuery(name = "getAllBrokersByUserId", query = "select b from BrokerEntity b inner join b.user u where u.id =:userId"),
        @NamedQuery(name = "getBrokerByEmail", query = "select b from BrokerEntity b where b.email =:email"),
        @NamedQuery(name = "batch_getAllEnableBrokers", query = "select b from BrokerEntity b where b.enable=true order by b.createdAt")
})
public class BrokerEntity implements Serializable {

    @Id
    @Access(AccessType.PROPERTY)
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @Column(name = "email")
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private BrokerType type;

    @NotNull
    @Size(max = 2048)
    @Column(name = "refresh_token", length = 2048)
    private String refreshToken;

    @NotNull
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "enable")
    private boolean enable;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey)
    private UserEntity user;

    @OneToMany(mappedBy = "broker", fetch = FetchType.LAZY)
    private Set<MessageEntity> messages = new HashSet<>();

    @Version
    @Column(name = "version")
    private int version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BrokerType getType() {
        return type;
    }

    public void setType(BrokerType type) {
        this.type = type;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Set<MessageEntity> getMessages() {
        return messages;
    }

    public void setMessages(Set<MessageEntity> messages) {
        this.messages = messages;
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
        if (!(obj instanceof BrokerEntity)) {
            return false;
        }
        BrokerEntity other = (BrokerEntity) obj;
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

