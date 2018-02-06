package org.clarksnut.batchs.messages.groupfiles;

import org.apache.commons.collections4.ListUtils;
import org.clarksnut.models.jpa.entity.FileEntity;
import org.clarksnut.models.jpa.entity.GroupFileEntity;
import org.jberet.support.io.JpaItemWriter;
import org.jboss.logging.Logger;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Named
public class GroupFilesWriter extends JpaItemWriter {

    private static final Logger logger = Logger.getLogger(GroupFilesWriter.class);

    @Inject
    @ConfigurationValue("clarksnut.scheduler.send.size")
    private Optional<Integer> clarksnutSchedulerSendSize;

    @Override
    public void writeItems(final List<Object> items) throws Exception {
        List<FileEntity> fileEntities = items.stream().map(f -> (FileEntity) f).collect(Collectors.toList());

        int partitionSize = clarksnutSchedulerSendSize.orElse(50);
        List<List<FileEntity>> partitions = ListUtils.partition(fileEntities, partitionSize);


        if (entityTransaction) {
            em.getTransaction().begin();
        }

        for (List<FileEntity> partition : partitions) {
            GroupFileEntity fileGroupEntity = new GroupFileEntity();
            fileGroupEntity.setId(UUID.randomUUID().toString());
            em.persist(fileGroupEntity);

            partition.forEach(fileEntity -> {
                fileEntity.setGroup(fileGroupEntity);
                em.merge(fileEntity);
            });
        }

        if (entityTransaction) {
            em.getTransaction().commit();
        }
    }

}
