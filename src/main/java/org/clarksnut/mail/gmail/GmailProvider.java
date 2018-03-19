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
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.common.collect.Lists;
import org.clarksnut.mail.MailMessageModel;
import org.clarksnut.mail.MailProvider;
import org.clarksnut.mail.MailQuery;
import org.clarksnut.mail.MailVendorType;
import org.clarksnut.mail.exceptions.MailReadException;
import org.clarksnut.models.BrokerType;
import org.clarksnut.models.Constants;
import org.jboss.logging.Logger;
import org.keycloak.representations.AccessTokenResponse;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@Stateless
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
    public List<MailMessageModel> getMessages(String email, String refreshToken, MailQuery query) throws MailReadException {
        AccessTokenResponse token = getToken(refreshToken);
        Gmail client = buildClient(token);

        List<MailMessageModel> result = new ArrayList<>();
        try {
            List<Message> messages = getGmailMessages(client, email, query);
            for (List<Message> chunk : Lists.partition(messages, batchSize)) {
                readGmailMessagesInBatch(chunk, client, email).forEach(message -> {
                    result.add(new GmailMessageAdapter(client, email, message));
                });
            }
        } catch (IOException e) {
            throw new MailReadException("Could not pull messages", e);
        }
        return result;
    }

    @Override
    public Map<String, byte[]> getAttachments(String email, String refreshToken, Map<String, String> attachments) throws MailReadException {
        Map<String, byte[]> result = new HashMap<>();

        AccessTokenResponse token = getToken(refreshToken);
        Gmail client = buildClient(token);

        BatchRequest batch = client.batch();

        try {
            for (Map.Entry<String, String> entry : attachments.entrySet()) {
                String attachmentId = entry.getKey();
                String messageId = entry.getValue();


                client
                        .users()
                        .messages()
                        .attachments()
                        .get(email, messageId, attachmentId)
                        .queue(batch, new JsonBatchCallback<MessagePartBody>() {
                            @Override
                            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
                                logger.error("Read Message Failure code=" + e.getCode() + " message=" + e.getMessage());
                            }

                            @Override
                            public void onSuccess(MessagePartBody messagePartBody, HttpHeaders responseHeaders) throws IOException {
                                Base64 base64url = new Base64(true);
                                byte[] bytes = base64url.decodeBase64(messagePartBody.getData());

                                result.put(attachmentId, bytes);
                            }
                        });

            }

            batch.execute();
        } catch (IOException e) {
            throw new MailReadException("Could not pull attachment messages");
        }

        return result;
    }

    @Override
    public boolean validate(String refreshToken) throws IOException {
        Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(new NetHttpTransport())
                .setJsonFactory(new JacksonFactory())
                .setTokenServerUrl(new GenericUrl(Constants.GOOGLE_OAUTH2_TOKEN_SERVER_URL))
                .setClientAuthentication(new BasicAuthentication(clarksnutGoogleBrokerClientId.orElse(""), clarksnutGoogleBrokerClientSecret.orElse("")))
                .build()
                .setRefreshToken(refreshToken);
        try {
            // If true or false is returned, it means refresh token is still valid
            credential.refreshToken();
        } catch (IOException e) {
            // If exception was threw, it means that token probably was revoked
            return false;
        }

        return true;
    }

    private AccessTokenResponse getToken(String refreshToken) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(refreshToken, AccessTokenResponse.class);
        } catch (IOException e) {
            throw new IllegalStateException("Invalid refresh token, could not parse");
        }
    }

    private Gmail buildClient(AccessTokenResponse token) {
        Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(new NetHttpTransport())
                .setJsonFactory(new JacksonFactory())
                .setTokenServerUrl(new GenericUrl(Constants.GOOGLE_OAUTH2_TOKEN_SERVER_URL))
                .setClientAuthentication(new BasicAuthentication(clarksnutGoogleBrokerClientId.orElse(""), clarksnutGoogleBrokerClientSecret.orElse("")))
                .build()
                .setRefreshToken(token.getRefreshToken());

        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(applicationName)
                .build();
    }

    private List<Message> getGmailMessages(Gmail gmail, String email, MailQuery query) throws IOException {
        List<Message> messages = new ArrayList<>();

        String gmailQuery = new GmailQueryParser().parse(query);
        ListMessagesResponse response = gmail.users()
                .messages()
                .list(email)
                .setQ(gmailQuery)
                .execute();

        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = gmail.users()
                        .messages()
                        .list(email)
                        .setQ(gmailQuery)
                        .setPageToken(pageToken)
                        .execute();
            } else {
                break;
            }
        }

        return messages;
    }

    private List<Message> readGmailMessagesInBatch(List<Message> messages, Gmail gmail, String email) throws IOException {
        List<Message> result = new ArrayList<>();
        BatchRequest batch = gmail.batch();
        for (Message message : messages) {
            gmail
                    .users()
                    .messages()
                    .get(email, message.getId())
                    .queue(batch, new JsonBatchCallback<Message>() {
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
