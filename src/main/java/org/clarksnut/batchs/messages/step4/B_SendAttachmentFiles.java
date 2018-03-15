package org.clarksnut.batchs.messages.step4;

import org.clarksnut.batchs.BatchLogger;
import org.clarksnut.batchs.core.ResteasyItemWriter;
import org.clarksnut.models.SendStatus;
import org.clarksnut.models.jpa.entity.AttachmentEntity;
import org.clarksnut.models.jpa.entity.SendStatusEntity;
import org.clarksnut.services.KeycloakDeploymentConfig;
import org.jberet.support._private.SupportMessages;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
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
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Named
public class B_SendAttachmentFiles extends ResteasyItemWriter {

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
            AbstractMap.SimpleEntry<AttachmentEntity, GenericEntity<MultipartFormDataOutput>> entry = (AbstractMap.SimpleEntry<AttachmentEntity, GenericEntity<MultipartFormDataOutput>>) item;
            AttachmentEntity attachmentEntity = entry.getKey();
            GenericEntity<MultipartFormDataOutput> genericEntity = entry.getValue();

            Entity<Object> entity = Entity.entity(genericEntity, mediaTypeInstance);
            final Response response;
            if (HttpMethod.POST.equals(httpMethod)) {
                response = target.request().post(entity);
            } else {
                response = target.request().put(entity);
            }

            final int status = response.getStatus();
            final Response.Status.Family statusFamily = response.getStatusInfo().getFamily();

            SendStatusEntity sendStatusEntity = attachmentEntity.getSendStatus();

            if (statusFamily == Response.Status.Family.SUCCESSFUL) {
                sendStatusEntity.setStatus(SendStatus.SENT_SUCCESSFULLY);
            } else if (statusFamily == Response.Status.Family.CLIENT_ERROR) {
                if (status == Response.Status.CONFLICT.getStatusCode()) {
                    sendStatusEntity.setStatus(SendStatus.SENT_SUCCESSFULLY_BUT_ALREADY_IMPORTED);
                } else {
                    sendStatusEntity.setStatus(SendStatus.SENT_SUCCESSFULLY_BUT_REJECTED);
                }
            } else if (statusFamily == Response.Status.Family.SERVER_ERROR) {
                BatchLogger.LOGGER.couldNotSendFile(attachmentEntity.getFilename(), response.getStatus(), response.getStatusInfo().getReasonPhrase());
            } else {
                BatchLogger.LOGGER.couldNotSendFileAndNotKnowWhy(attachmentEntity.getFilename(), response.getStatus(), response.getStatusInfo().getReasonPhrase());
            }

            if (statusFamily == Response.Status.Family.SUCCESSFUL || statusFamily == Response.Status.Family.CLIENT_ERROR) {
                em.merge(sendStatusEntity);
            }
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
