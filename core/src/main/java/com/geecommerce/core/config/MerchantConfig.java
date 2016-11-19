package com.geecommerce.core.config;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Constant;
import com.geecommerce.core.Str;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;

public enum MerchantConfig {
    GET;

    public static final String MERCHANT_PACKAGE = "Merchant.Package";

    public static final String MERCHANT_PERSISTENCE = "Merchant.Persistence";

    // -------------------------------------------------------------------------------------
    // MongoDB configuration
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

    // -------------------------------------------------------------------------------------
    // SQL configuration
    // -------------------------------------------------------------------------------------
    public static final String SQL_DRIVER = "sql.driver";

    public static final String SQL_URL = "sql.url";

    public static final String SQL_PROTOCOL = "sql.protocol";

    public static final String SQL_HOST = "sql.host";

    public static final String SQL_PORT = "sql.port";

    public static final String SQL_NAME = "sql.name";

    public static final String SQL_USER = "sql.user";

    public static final String SQL_PASS = "sql.pass";

    public static final String SQL_POOL_MIN_SIZE = "sql.pool.min_size";

    public static final String SQL_POOL_INCREMENT_BY = "sql.pool.increment_by";

    public static final String SQL_POOL_MAX_SIZE = "sql.pool.max_size";

    // -------------------------------------------------------------------------------------
    // Elasticsearch configuration
    // -------------------------------------------------------------------------------------
    public static final String ELASTICSEARCH_NODES = "elasticsearch.nodes";

    // -------------------------------------------------------------------------------------
    // Security configuration
    // -------------------------------------------------------------------------------------
    public static final String FRONTEND_SECURITY_SUGAR = "frontend.security.sugar";
    public static final String BACKEND_SECURITY_SUGAR = "backend.security.sugar";

    private Configuration loadMerchantConfig() {
        Configuration config = null;

        App app = App.get();
        ApplicationContext appCtx = (ApplicationContext) app.getApplicationContext();

        if (appCtx != null || System.getProperty("merchant.properties.path") != null) {
            try {
                String envMerchantPropertiesPath = System.getProperty("merchant.properties.path");

                if (envMerchantPropertiesPath != null && !"".equals(envMerchantPropertiesPath.trim())) {
                    config = new PropertiesConfiguration(new File(envMerchantPropertiesPath));
                } else if (appCtx.getMerchant() != null) {
                    File propertiesFile = new File(appCtx.getMerchant().getConfigurationPath(), app.isTestMode() ? Constant.MERCHANT_TEST_CONFIG_NAME : Constant.MERCHANT_CONFIG_NAME);

                    if (propertiesFile.exists()) {
                        config = new PropertiesConfiguration(propertiesFile);
                    } else {
                        throw new RuntimeException("FATAL ERROR: Merchant properties file '" + propertiesFile.getAbsolutePath() + "' not found.");
                    }
                } else {
                    throw new RuntimeException("FATAL ERROR: Unable to load merchant properties file!");
                }
            } catch (ConfigurationException e) {
                throw new RuntimeException("FATAL ERROR: Unable to load merchant configuration.", e);
            }
        } else {
            throw new RuntimeException("FATAL ERROR: Cannot load merchant configuration because ApplicationContext is null.");
        }

        return config;
    }

    public Configuration config() {
        App app = App.get();
        ApplicationContext appCtx = (ApplicationContext) app.getApplicationContext();

        String cacheKey = null;

        if (appCtx != null) {
            cacheKey = new StringBuilder("gc/merchant-configuration/").append(appCtx.getMerchant().getId().str()).toString();
        } else if (System.getProperty("merchant.properties.path") != null) {
            cacheKey = new StringBuilder("gc/merchant-configuration/").append(System.getProperty("merchant.properties.path").hashCode()).toString();
        }

        CacheManager cm = app.inject(CacheManager.class);
        Cache<String, Configuration> c = cm.getCache(getClass().getName());

        Configuration merchantConfig = c.get(cacheKey);

        if (merchantConfig == null) {
            merchantConfig = loadMerchantConfig();
            c.put(cacheKey, merchantConfig);
        }

        return merchantConfig;
    }

    public Map<String, String> values(String prefix) {
        return values(prefix, null);
    }

    public Map<String, String> values(String prefix, String regex) {
        Map<String, String> values = new HashMap<>();
        Iterator<String> keys = config().getKeys(prefix);

        while (keys.hasNext()) {
            String key = (String) keys.next();

            if (!Str.isEmpty(regex)) {
                if (key.matches(regex)) {
                    values.put(key, config().getString(key));
                }
            } else {
                values.put(key, config().getString(key));
            }
        }

        return values;
    }

    public static void main(String[] args) {
        System.out.println("mongodb.dma.connection_provider".matches("^mongodb.dma\\.[^\\.]+$"));

        System.out.println("mysql.pool.max_size".matches("^mysql\\.[^\\.]+$"));

    }

    public String val(String key) {
        return config().getString(key);
    }

    public String[] array(String key) {
        return config().getStringArray(key);
    }

    public int intVal(String key) {
        return config().containsKey(key) ? config().getInt(key) : 0;
    }

    public double doubleVal(String key) {
        return config().containsKey(key) ? config().getDouble(key) : 0;
    }

    public long longVal(String key) {
        return config().containsKey(key) ? config().getLong(key) : 0;
    }
}
