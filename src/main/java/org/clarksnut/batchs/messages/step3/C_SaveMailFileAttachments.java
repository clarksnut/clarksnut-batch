package org.clarksnut.batchs.messages.step3;

import org.clarksnut.models.jpa.entity.FileEntity;
import org.jberet.support.io.JpaItemReaderWriterBase;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemWriter;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
public class C_SaveMailFileAttachments extends JpaItemReaderWriterBase implements ItemWriter {

    /**
     * Flag to control whether to begin entity transaction before writing items,
     * and to commit entity transaction after writing items.
     * Optional property, and defaults to {@code false}.
     */
    @Inject
    @BatchProperty
    protected boolean entityTransaction;

    @Override
    public void open(Serializable checkpoint) throws Exception {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void writeItems(List<Object> items) throws Exception {
        if (entityTransaction) {
            em.getTransaction().begin();
        }

        for (final Object e : items) {
            FileEntity fileEntity = (FileEntity) e;
            if (fileEntity != null) {
                em.persist(fileEntity);
            }
        }

        if (entityTransaction) {
            em.getTransaction().commit();
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return null;
    }
}
