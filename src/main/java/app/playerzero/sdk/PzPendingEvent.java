package app.playerzero.sdk;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PzPendingEvent {
    private final PzEventType type;
    private final Consumer<PzEvent> sendFn;
    private final Function<String, String> privatizeText;
    private final Supplier<String> eventIdGenerator;
    private final Map<String, Object> properties;
    private String id;
    private String subtype;
    private PzIdentity identity;
    private String value;
    private Date ts;
    private Map<String, Object> metadata;
    private String signalTitle;
    private Throwable signalError;

    PzPendingEvent(
            PzEventType type,
            PzIdentity defaultIdentity,
            Map<String, Object> eventProperties,
            Consumer<PzEvent> sendFn,
            Function<String, String> privatizeText,
            Supplier<String> eventIdGenerator
    ) {
        assert (type != null) : "PzEventType is not defined";
        assert (sendFn != null) : "Send Implementation is not defined";
        this.type = type;
        this.identity = defaultIdentity;
        this.properties = (eventProperties == null) ? new HashMap<>() : new HashMap<>(eventProperties);
        this.sendFn = sendFn;
        this.privatizeText = privatizeText;
        this.eventIdGenerator = eventIdGenerator;
    }

    public void send() {
        if (this.identity != null) properties.put("identity", this.identity.properties);
        if (this.metadata != null) properties.put("metadata", this.metadata);

        properties.put("thread", Thread.currentThread().getName());

        if (type == PzEventType.Signal) {
            properties.put("title", privatizeText.apply(signalTitle));
            if (signalError != null) {
                LinkedHashMap<String, String> stacktrace = new LinkedHashMap<>();
                stacktrace.put("name", signalError.getClass().getName());
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                signalError.printStackTrace(pw);
                stacktrace.put("stack", sw.toString());
                properties.put("error", stacktrace);
            }
        }

        sendFn.accept(new PzEvent(
                (id == null)
                        ? (this.eventIdGenerator != null) ? this.eventIdGenerator.get() : null
                        : id,
                type,
                subtype,
                (identity != null) ? identity.actors : null,
                value,
                ts,
                properties
        ));
    }

    public PzPendingEvent setSignalTitle(String title) {
        if (this.type != PzEventType.Signal) throw new UnsupportedOperationException("Must be of Signal type");
        this.signalTitle = title;
        return this;
    }

    public PzPendingEvent setSignalError(Throwable error) {
        if (this.type != PzEventType.Signal) throw new UnsupportedOperationException("Must be of Signal type");
        this.signalError = error;
        return this;
    }

    public PzPendingEvent setId(String id) {
        this.id = id;
        return this;
    }

    public PzPendingEvent setSubtype(String subtype) {
        this.subtype = subtype;
        return this;
    }

    public PzPendingEvent identify(String id) {
        return identify(id, (Map<String, Object>) null);
    }
    public PzPendingEvent identify(String userId, Map<String, Object> metadata) {
        HashMap<String, String> ids = null;
        if (userId != null) {
            ids = new HashMap<>();
            ids.put("UserId", userId);
        }
        return identify(ids, metadata);
    }

    public PzPendingEvent identify(Map<String, String> actors) {
        return identify(actors, null);
    }

    public PzPendingEvent identify(Map<String, String> actors, Map<String, Object> metadata) {
        if (actors == null) this.identity = null;
        else this.identity = new PzIdentity(actors, metadata);
        return this;
    }

    public PzPendingEvent setValue(String value) {
        this.value = (type == PzEventType.Signal) ? value : privatizeText.apply(value);
        return this;
    }

    public PzPendingEvent setTs(Date ts) {
        this.ts = ts;
        return this;
    }

    public PzPendingEvent setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }
}
