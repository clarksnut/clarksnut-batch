package org.clarksnut.models.jpa.entity;

import org.clarksnut.common.jpa.CreatedAtListener;
import org.clarksnut.common.jpa.UpdatedAtListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cl_message", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"message_id", "broker_id"})
})
@NamedQueries({
        @NamedQuery(name = "getMessageByMessageIdAndBrokerId", query = "select m from MessageEntity m inner join m.broker b where b.id=:brokerId and m.messageId=:messageId")
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "internal_date")
    private Date internalDate;

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

    public Date getInternalDate() {
        return internalDate;
    }

    public void setInternalDate(Date internalDate) {
        this.internalDate = internalDate;
    }

    public BrokerEntity getBroker() {
        return broker;
    }

    public void setBroker(BrokerEntity broker) {
        this.broker = broker;
    }

    public Set<FileEntity> getFiles() {
        return files;
    }

    public void setFiles(Set<FileEntity> files) {
        this.files = files;
    }
}
