package app.playerzero.sdk;

import java.util.Map;

public class PzEventOptions {
    protected String type;
    protected Map<String, Object> metadata;

    public PzEventOptions setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    public PzEventOptions setType(String type) {
        this.type = type;
        return this;
    }
}
