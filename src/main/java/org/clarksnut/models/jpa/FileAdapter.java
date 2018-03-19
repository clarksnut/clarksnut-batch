package org.clarksnut.models.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.FileModel;
import org.clarksnut.models.jpa.entity.FileEntity;

import javax.persistence.EntityManager;

public class FileAdapter implements FileModel, JpaModel<FileEntity> {

    private final EntityManager em;
    private final FileEntity file;

    public FileAdapter(EntityManager em, FileEntity file) {
        this.em = em;
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
