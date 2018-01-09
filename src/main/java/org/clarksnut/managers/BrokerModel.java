package org.clarksnut.managers;

import org.clarksnut.models.BrokerType;

public class BrokerModel {

    private final BrokerType type;
    private final String email;

    public BrokerModel(BrokerType type, String email) {
        this.type = type;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public BrokerType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BrokerModel that = (BrokerModel) o;

        if (type != that.type) return false;
        return email.equals(that.email);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }

}
