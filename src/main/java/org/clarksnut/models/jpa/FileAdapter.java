package org.clarksnut.models.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.FileModel;
import org.clarksnut.models.FileModel;
import org.clarksnut.models.jpa.entity.BrokerEntity;
import org.clarksnut.models.jpa.entity.FileEntity;
import org.hibernate.Session;

import javax.persistence.EntityManager;

public class FileAdapter implements FileModel, JpaModel<FileEntity> {

    private final Session session;
    private final FileEntity file;

    public FileAdapter(Session session, FileEntity file) {
        this.session = session;
        this.file = file;
    }

    public static FileEntity toEntity(FileModel model, EntityManager em) {
        if (model instanceof FileAdapter) {
            return ((FileAdapter) model).getEntity();
        }
        return em.getReference(FileEntity.class, model.getId());
    }

    @Override
    public FileEntity getEntity() {
        return file;
    }

    @Override
    public String getId() {
        return file.getId();
    }

    @Override
    public byte[] getBytes() {
        return file.getFile();
    }

}
