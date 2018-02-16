package org.clarksnut.batchs.messages.broker;

import org.clarksnut.models.BrokerModel;
import org.clarksnut.models.BrokerProvider;
import org.clarksnut.models.jpa.BrokerAdapter;
import org.clarksnut.models.jpa.entity.BrokerEntity;
import org.clarksnut.models.jpa.entity.UserEntity;
import org.jberet.support.io.JpaItemWriter;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
public class RefreshLinkedBrokersWriter extends JpaItemWriter {

    private static final Logger logger = Logger.getLogger(RefreshLinkedBrokersWriter.class);

    @Inject
    private BrokerProvider brokerProvider;

    @Override
    public void writeItems(final List<Object> items) throws Exception {
        if (entityTransaction) {
            em.getTransaction().begin();
        }

        for (final Object e : items) {
            LinkedBrokersProcessed item = (LinkedBrokersProcessed) e;

            for (BrokerEntity brokerEntity : item.getAdded()) {
                BrokerModel broker = brokerProvider.getBrokerByEmail(brokerEntity.getEmail());
                if (broker == null) {
                    em.persist(brokerEntity);
                } else {
                    BrokerEntity brokerToUpdate = BrokerAdapter.toEntity(broker, em);
                    brokerToUpdate.setUser(item.getUser());
                    em.merge(brokerToUpdate);
                }
            }

            for (BrokerEntity brokerEntity : item.getRemoved()) {
                BrokerModel broker = brokerProvider.getBrokerByEmail(brokerEntity.getEmail());
                if (broker != null) {
                    BrokerEntity brokerToUpdate = BrokerAdapter.toEntity(broker, em);
                    UserEntity currentOwner = brokerToUpdate.getUser();
                    if (currentOwner != null && currentOwner.equals(item.getUser())) {
                        brokerToUpdate.setUser(item.getUser());
                    }
                    em.merge(brokerToUpdate);
                }
            }
        }

        if (entityTransaction) {
            em.getTransaction().commit();
        }
    }

}
