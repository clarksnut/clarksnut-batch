package org.clarksnut.batchs;

import javax.batch.api.listener.AbstractJobListener;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class JobContextSetupListener extends AbstractJobListener {

    @Inject
    private JobContext jobContext;

    @Override
    public void beforeJob() throws Exception {
        BatchLogger.LOGGER.startingJob(jobContext.getJobName(), jobContext.getExecutionId());
    }

    @Override
    public void afterJob() throws Exception {
        BatchLogger.LOGGER.finishedJob(jobContext.getJobName(), jobContext.getExecutionId(), jobContext.getBatchStatus(), jobContext.getExitStatus());
    }
}
