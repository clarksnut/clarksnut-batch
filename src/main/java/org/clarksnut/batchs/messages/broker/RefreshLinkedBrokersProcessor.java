package org.clarksnut.batchs.messages.broker;

import org.clarksnut.managers.BrokerManager;
import org.clarksnut.managers.BrokerModel;
import org.clarksnut.models.jpa.entity.UserEntity;
import org.clarksnut.models.jpa.entity.BrokerEntity;
import org.clarksnut.models.utils.SetOps;
import org.jboss.logging.Logger;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Named
public class RefreshLinkedBrokersProcessor implements ItemProcessor {

    private static final Logger logger = Logger.getLogger(RefreshLinkedBrokersProcessor.class);

    @Inject
    private BrokerManager brokerManager;

    @Override
    public LinkedBrokersProcessed processItem(Object item) throws Exception {
        UserEntity userEntity = (UserEntity) item;

        Set<BrokerModel> availableLinkedBrokers = brokerManager.getLinkedBrokers(userEntity.getOfflineToken());
        Map<BrokerModel, BrokerEntity> currentLinkedBrokers = new HashMap<>();
        for (BrokerEntity e : userEntity.getLinkedBrokers()) {
            currentLinkedBrokers.put(new BrokerModel(e.getType(), e.getEmail()), e);
        }


        Set<BrokerEntity> linkedBrokersToAdd = SetOps.difference(availableLinkedBrokers, currentLinkedBrokers.keySet())
                .stream()
                .map(broker -> {
                    BrokerEntity entity = new BrokerEntity();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setType(broker.getType());
                    entity.setEmail(broker.getEmail());
                    entity.setEnabled(true);
                    entity.setUser(userEntity);
                    return entity;
                })
                .collect(Collectors.toSet());

        Set<BrokerEntity> linkedBrokersToRemove = SetOps.difference(currentLinkedBrokers.keySet(), availableLinkedBrokers)
                .stream()
                .map(currentLinkedBrokers::get)
                .peek(c -> c.setEnabled(false))
                .collect(Collectors.toSet());


        return new LinkedBrokersProcessed(linkedBrokersToAdd, linkedBrokersToRemove);
    }

}
