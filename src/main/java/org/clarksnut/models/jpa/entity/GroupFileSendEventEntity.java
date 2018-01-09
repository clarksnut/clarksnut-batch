package org.clarksnut.models.jpa.entity;

import org.clarksnut.models.SendResult;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "cl_send_event")
public class GroupFileSendEventEntity {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "send_date")
    private Date sendDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private SendResult result;

    @NotNull
    @Column(name = "message")
    private String message;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "send_event_id", foreignKey = @ForeignKey)
    private GroupFileEntity group;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public SendResult getResult() {
        return result;
    }

    public void setResult(SendResult result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GroupFileEntity getGroup() {
        return group;
    }

    public void setGroup(GroupFileEntity group) {
        this.group = group;
    }
}
