package org.clarksnut.models.jpa;

import org.clarksnut.models.*;
import org.clarksnut.models.jpa.entity.FileEntity;
import org.clarksnut.models.jpa.entity.UserEntity;
import org.hibernate.Session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

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
        Session session = getSession();

        FileEntity entity = session.find(FileEntity.class, id);
        if (entity == null) return null;
        return new FileAdapter(session, entity);
    }
}
