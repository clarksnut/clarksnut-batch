package org.clarksnut.models.jpa;

import org.clarksnut.models.BrokerModel;
import org.clarksnut.models.MessageModel;
import org.clarksnut.models.MessageProvider;
import org.clarksnut.models.jpa.entity.MessageEntity;
import org.hibernate.Session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Stateless
public class JpaMessageProvider extends AbstractHibernateProvider implements MessageProvider {

    @PersistenceContext
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public MessageModel addMessage(String messageId, Date messageDate, BrokerModel broker) {
        MessageEntity entity = new MessageEntity();

        entity.setId(UUID.randomUUID().toString());
        entity.setMessageId(messageId);
        entity.setMessageDate(messageDate);
        entity.setBroker(BrokerAdapter.toEntity(broker, em));

        Session session = getSession();
        session.persist(entity);
        return new MessageAdapter(session, entity);
    }

    @Override
    public MessageModel getMessage(String id) {
        Session session = getSession();

        MessageEntity entity = session.find(MessageEntity.class, id);
        if (entity == null) return null;
        return new MessageAdapter(session, entity);
    }

    @Override
    public MessageModel getUserByMessageIdAndBroker(String messageId, BrokerModel broker) {
        Session session = getSession();

        TypedQuery<MessageEntity> query = session.createNamedQuery("getMessageByMessageIdAndBrokerId", MessageEntity.class);
        query.setParameter("messageId", messageId);
        query.setParameter("brokerId", broker.getId());
        List<MessageEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return new MessageAdapter(session, entities.get(0));
    }

}
