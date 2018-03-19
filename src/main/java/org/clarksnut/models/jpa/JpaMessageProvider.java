package org.clarksnut.models.jpa;

import org.clarksnut.models.BrokerModel;
import org.clarksnut.models.MessageModel;
import org.clarksnut.models.MessageProvider;
import org.clarksnut.models.jpa.entity.MessageEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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


        em.persist(entity);
        return new MessageAdapter(em, entity);
    }

    @Override
    public MessageModel getMessage(String id) {


        MessageEntity entity = em.find(MessageEntity.class, id);
        if (entity == null) return null;
        return new MessageAdapter(em, entity);
    }

    @Override
    public MessageModel getUserByMessageIdAndBroker(String messageId, BrokerModel broker) {
        TypedQuery<MessageEntity> query = em.createNamedQuery("getMessageByMessageIdAndBrokerId", MessageEntity.class);
        query.setParameter("messageId", messageId);
        query.setParameter("brokerId", broker.getId());
        List<MessageEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return new MessageAdapter(em, entities.get(0));
    }

}
