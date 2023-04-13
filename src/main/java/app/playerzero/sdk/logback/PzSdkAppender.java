package app.playerzero.sdk.logback;

import app.playerzero.sdk.PzApi;
import app.playerzero.sdk.PzEventType;
import app.playerzero.sdk.PzOptions;
import app.playerzero.sdk.PzPendingEvent;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.event.KeyValuePair;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PzSdkAppender extends AppenderBase<ILoggingEvent> {
    private PzApi sdk;
    private String apiToken;
    private String dataset = "dataset";
    private Boolean prod = true;
    private Integer batchEventsSize = 100;
    private Integer debounceInMs = 2000;
    private Function<String, String> privacy;
    private String endpoint = "https://sdk.playerzero.app";
    private Map<String, Object> eventProperties;

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public Boolean getProd() {
        return prod;
    }

    public void setProd(Boolean prod) {
        this.prod = prod;
    }

    public Integer getBatchEventsSize() {
        return batchEventsSize;
    }

    public void setBatchEventsSize(Integer batchEventsSize) {
        this.batchEventsSize = batchEventsSize;
    }

    public Integer getDebounceInMs() {
        return debounceInMs;
    }

    public void setDebounceInMs(Integer debounceInMs) {
        this.debounceInMs = debounceInMs;
    }

    public Function<String, String> getPrivacy() {
        return privacy;
    }

    public void setPrivacy(Function<String, String> privacy) {
        this.privacy = privacy;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Map<String, Object> getEventProperties() {
        return eventProperties;
    }

    public void setEventProperties(Map<String, Object> eventProperties) {
        this.eventProperties = eventProperties;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        String levelStr = eventObject.getLevel().levelStr;
        Map<String, Object> metadata = new LinkedHashMap<>();
        List<KeyValuePair> pairs = eventObject.getKeyValuePairs();
        if (pairs != null) {
            for (KeyValuePair pair : pairs) metadata.put(pair.key, pair.value);
        }

        if ("ERROR".equals(levelStr)) {
            PzPendingEvent pzPendingEvent = sdk.pendingEvent(PzEventType.Signal)
                    .setSubtype(levelStr)
                    .setSignalTitle(eventObject.getMessage())
                    .setMetadata(metadata)
                    .setTs(new Date(eventObject.getTimeStamp()));
            IThrowableProxy throwableProxy = eventObject.getThrowableProxy();
            if (throwableProxy instanceof ThrowableProxy) {
                pzPendingEvent.setSignalError(((ThrowableProxy) throwableProxy).getThrowable());
            }
            pzPendingEvent.send();
        } else if (!"OFF".equals(levelStr)) {
            sdk.pendingEvent(PzEventType.Logged)
                    .setSubtype(levelStr)
                    .setValue(eventObject.getMessage())
                    .setMetadata(metadata)
                    .setTs(new Date(eventObject.getTimeStamp()))
                    .send();
        }
    }

    @Override
    public void start() {
        sdk = PzApi.getInstance(apiToken, new PzOptions()
                .setDataset(dataset)
                .setProd(prod)
                .setBatchEventsSize(batchEventsSize)
                .setEndpoint(endpoint)
                .setPrivacy(privacy)
                .setDebounceInMs(debounceInMs)
                .setEventProperties(eventProperties));
        super.start();
    }
}
