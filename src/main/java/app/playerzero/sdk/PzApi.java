package app.playerzero.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class PzApi {
    private static final String SDK_VERSION = "Java " + PzApi.class.getPackage().getImplementationVersion();
    private static final Pattern CHECKS = Pattern.compile(
            "\\b\\d{3}-?\\d{2}-?\\d{4}\\b" // SSN
                    + "|\\b(?:\\d{3,4}[ -]?){4}\\b" // CC
            , Pattern.MULTILINE);
    private static final Logger logger = LoggerFactory.getLogger(PzApi.class);
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final WeakHashMap<String, PzApi> instances = new WeakHashMap<>();
    private final String apiToken;
    private final String dataset;
    private final Boolean prod;
    private final ThreadLocal<PzIdentity> identification = new InheritableThreadLocal<>();
    private final Integer batchEventsSize;
    private final Integer debounceInMs;
    private final Function<String, String> privacy;
    private final String endpoint;
    private final Supplier<String> eventIdGenerator;
    private final Map<String, Object> eventProperties;
    private final Consumer<PzEvent> queuedEvent;
    private final Consumer<Collection<PzEvent>> dequeuedEvents;
    private final WeakHashMap<Queue<PzEvent>, Future<?>> debounceRefs = new WeakHashMap<>();
    private volatile Queue<PzEvent> eventQ = new ConcurrentLinkedQueue<>();

    private PzApi(String apiToken, PzOptions options) {
        this.apiToken = apiToken;
        PzOptions o = (options == null) ? new PzOptions() : options;
        this.dataset = o.dataset;
        this.prod = o.prod;
        this.batchEventsSize = o.batchEventsSize;
        this.debounceInMs = o.debounceInMs;
        this.privacy = o.privacy;
        this.endpoint = o.endpoint + "/data";
        this.eventIdGenerator = (o.eventIdGenerator == null) ? () -> UUID.randomUUID().toString() : o.eventIdGenerator;
        this.eventProperties = (o.eventProperties == null) ? new HashMap<>() : new HashMap<>(o.eventProperties);
        this.queuedEvent = o.queuedEvent;
        this.dequeuedEvents = o.dequeuedEvents;
        if (o.restoreQueue != null) o.restoreQueue.get().whenCompleteAsync((pzEvents, throwable) -> {
            if (pzEvents != null && !pzEvents.isEmpty()) eventQ.addAll(pzEvents);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                flushEventQ().get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                // ignore
            }
        }));
    }

    public static PzApi getInstance(String apiToken) {
        return getInstance(apiToken, null);
    }

    public synchronized static PzApi getInstance(String apiToken, PzOptions options) {
        return instances.computeIfAbsent(apiToken, token -> new PzApi(token, options));
    }

    public void withIdentify(Map<String, String> actors, Map<String, Object> metadata, Runnable action) {
        PzIdentity current = this.identification.get();
        try {
            identify(actors, metadata);
            action.run();
        } finally {
            this.identification.set(current);
        }
    }

    public void identify(String id) {
        identify(id, null);
    }

    public void identify(String id, Map<String, Object> metadata) {
        HashMap<String, String> ids = null;
        if (id != null) {
            ids = new HashMap<>();
            ids.put("User", id);
        }
        identify(ids, metadata);
    }

    public void identify(Map<String, String> actors) {
        identify(actors, null);
    }

    public void identify(Map<String, String> actors, Map<String, Object> metadata) {
        if (actors == null) this.identification.remove();
        else this.identification.set(new PzIdentity(actors, metadata));
    }

    public PzPendingEvent pendingEvent(PzEventType type) {
        return new PzPendingEvent(
                type,
                this.identification.get(),
                this.eventProperties,
                this::publishEvents,
                this::privatizeText,
                this.eventIdGenerator
        );
    }

    public void track(String name) {
        track(name, null);
    }

    public void track(String name, PzEventOptions options) {
        PzIdentity identity = this.identification.get();
        if (identity == null || identity.actors.isEmpty()) {
            Exception e = new Exception();
            StackTraceElement src = Arrays.stream(e.getStackTrace())
                    .filter(elem -> !elem.getClassName().equals(PzApi.class.getName()))
                    .findFirst()
                    .get();
            logger.warn("Tracked event '{}' at {}:{} discarded due to lack of identification", name, src.getFileName(), src.getLineNumber());
            return;
        }
        PzPendingEvent eventToSend = newEvent(PzEventType.Tracked, options == null ? null : options.metadata);
        eventToSend.setSubtype(options == null ? null : options.type);
        eventToSend.setValue(privatizeText(name));
        eventToSend.send();
    }

    public void log(String message) {
        log(message, null);
    }

    public void log(String message, PzEventOptions options) {
        PzPendingEvent eventToSend = newEvent(PzEventType.Logged, options == null ? null : options.metadata);
        eventToSend.setSubtype(options == null ? null : options.type);
        eventToSend.setValue(privatizeText(message));
        eventToSend.send();
    }

    public void signal(String reason) {
        signal(reason, null, null);
    }

    public void signal(Throwable error) {
        signal(null, error, null);
    }

    public void signal(String reason, PzSignalOptions options) {
        signal(reason, null, options);
    }

    public void signal(Throwable error, PzSignalOptions options) {
        signal(null, error, options);
    }

    public void signal(String reason, Throwable error) {
        signal(reason, error, null);
    }

    public void signal(String reason, Throwable error, PzSignalOptions options) {
        if (reason == null && error == null) return;
        PzSignalOptions o = options != null ? options : new PzSignalOptions();
        if (reason == null) reason = o.reason;
        if (error == null) error = o.error;

        String message = reason;
        if (reason == null) {
            message = error.getMessage();
            if ((message == null || message.trim().isBlank()) && error.getCause() != null) {
                message = error.getCause().getMessage();
            }
            if (message == null || message.trim().isBlank()) message = error.getClass().getName();
        }

        PzPendingEvent eventToSend = newEvent(PzEventType.Signal, o.metadata);
        eventToSend.setSubtype(o.type);
        eventToSend.setValue(o.fp);
        eventToSend.setSignalTitle(message);
        eventToSend.setSignalError(error);
        eventToSend.send();
    }

    private Future<Boolean> flushEventQ() {
        Queue<PzEvent> flushedQ;
        synchronized (eventQ) {
            flushedQ = eventQ;
            eventQ = new ConcurrentLinkedQueue<>();

            Future<?> remove = debounceRefs.remove(flushedQ);
            if (remove != null && !remove.isCancelled() && !remove.isDone()) {
                remove.cancel(false);
            }
        }

        if (flushedQ.isEmpty()) return CompletableFuture.completedFuture(false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            mapper.writeValue(baos, flushedQ);
        } catch (IOException e) {
            // intentionally ignore the error
            return CompletableFuture.completedFuture(false);
        }

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        HttpClient client = HttpClient.newHttpClient();
        client.sendAsync(
                HttpRequest.newBuilder(URI.create(endpoint))
                        .POST(HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray()))
                        .header("Authorization", String.format("Bearer %s", apiToken))
                        .header("X-PlayerZeroSdk", SDK_VERSION)
                        .header("X-PlayerZeroScope", String.format("%s %s", prod, dataset))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        ).whenComplete((resp, error) -> {
            if (dequeuedEvents != null) dequeuedEvents.accept(flushedQ);
            future.complete(true);
        }); // Intentional fire and forget

        return future;
    }

    private PzPendingEvent newEvent(PzEventType type, Map<String, Object> metadata) {
        return pendingEvent(type).setMetadata(metadata);
    }

    private String privatizeText(String text) {
        if (text.length() > 1024 * 1024) return "";
        String cleansedText = text;
        if (privacy != null) cleansedText = privacy.apply(text);
        return cleansedText.replaceAll(CHECKS.pattern(), "<redact>");
    }

    private void publishEvents(PzEvent data) {
        Queue<PzEvent> qRef = eventQ; // it could get flushed in the middle of this
        eventQ.add(data);
        if (queuedEvent != null) queuedEvent.accept(data);
        if (eventQ.size() >= batchEventsSize) {
            flushEventQ();
            return;
        }

        Future<?> previous = debounceRefs.put(
                qRef,
                scheduler.schedule(this::flushEventQ, debounceInMs, TimeUnit.MILLISECONDS)
        );
        if (previous != null) previous.cancel(false);
    }
}
