package app.playerzero.sdk;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PzOptions {
    String dataset = "default";
    Boolean prod = true;
    Integer batchEventsSize = 100;
    Integer debounceInMs = 2000;
    Function<String, String> privacy;
    String endpoint = "https://sdk.playerzero.app";
    Supplier<String> eventIdGenerator;
    Map<String, Object> eventProperties;
    Consumer<PzEvent> queuedEvent;
    Consumer<Collection<PzEvent>> dequeuedEvents;
    Supplier<CompletableFuture<Collection<PzEvent>>> restoreQueue;

    public PzOptions setBatchEventsSize(Integer batchEventsSize) {
        if (batchEventsSize == null) batchEventsSize = 100;
        this.batchEventsSize = batchEventsSize;
        assert this.batchEventsSize >= 0 : "Negative batch sizes are not supported";
        return this;
    }

    public PzOptions setDataset(String dataset) {
        if (dataset == null) dataset = "default";
        this.dataset = dataset;
        return this;
    }

    public PzOptions setDebounceInMs(Integer debounceInMs) {
        if (debounceInMs == null) debounceInMs = 2000;
        this.debounceInMs = debounceInMs;
        return this;
    }

    public PzOptions setEndpoint(String endpoint) {
        if (endpoint == null) endpoint = "https://sdk.playerzero.app";
        try {
            URL urlTest = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
        this.endpoint = endpoint;
        return this;
    }

    public PzOptions setEventIdGenerator(Supplier<String> eventIdGenerator) {
        this.eventIdGenerator = eventIdGenerator;
        return this;
    }

    public PzOptions setEventProperties(Map<String, Object> eventProperties) {
        this.eventProperties = eventProperties;
        return this;
    }

    public PzOptions setPrivacy(Function<String, String> privacy) {
        this.privacy = privacy;
        return this;
    }

    public PzOptions setProd(Boolean prod) {
        this.prod = prod;
        return this;
    }

    public PzOptions setQueuedEvent(Consumer<PzEvent> queuedEvent) {
        this.queuedEvent = queuedEvent;
        return this;
    }

    public PzOptions setDequeuedEvents(Consumer<Collection<PzEvent>> dequeuedEvents) {
        this.dequeuedEvents = dequeuedEvents;
        return this;
    }

    public PzOptions setRestoreQueue(Supplier<CompletableFuture<Collection<PzEvent>>> restoreQueue) {
        this.restoreQueue = restoreQueue;
        return this;
    }
}
