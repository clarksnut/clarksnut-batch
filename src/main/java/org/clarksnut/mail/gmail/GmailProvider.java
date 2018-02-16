package org.clarksnut.mail.gmail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.common.collect.Lists;
import net.sf.cglib.proxy.Enhancer;
import org.clarksnut.mail.*;
import org.clarksnut.mail.exceptions.MailReadException;
import org.clarksnut.mail.utils.CredentialHandler;
import org.clarksnut.managers.BrokerManager;
import org.clarksnut.models.BrokerType;
import org.jboss.logging.Logger;
import org.keycloak.representations.AccessTokenResponse;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@RequestScoped
@MailVendorType(BrokerType.GOOGLE)
public class GmailProvider implements MailProvider {

    private static final Logger logger = Logger.getLogger(GmailProvider.class);
    private static final int batchSize = 1000;

    @Inject
    @ConfigurationValue("clarksnut.mail.vendor.gmail.applicationName")
    private Optional<String> clarksnutGmailApplicationName;


    @Inject
    @ConfigurationValue("clarksnut.broker.vendor.google.clientId")
    private Optional<String> clarksnutGoogleBrokerClientId;

    @Inject
    @ConfigurationValue("clarksnut.broker.vendor.google.clientSecret")
    private Optional<String> clarksnutGoogleBrokerClientSecret;

    private HttpTransport HTTP_TRANSPORT;
    private JsonFactory JSON_FACTORY;

    private String applicationName;

    @PostConstruct
    private void init() {
        applicationName = clarksnutGmailApplicationName.orElse("clarksnut");
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            JSON_FACTORY = JacksonFactory.getDefaultInstance();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Could not initialize http transport and/or json factory");
        }
    }

    @Override
    public TreeSet<MailUblMessageModel> getUblMessages(MailRepositoryModel repository, MailQuery query) throws MailReadException {
        Gmail gmail = buildClient(repository);

        TreeSet<MailUblMessageModel> result = new TreeSet<>(Comparator.comparing(MailUblMessageModel::getReceiveDate));

        if (gmail== null) {
            logger.error("Could not build Credential from token");
            return result;
        }

        try {
            List<Message> messages = pullMessages(gmail, repository, query);
            for (List<Message> chunk : Lists.partition(messages, batchSize)) {
                execBatch(chunk, gmail, repository).forEach(message -> {
                    result.add(new GmailMessageAdapter(gmail, repository, message));
                });
            }

        } catch (IOException e) {
            throw new MailReadException("Could not pull messages", e);
        }
        return result;
    }

    private Gmail buildClient(MailRepositoryModel mailRepository) {
        Credential credential;
        if (mailRepository.getBrokerRefreshToken() != null) {
            ObjectMapper mapper = new ObjectMapper();
            AccessTokenResponse token;
            try {
                token = mapper.readValue(mailRepository.getBrokerRefreshToken(), AccessTokenResponse.class);
            } catch (IOException e) {
                return null;
            }

            credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                    .setTransport(new NetHttpTransport())
                    .setJsonFactory(new JacksonFactory())
                    .setTokenServerUrl(new GenericUrl("https://www.googleapis.com/oauth2/v4/token"))
                    .setClientAuthentication(new BasicAuthentication(clarksnutGoogleBrokerClientId.get(), clarksnutGoogleBrokerClientSecret.get()))
                    .build()
                    .setRefreshToken(token.getRefreshToken());
        } else {
            credential = BrokerManager.getCredential().setRefreshToken(mailRepository.getUserRefreshToken());

            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(Credential.class);
            enhancer.setCallback(new CredentialHandler("google", credential));

            credential = (Credential) enhancer.create(
                    new Class[]{Credential.AccessMethod.class},
                    new Credential.AccessMethod[]{credential.getMethod()});
        }


        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(applicationName)
                .build();
    }

    private List<Message> pullMessages(Gmail gmail, MailRepositoryModel repository, MailQuery query) throws IOException {
        List<Message> messages = new ArrayList<>();
        GmailQueryParser parser = new GmailQueryParser();
        String gmailQuery = parser.parse(query);

        ListMessagesResponse response = gmail.users()
                .messages()
                .list(repository.getEmail())
                .setQ(gmailQuery)
                .execute();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = gmail.users()
                        .messages()
                        .list(repository.getEmail())
                        .setQ(gmailQuery)
                        .setPageToken(pageToken)
                        .execute();
            } else {
                break;
            }
        }

        return messages;
    }

    private List<Message> execBatch(List<Message> messages, Gmail gmail, MailRepositoryModel repository) throws IOException {
        List<Message> result = new ArrayList<>();
        BatchRequest batch = gmail.batch();
        for (Message message : messages) {
            gmail.users().messages().get(repository.getEmail(), message.getId()).queue(batch, new JsonBatchCallback<Message>() {
                @Override
                public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
                    logger.error("Read Message Failure code=" + e.getCode() + " message=" + e.getMessage());
                }

                @Override
                public void onSuccess(Message message, HttpHeaders responseHeaders) throws IOException {
                    result.add(message);
                }
            });
        }
        batch.execute();
        return result;
    }
}
