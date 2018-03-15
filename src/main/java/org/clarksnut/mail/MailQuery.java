package org.clarksnut.mail;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class MailQuery {

    private final Date after;
    private final Date before;
    private final Set<String> has;
    private final Set<String> fileType;

    private MailQuery(Builder builder) {
        this.after = builder.after;
        this.before = builder.before;
        this.has = builder.has;
        this.fileType = builder.fileType;
    }

    public Date getAfter() {
        return after;
    }

    public Date getBefore() {
        return before;
    }

    public Set<String> getHas() {
        return has;
    }

    public Set<String> getFileType() {
        return fileType;
    }

    public static class Builder {
        private Date after;
        private Date before;
        private Set<String> has = new HashSet<>();
        private Set<String> fileType = new HashSet<>();

        public Builder after(Date after) {
            this.after = after;
            return this;
        }

        public Builder before(Date before) {
            this.before = before;
            return this;
        }

        public Builder has(String type) {
            this.has.add(type);
            return this;
        }

        public Builder fileType(String fileType) {
            this.fileType.add(fileType);
            return this;
        }

        public MailQuery build() {
            return new MailQuery(this);
        }
    }
}
