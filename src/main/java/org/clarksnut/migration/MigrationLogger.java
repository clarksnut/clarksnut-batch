package org.clarksnut.migration;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode = "CLARKSNUT")
public interface MigrationLogger extends BasicLogger {

    MigrationLogger LOGGER = Logger.getMessageLogger(MigrationLogger.class, MigrationLogger.class.getPackage().getName());

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 1, value = "Flyway migration is starting...")
    void migrationStarting();

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 2, value = "Flyway migration finished")
    void migrationFinished();

    @LogMessage(level = Logger.Level.ERROR)
    @Message(id = 3, value = "Flyway could not look up JNDI DataSource %s")
    void errorLookingUpDatasource(String dataSourceJndi);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 4, value = "Flyway detected %s Dialect")
    void detectedDialect(String dataSourceJndi);

}
