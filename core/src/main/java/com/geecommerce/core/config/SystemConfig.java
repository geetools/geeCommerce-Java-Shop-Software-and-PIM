package com.geecommerce.core.config;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.geecommerce.core.AppRegistry;
import com.geecommerce.core.RegistryKey;

public enum SystemConfig {
    GET;

    private Configuration config = null;

    // -------------------------------------------------------------------------------------
    // Application configuration
    // -------------------------------------------------------------------------------------

    public static final String APPLICATION_WEBAPP_PATH = "Application.Webapp.Path";

    public static final String APPLICATION_PROJECTS_PATH = "Application.Projects.Path";

    public static final String APPLICATION_RESOURCES_PATH = "Application.Resources.Path";

    public static final String APPLICATION_CERTS_PATH = "Application.Certs.Path";

    public static final String APPLICATION_TEMPLATE_PATH = "Application.Template.Path";

    public static final String APPLICATION_TEMPLATE_SUFFIX = "Application.Template.Suffix";

    public static final String SYSTEM_PERSISTENCE = "System.Persistence";

    public static final String SYSTEM_PERSISTENCE_MODELS = "System.Persistence.Models";

    // -------------------------------------------------------------------------------------
    // MongoDB system database configuration
    // -------------------------------------------------------------------------------------

    public static final String MONGODB_HOST = "mongodb.host";

    public static final String MONGODB_NAME = "mongodb.name";

    public static final String MONGODB_PORT = "mongodb.port";

    public static final String MONGODB_USER = "mongodb.user";

    public static final String MONGODB_PASS = "mongodb.pass";

    public static final String MONGODB_DMA_HOST = "mongodb.dma.host";

    public static final String MONGODB_DMA_NAME = "mongodb.dma.name";

    public static final String MONGODB_DMA_PORT = "mongodb.dma.port";

    public static final String MONGODB_DMA_USER = "mongodb.dma.user";

    public static final String MONGODB_DMA_PASS = "mongodb.dma.pass";

    public static final String IDGENERATOR_DATACENTER_ID = "idgenerator.datacenter_id";

    public static final String IDGENERATOR_WORKER_ID = "idgenerator.worker_id";

    public static final String IDGENERATOR_NUM_TRIES = "idgenerator.num_tries";

    public static final String IDGENERATOR_WAIT_UNTIL_RETRY = "idgenerator.wait_until_retry";

    public static final String ELASTICSEARCH_PATH = "elasticsearch.path";

    public static final String IMAGEMAGICK_PATH = "imagemagick.path";

    private SystemConfig() {
        ServletContext servletCtx = AppRegistry.get(RegistryKey.SERVLET_CONTEXT.key());

        try {
            String envSystemPropertiesPath = System.getProperty("system.properties.path");

            if (envSystemPropertiesPath != null && !"".equals(envSystemPropertiesPath.trim())) {
                config = new PropertiesConfiguration(new File(envSystemPropertiesPath));
            } else if (servletCtx != null) {
                File propertiesFile = new File(servletCtx.getRealPath("/WEB-INF/conf/System.properties"));

                if (propertiesFile.exists()) {
                    config = new PropertiesConfiguration(propertiesFile);
                } else {
                    throw new RuntimeException("FATAL ERROR: System properties file '"
                        + propertiesFile.getAbsolutePath() + "' not found.");
                }
            } else {
                config = new PropertiesConfiguration("System.properties");
            }
        } catch (ConfigurationException e) {
            throw new RuntimeException("FATAL ERROR: Unable to load System configuration.", e);
        }
    }

    public Map<String, String> values(String prefix) {
        Map<String, String> values = new HashMap<>();
        Iterator<String> keys = config.getKeys(prefix);

        while (keys.hasNext()) {
            String key = (String) keys.next();
            values.put(key, config.getString(key));
        }

        return values;
    }

    public String val(String key) {
        return config.getString(key);
    }

    public String val(String key, String defaultVal) {
        String val = config.getString(key);

        return val == null ? defaultVal : val;
    }

    public String[] array(String key) {
        return config.getStringArray(key);
    }

    public int intVal(String key) {
        return config.getInt(key);
    }

    public double doubleVal(String key) {
        return config.getDouble(key);
    }

    public long longVal(String key) {
        return config.getLong(key);
    }

    public boolean booleanVal(String key) {
        return config.getBoolean(key);
    }

    public boolean booleanVal(String key, boolean defaultValue) {
        return config.getBoolean(key, defaultValue);
    }
}
