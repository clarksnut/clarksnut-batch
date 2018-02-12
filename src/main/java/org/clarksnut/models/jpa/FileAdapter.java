package org.clarksnut.models.jpa;

import org.clarksnut.models.FileModel;
import org.clarksnut.models.jpa.entity.FileEntity;

import javax.persistence.EntityManager;

public class FileAdapter implements FileModel {

    private final EntityManager em;
    private final FileEntity file;

    public FileAdapter(EntityManager em, FileEntity file) {
        this.em = em;
        this.file = file;
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
