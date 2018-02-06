package org.clarksnut.batchs.messages.broker;

import org.clarksnut.managers.BrokerManager;
import org.clarksnut.managers.BrokerModel;
import org.clarksnut.models.jpa.entity.UserEntity;
import org.clarksnut.models.jpa.entity.UserLinkedBrokerEntity;
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
        Map<BrokerModel, UserLinkedBrokerEntity> currentLinkedBrokers = new HashMap<>();
        for (UserLinkedBrokerEntity e : userEntity.getLinkedBrokers()) {
            currentLinkedBrokers.put(new BrokerModel(e.getType(), e.getEmail()), e);
        }


        Set<UserLinkedBrokerEntity> linkedBrokersToAdd = SetOps.difference(availableLinkedBrokers, currentLinkedBrokers.keySet())
                .stream()
                .map(broker -> {
                    UserLinkedBrokerEntity entity = new UserLinkedBrokerEntity();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setType(broker.getType());
                    entity.setEmail(broker.getEmail());
                    entity.setUser(userEntity);
                    return entity;
                })
                .collect(Collectors.toSet());

        Set<UserLinkedBrokerEntity> linkedBrokersToRemove = SetOps.difference(currentLinkedBrokers.keySet(), availableLinkedBrokers)
                .stream()
                .map(currentLinkedBrokers::get)
                .collect(Collectors.toSet());


        return new LinkedBrokersProcessed(linkedBrokersToAdd, linkedBrokersToRemove);
    }

}
