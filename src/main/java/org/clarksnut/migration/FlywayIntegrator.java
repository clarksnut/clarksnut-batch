package org.clarksnut.migration;

import org.flywaydb.core.Flyway;
import org.hibernate.boot.Metadata;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.PostgreSQL9Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class FlywayIntegrator implements Integrator {

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        MigrationLogger.LOGGER.migrationStarting();

        Flyway flyway = new Flyway();
        String dataSourceJndi = getDatasourceNameJndi();
        try {
            DataSource dataSource = (DataSource) new InitialContext().lookup(dataSourceJndi);
            flyway.setDataSource(dataSource);
        } catch (NamingException ex) {
            MigrationLogger.LOGGER.errorLookingUpDatasource(dataSourceJndi);
            throw new IllegalStateException("Could not look up Datasource");
        }

        Dialect dialect = sessionFactory.getJdbcServices().getDialect();
        if (dialect instanceof H2Dialect) {
            flyway.setLocations("classpath:db/migration/h2");

            MigrationLogger.LOGGER.detectedDialect("H2Dialect");
        } else if (dialect instanceof PostgreSQL9Dialect) {
            flyway.setLocations("classpath:db/migration/postgresql");

            MigrationLogger.LOGGER.detectedDialect("PostgreSQL9Dialect");
        } else {
            throw new IllegalStateException("Dialect not supported");
        }

        flyway.migrate();
        MigrationLogger.LOGGER.migrationFinished();
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {

    }

    private String getDatasourceNameJndi() {
        return "java:jboss/datasources/ClarksnutDS";
    }

}
