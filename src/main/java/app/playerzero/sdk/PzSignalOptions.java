package app.playerzero.sdk;

import java.util.Map;

public class PzSignalOptions extends PzEventOptions {
    protected String reason;
    protected Throwable error;
    protected String fp;

    @Override
    public PzSignalOptions setType(String type) {
        super.setType(type);
        return this;
    }

    @Override
    public PzSignalOptions setMetadata(Map<String, Object> metadata) {
        super.setMetadata(metadata);
        return this;
    }

    public PzSignalOptions setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public PzSignalOptions setError(Throwable error) {
        this.error = error;
        return this;
    }

    public PzSignalOptions setFp(String fp) {
        this.fp = fp;
        return this;
    }
}
