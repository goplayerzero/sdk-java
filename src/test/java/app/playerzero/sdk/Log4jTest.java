package app.playerzero.sdk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

public class Log4jTest {
    private final Logger logger = LogManager.getLogger(Log4jTest.class);

    @Test
    public void testLoggingEvents() {
        logger.info("Simple Message Test");
        logger.warn("Simple Message Test");
        logger.error("Something broke here", new IllegalStateException("Probably bad"));
    }
}
