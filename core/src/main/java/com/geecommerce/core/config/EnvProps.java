package com.geecommerce.core.config;

import java.io.File;

import javax.servlet.ServletContext;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.geecommerce.core.App;

public enum EnvProps {
    GET;

    private Configuration config = null;

    // -------------------------------------------------------------------------------------
    // Environment configuration
    // -------------------------------------------------------------------------------------

    public static final String VERSION = "version";
    public static final String SERVER = "server";
    public static final String MODE = "mode";
    public static final String MODE_DEV = "dev";
    public static final String MODE_TEST = "test";
    public static final String MODE_PRODUCTION = "prod";

    private EnvProps() {
        ServletContext servletCtx = App.get().getServletContext();

        try {
            String envPropertiesPath = System.getProperty("env.properties.path");

            if (envPropertiesPath != null && !"".equals(envPropertiesPath.trim())) {
                config = new PropertiesConfiguration(new File(envPropertiesPath));
            } else if (servletCtx != null) {
                File propertiesFile = new File(servletCtx.getRealPath("/WEB-INF/conf/Environment.properties"));

                if (propertiesFile.exists()) {
                    config = new PropertiesConfiguration(propertiesFile);
                } else {
                    throw new RuntimeException("FATAL ERROR: env properties file '" + propertiesFile.getAbsolutePath() + "' not found.");
                }
            } else {
                config = new PropertiesConfiguration("Environment.properties");
            }
        } catch (ConfigurationException e) {
            throw new RuntimeException("FATAL ERROR: Unable to load Environment.properties.", e);
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
