package app.playerzero.sdk;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PzEvent {
    private final String id;
    private final PzEventType type;
    private final String subtype;
    private final Map<String, String> identity;
    private final Date ts;
    private final Map<String, Object> properties;
    private String value;

    PzEvent(
            String id,
            PzEventType type,
            String subtype,
            Map<String, String> identities,
            String value,
            Date ts,
            Map<String, Object> properties
    ) {
        this.id = id;
        this.type = type;
        this.subtype = (subtype != null && !subtype.isBlank()) ? subtype.trim() : null;
        this.identity = (identities != null) ? new HashMap<>(identities) : new HashMap<>();
        this.value = value;
        this.ts = (ts != null) ? ts : new Date();
        this.properties = (properties != null) ? new HashMap<>(properties) : new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public PzEventType getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public Map<String, String> getIdentity() {
        return identity;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getTs() {
        return ts;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
