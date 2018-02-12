package org.clarksnut.models.jpa.entity;

import org.clarksnut.common.jpa.CreatedAtListener;
import org.clarksnut.common.jpa.UpdatedAtListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cl_message", uniqueConstraints = {
        @UniqueConstraint(columnNames = "message_id"),
        @UniqueConstraint(columnNames = "provider")
}, indexes = {
        @Index(columnList = "message_id", unique = true),
        @Index(columnList = "provider", unique = true)
})
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
@NamedQueries({
        @NamedQuery(name = "getMessageByProviderAndMessageId", query = "select m from MessageEntity m where m.provider=:provider and m.messageId=:messageId")
})
public class MessageEntity {

    @Id
    @Access(AccessType.PROPERTY)
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @Column(name = "message_id")
    private String messageId;

    @NotNull
    @Column(name = "provider")
    private String provider;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id", foreignKey = @ForeignKey)
    private BrokerEntity broker;

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY)
    private Set<FileEntity> files = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Set<FileEntity> getFiles() {
        return files;
    }

    public void setFiles(Set<FileEntity> files) {
        this.files = files;
    }

    public BrokerEntity getBroker() {
        return broker;
    }

    public void setBroker(BrokerEntity broker) {
        this.broker = broker;
    }
}
