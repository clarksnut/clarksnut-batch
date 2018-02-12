package org.clarksnut.batchs.messages.mail;

import org.clarksnut.models.jpa.entity.FileEntity;
import org.clarksnut.models.jpa.entity.BrokerEntity;
import org.jberet.support.io.JpaItemWriter;

import javax.inject.Named;
import java.util.List;
import java.util.Map;

@Named
public class PullMailMessagesWriter extends JpaItemWriter {

    @Override
    public void writeItems(List<Object> items) throws Exception {
        if (entityTransaction) {
            em.getTransaction().begin();
        }

        for (final Object item : items) {
            ((Map<BrokerEntity, List<FileEntity>>) item).forEach((key, value) -> {
                em.merge(key);
                for (FileEntity e : value) {
                    em.persist(e);
                }
            });
        }

        if (entityTransaction) {
            em.getTransaction().commit();
        }
    }
}
