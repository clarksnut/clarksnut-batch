package org.clarksnut.mail.gmail;

import org.clarksnut.mail.MailQuery;
import org.clarksnut.mail.MailQueryParser;

public class GmailQueryParser implements MailQueryParser {

    @Override
    public String parse(MailQuery query) {
        GmailQuery.Builder builder = new GmailQuery.Builder();
        if (query.getAfter() != null) {
            builder.after(query.getAfter());
        }
        if (query.getBefore() != null) {
            builder.before(query.getBefore());
        }
        if (query.getHas() != null) {
            query.getHas().forEach(builder::has);
        }
        if (query.getFileType() != null) {
            query.getFileType().forEach(builder::fileType);
        }
        return builder.build().query();
    }

}
