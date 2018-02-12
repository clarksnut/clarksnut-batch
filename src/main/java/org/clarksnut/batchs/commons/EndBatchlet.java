package org.clarksnut.batchs.commons;

import org.jboss.logging.Logger;

import javax.batch.api.AbstractBatchlet;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@Dependent
public class EndBatchlet extends AbstractBatchlet {

    private static final Logger logger = Logger.getLogger(EndBatchlet.class);

    @Inject
    private JobContext jobCtx;

    @Override
    public String process() {
        logger.info("Ending batchlet..." + jobCtx.getJobName());
        return BatchStatus.COMPLETED.toString();
    }

}