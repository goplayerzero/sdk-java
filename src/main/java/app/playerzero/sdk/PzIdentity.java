package app.playerzero.sdk;

import java.util.HashMap;
import java.util.Map;

class PzIdentity {
    Map<String, String> actors;
    Map<String, Object> properties;

    PzIdentity() {
        this(new HashMap<>());
    }

    private PzIdentity(Map<String, String> actors) {
        this(actors, new HashMap<>());
    }

    PzIdentity(Map<String, String> actors, Map<String, Object> properties) {
        this.actors = actors;
        this.properties = properties;
        if (this.properties == null) this.properties = new HashMap<>();
    }
}
