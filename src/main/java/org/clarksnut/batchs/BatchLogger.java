package org.clarksnut.batchs;

import org.clarksnut.models.BrokerType;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

import javax.batch.runtime.BatchStatus;

@MessageLogger(projectCode = "CLARKSNUT")
public interface BatchLogger extends BasicLogger {

    BatchLogger LOGGER = Logger.getMessageLogger(BatchLogger.class, BatchLogger.class.getPackage().getName());

    @LogMessage(level = Logger.Level.ERROR)
    @Message(id = 1000, value = "Could not find a Provider for broker [%s].")
    void brokerProviderNotFound(BrokerType brokerType);

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 1001, value = "Broker [%s: %s] marked for disabling.")
    void brokerMarkedForDisabling(BrokerType brokerType, String email);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 1002, value = "Could not verify refresh token of Broker [%s: %s].")
    void couldNotVerifyRefreshToken(BrokerType brokerType, String email);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 1003, value = "Mail message from Broker [%s: %s] and messageId [%s] was already imported.")
    void mailMessageWasAlreadyImported(BrokerType type, String email, String messageId);

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 1004, value = "Broker [%s: %s]. %s is not a valid UBL File.")
    void notValidUBLFile(BrokerType type, String email, String filename);

    @LogMessage(level = Logger.Level.ERROR)
    @Message(id = 1005, value = "Could not send file %s due to remote server error %s: %s.")
    void couldNotSendFile(String filename, int status, String reasonPhrase);

    @LogMessage(level = Logger.Level.ERROR)
    @Message(id = 1006, value = "Could not send file %s and dont know why %s: %s.")
    void couldNotSendFileAndNotKnowWhy(String filename, int status, String reasonPhrase);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 1007, value = "Job %s: %s starting...")
    void startingJob(String jobName, long executionId);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 1008, value = "Job %s: %s finished with batch status %s and exit status %s")
    void finishedJob(String jobName, long executionId, BatchStatus batchStatus, String exitStatus);
}
