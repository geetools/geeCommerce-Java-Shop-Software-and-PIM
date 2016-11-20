package com.geecommerce.core.cache;

import java.io.File;

import javax.servlet.ServletContext;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.geecommerce.core.App;

public enum CacheProps {
    GET;

    private Configuration config = null;

    private CacheProps() {
        ServletContext servletCtx = App.get().servletContext();

        try {
            String cachePropertiesPath = System.getProperty("cache.properties.path");

            if (cachePropertiesPath != null && !"".equals(cachePropertiesPath.trim())) {
                config = new PropertiesConfiguration(new File(cachePropertiesPath));
            } else if (servletCtx != null) {
                File propertiesFile = new File(servletCtx.getRealPath("/WEB-INF/conf/Cache.properties"));

                if (propertiesFile.exists()) {
                    config = new PropertiesConfiguration(propertiesFile);
                } else {
                    throw new RuntimeException(
                        "FATAL ERROR: env properties file '" + propertiesFile.getAbsolutePath() + "' not found.");
                }
            } else {
                config = new PropertiesConfiguration("Cache.properties");
            }
        } catch (ConfigurationException e) {
            throw new RuntimeException("FATAL ERROR: Unable to load Cache.properties.", e);
        }
    }

    public String val(String key) {
        return config.getString(key);
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
}
