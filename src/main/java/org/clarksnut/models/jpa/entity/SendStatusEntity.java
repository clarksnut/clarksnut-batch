package org.clarksnut.models.jpa.entity;

import org.clarksnut.models.SendStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "cn_send_status")
public class SendStatusEntity implements Serializable {

    @Id
    @Access(AccessType.PROPERTY)
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SendStatus status;

    @NotNull
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private AttachmentEntity attachment;

    @Version
    @Column(name = "version")
    private int version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SendStatus getStatus() {
        return status;
    }

    public void setStatus(SendStatus status) {
        this.status = status;
    }

    public AttachmentEntity getAttachment() {
        return attachment;
    }

    public void setAttachment(AttachmentEntity attachment) {
        this.attachment = attachment;
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
        if (!(obj instanceof SendStatusEntity)) {
            return false;
        }
        SendStatusEntity other = (SendStatusEntity) obj;
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
