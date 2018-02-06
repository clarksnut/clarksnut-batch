package org.clarksnut.models.jpa.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cl_group_file")
@NamedQueries({
        @NamedQuery(name = "getAllNotSentGroupFiles", query = "select g from GroupFileEntity g where g.sended = false")
})
public class GroupFileEntity implements Serializable {

    @Id
    @Access(AccessType.PROPERTY)
    @Column(name = "id", length = 36)
    private String id;

    @Type(type = "org.hibernate.type.YesNoType")
    @Column(name = "sended")
    private boolean sended;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private Set<GroupFileSendEventEntity> sendEvents = new HashSet<>();

    @Version
    @Column(name = "version")
    private int version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSended() {
        return sended;
    }

    public void setSended(boolean sended) {
        this.sended = sended;
    }

    public Set<GroupFileSendEventEntity> getSendEvents() {
        return sendEvents;
    }

    public void setSendEvents(Set<GroupFileSendEventEntity> sendEvents) {
        this.sendEvents = sendEvents;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
