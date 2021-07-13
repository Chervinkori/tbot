package chweb.ru.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author chervinko <br>
 * 07.07.2021
 */
public final class Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    private static final Properties props = new Properties();

    private static final String DEFAULT_CONFIG = "config.properties";

    private Configuration() {
    }

    public static void init() throws Exception {
        try (InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG)) {
            if (inputStream == null) {
                throw new FileNotFoundException(String.format("Property file '%s' not found in the classpath", DEFAULT_CONFIG));
            }
            props.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            LOGGER.info("Loading configuration successfully completed");
        } catch (Exception exp) {
            throw new Exception("Error loading configuration: " + exp.getMessage(), exp);
        }
    }

    public static String getProperty(String key) {
        return props.getProperty(key);
    }
}
