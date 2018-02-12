package org.clarksnut.batchs.messages.send;

import org.clarksnut.batchs.core.ResteasyItemWriter;
import org.clarksnut.models.jpa.entity.GroupFileEntity;
import org.clarksnut.services.KeycloakDeploymentConfig;
import org.jberet.support._private.SupportMessages;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.admin.client.Config;
import org.keycloak.admin.client.resource.BearerAuthFilter;
import org.keycloak.admin.client.token.TokenManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.batch.api.BatchProperty;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Named
public class SendGroupFilesWriter extends ResteasyItemWriter {

    private static final Logger logger = Logger.getLogger(SendGroupFilesWriter.class);

    @Inject
    @BatchProperty
    protected String authToken;

    private TokenManager tokenManager;
    private ResteasyClient tokenClient;

    /**
     * {@code javax.enterprise.inject.Instance} that holds optional injection
     * of {@code EntityManager}. If {@link #entityManagerLookupName} is not
     * specified, this field will be checked to obtain {@code EntityManager}.
     */
    @Inject
    protected Instance<EntityManager> entityManagerInstance;

    /**
     * JNDI lookup name of {@code EntityManager}. Optional property, and defaults
     * to null.  If specified, it will be used to perform a JNDI lookup of the
     * {@code EntityManager}.
     */
    @Inject
    @BatchProperty
    protected String entityManagerLookupName;

    /**
     * Persistence unit name. Optional property and defaults to null.
     * If neither {@link #entityManagerLookupName} nor {@link #entityManagerInstance}
     * is initialized with injected value, this persistence unit name will be used
     * to create {@EntityManagerFactory} and {@code EntityManager}.
     */
    @Inject
    @BatchProperty
    protected String persistenceUnitName;

    /**
     * Persistence unit properties, as a list of key-value pairs separated by comma (,).
     * Optional property and defaults to null.
     */
    @Inject
    @BatchProperty
    protected Map persistenceUnitProperties;

    protected EntityManagerFactory emf;
    protected EntityManager em;

    /**
     * Flag to control whether to begin entity transaction before writing items,
     * and to commit entity transaction after writing items.
     * Optional property, and defaults to {@code false}.
     */
    @Inject
    @BatchProperty
    protected boolean entityTransaction;

    @PostConstruct
    protected void postConstruct() {
        initEntityManager();
    }

    @PreDestroy
    protected void preDestroy() {
        closeEntityManager();
    }

    protected void initEntityManager() {
        if (em == null) {
            if (entityManagerLookupName != null) {
                InitialContext ic = null;
                try {
                    ic = new InitialContext();
                    em = (EntityManager) ic.lookup(entityManagerLookupName);
                } catch (final NamingException e) {
                    throw SupportMessages.MESSAGES.failToLookup(e, entityManagerLookupName);
                } finally {
                    if (ic != null) {
                        try {
                            ic.close();
                        } catch (final NamingException e) {
                            //ignore
                        }
                    }
                }
            } else {
                if (entityManagerInstance != null && !entityManagerInstance.isUnsatisfied()) {
                    em = entityManagerInstance.get();
                }
                if (em == null) {
                    emf = Persistence.createEntityManagerFactory(persistenceUnitName, persistenceUnitProperties);
                    em = emf.createEntityManager();
                }
            }
        }
    }

    protected void closeEntityManager() {
        if (emf != null) {
            em.close();
            emf.close();
        }
    }

    @Override
    public void open(Serializable checkpoint) throws Exception {
        super.open(checkpoint);
        client.register(newAuthFilter());
    }

    private BearerAuthFilter newAuthFilter() {
        BearerAuthFilter authFilter;
        if (authToken != null) {
            authFilter = new BearerAuthFilter(authToken);
        } else {
            KeycloakDeployment kcDeployment = KeycloakDeploymentConfig.getInstance().getDeployment();
            Config config = new Config(
                    kcDeployment.getAuthServerBaseUrl(), kcDeployment.getRealm(),
                    null, null,
                    kcDeployment.getResourceName(), (String) kcDeployment.getResourceCredentials().get("secret"),
                    OAuth2Constants.CLIENT_CREDENTIALS);

            tokenClient = new ResteasyClientBuilder().connectionPoolSize(10).build();
            tokenManager = new TokenManager(config, tokenClient);

            authFilter = new BearerAuthFilter(tokenManager);
        }
        return authFilter;
    }

    @Override
    public void writeItems(List<Object> items) throws Exception {
        final ResteasyWebTarget target = client.target(restUrl);

        if (entityTransaction) {
            em.getTransaction().begin();
        }

        for (Object item : items) {
            ((Map<GroupFileEntity, GenericEntity>) item).forEach((key, value) -> {
                Entity<Object> entity = Entity.entity(value, mediaTypeInstance);
                final Response response;
                if (HttpMethod.POST.equals(httpMethod)) {
                    response = target.request().post(entity);
                } else {
                    response = target.request().put(entity);
                }
                final Response.Status.Family statusFamily = response.getStatusInfo().getFamily();
                if (statusFamily == Response.Status.Family.SUCCESSFUL) {
                    em.merge(key);
                }
                if (statusFamily == Response.Status.Family.CLIENT_ERROR || statusFamily == Response.Status.Family.SERVER_ERROR) {
                    logger.error("Could not send file:" + response.getStatus() + " " + response.getStatusInfo().getReasonPhrase() + " " + response.getEntity());
                }
            });
        }

        if (entityTransaction) {
            em.getTransaction().commit();
        }
    }

    @Override
    public void close() {
        super.close();
        if (tokenClient != null) {
            tokenClient.close();
            tokenClient = null;
        }
    }
}
