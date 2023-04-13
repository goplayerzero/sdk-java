package app.playerzero.sdk.log4j;

import app.playerzero.sdk.PzApi;
import app.playerzero.sdk.PzEventType;
import app.playerzero.sdk.PzOptions;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Plugin(name = "PzSdkAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class PzSdkAppender extends AbstractAppender {
    private final String apiToken;
    private final boolean prod;
    private final String dataset;
    private final String endpoint;
    private PzApi sdk;

    protected PzSdkAppender(
            String name,
            Layout<? extends Serializable> layout,
            String apiToken,
            String dataset,
            String prod,
            String endpoint
    ) {
        super(name, null, layout, false, Property.EMPTY_ARRAY);
        this.apiToken = apiToken;
        this.prod = (prod == null || "true".equalsIgnoreCase(prod));
        this.dataset = dataset;
        this.endpoint = endpoint;
    }

    @PluginFactory
    public static PzSdkAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginAttribute("apiToken") String apiToken,
            @PluginAttribute("dataset") String dataset,
            @PluginAttribute("prod") String prod,
            @PluginAttribute("endpoint") String endpoint
    ) {
        if (name == null) return null;
        if (layout == null) layout = PatternLayout.createDefaultLayout();
        return new PzSdkAppender(name, layout, apiToken, dataset, prod, endpoint);
    }

    @Override
    public void append(LogEvent event) {
        String levelStr = event.getLevel().name();
        Map<String, Object> metadata = new LinkedHashMap<>();
        event.getContextData().forEach(metadata::put);

        if ("ERROR".equals(levelStr)) {
            sdk.pendingEvent(PzEventType.Signal)
                    .setSubtype(levelStr)
                    .setSignalTitle(event.getMessage().getFormattedMessage())
                    .setSignalError(event.getThrown())
                    .setMetadata(metadata)
                    .setTs(new Date(event.getInstant().getEpochMillisecond()))
                    .send();
        } else if (!"OFF".equals(levelStr)) {
            sdk.pendingEvent(PzEventType.Logged)
                    .setSubtype(levelStr)
                    .setValue(event.getMessage().getFormattedMessage())
                    .setMetadata(metadata)
                    .setTs(new Date(event.getInstant().getEpochMillisecond()))
                    .send();
        }
    }

    @Override
    public void start() {
        sdk = PzApi.getInstance(apiToken, new PzOptions()
                .setDataset(dataset)
                .setProd(prod)
                .setEndpoint(endpoint));
        super.start();
    }
}
