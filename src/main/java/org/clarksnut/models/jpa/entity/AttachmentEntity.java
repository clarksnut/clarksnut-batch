package org.clarksnut.models.jpa.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "cn_attachment")
@NamedQueries({
        @NamedQuery(name = "batch_getAllAttachmentsWithNoFile", query = "select a from AttachmentEntity a inner join a.file f join fetch a.message m join fetch m.broker b where f is null order by b.id"),
        @NamedQuery(name = "batch_getAllAttachmentsWithFileAndPendingSendStatus", query = "select a from AttachmentEntity a join fetch a.sendStatus s join fetch a.file f join fetch a.message m join fetch m.broker b where s.status = org.clarksnut.models.SendStatus.WAITING_FOR_SEND and f is not null order by b.id"),
})
public class AttachmentEntity implements Serializable {

    @Id
    @Access(AccessType.PROPERTY)
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @Column(name = "attachment_id")
    private String attachmentId;

    @NotNull
    @Column(name = "filename")
    private String filename;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", foreignKey = @ForeignKey)
    private MessageEntity message;

    @OneToOne(mappedBy = "attachment", fetch = FetchType.LAZY)
    private FileEntity file;

    @OneToOne(mappedBy = "attachment", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private SendStatusEntity sendStatus;

    @Version
    @Column(name = "version")
    private int version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public MessageEntity getMessage() {
        return message;
    }

    public void setMessage(MessageEntity message) {
        this.message = message;
    }

    public FileEntity getFile() {
        return file;
    }

    public void setFile(FileEntity file) {
        this.file = file;
    }

    public SendStatusEntity getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(SendStatusEntity sendStatus) {
        this.sendStatus = sendStatus;
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
        if (!(obj instanceof AttachmentEntity)) {
            return false;
        }
        AttachmentEntity other = (AttachmentEntity) obj;
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
