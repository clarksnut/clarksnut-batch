package org.clarksnut.models.jpa;

import org.clarksnut.models.FileModel;
import org.clarksnut.models.FileProvider;
import org.clarksnut.models.MessageModel;
import org.clarksnut.models.jpa.entity.FileEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class JpaFileProvider extends AbstractHibernateProvider implements FileProvider {

    @PersistenceContext
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public FileModel addFile(byte[] file, String filename, MessageModel message) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public FileModel getFile(String id) {


        FileEntity entity = em.find(FileEntity.class, id);
        if (entity == null) return null;
        return new FileAdapter(em, entity);
    }
}
