package org.clarksnut.batchs.messages.broker;

import org.clarksnut.models.jpa.entity.BrokerEntity;
import org.jberet.support.io.JpaItemWriter;
import org.jboss.logging.Logger;

import javax.inject.Named;
import java.util.List;

@Named
public class RefreshLinkedBrokersWriter extends JpaItemWriter {

    private static final Logger logger = Logger.getLogger(RefreshLinkedBrokersWriter.class);

    @Override
    public void writeItems(final List<Object> items) throws Exception {
        if (entityTransaction) {
            em.getTransaction().begin();
        }
        for (final Object e : items) {
            LinkedBrokersProcessed item = (LinkedBrokersProcessed) e;
            for (BrokerEntity entity : item.getAdded()) {
                em.persist(entity);
            }
            for (BrokerEntity entity : item.getRemoved()) {
                em.remove(e);
            }
        }
        if (entityTransaction) {
            em.getTransaction().commit();
        }
    }

}
