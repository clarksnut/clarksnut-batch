package org.clarksnut.mail;

public class MailRepositoryModel {

    private final String email;
    private final String userRefreshToken;
    private final String brokerRefreshToken;

    private MailRepositoryModel(Builder builder) {
        this.email = builder.email;
        this.userRefreshToken = builder.userRefreshToken;
        this.brokerRefreshToken = builder.brokerRefreshToken;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getEmail() {
        return email;
    }

    public String getUserRefreshToken() {
        return userRefreshToken;
    }

    public String getBrokerRefreshToken() {
        return brokerRefreshToken;
    }

    public static class Builder {
        private String email;
        private String userRefreshToken;
        private String brokerRefreshToken;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder userRefreshToken(String userRefreshToken) {
            this.userRefreshToken = userRefreshToken;
            return this;
        }

        public Builder brokerRefreshToken(String brokerRefreshToken) {
            this.brokerRefreshToken = brokerRefreshToken;
            return this;
        }

        public MailRepositoryModel build() {
            return new MailRepositoryModel(this);
        }
    }
}
