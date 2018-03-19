package org.clarksnut.models.jpa.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cn_message", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"message_id", "broker_id"})
})
@NamedQueries({
        @NamedQuery(name = "getMessageByMessageIdAndBrokerId", query = "select m from MessageEntity m inner join m.broker b where b.id=:brokerId and m.messageId=:messageId"),
        @NamedQuery(name = "batch_getAllMessagesByBrokerId", query = "select m from MessageEntity m inner join m.broker b where b.id=:brokerId order by m.messageDate")
})
public class MessageEntity implements Serializable {

    @Id
    @Access(AccessType.PROPERTY)
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @Column(name = "message_id", length = 500)
    private String messageId;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "message_date")
    private Date messageDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id", foreignKey = @ForeignKey)
    private BrokerEntity broker;

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<AttachmentEntity> attachments = new HashSet<>();

    @Version
    @Column(name = "version")
    private int version;

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

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public BrokerEntity getBroker() {
        return broker;
    }

    public void setBroker(BrokerEntity broker) {
        this.broker = broker;
    }

    public Set<AttachmentEntity> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<AttachmentEntity> attachments) {
        this.attachments = attachments;
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
        if (!(obj instanceof MessageEntity)) {
            return false;
        }
        MessageEntity other = (MessageEntity) obj;
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
