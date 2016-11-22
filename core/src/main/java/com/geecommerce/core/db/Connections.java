package com.geecommerce.core.db;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Str;
import com.geecommerce.core.config.MerchantConfig;
import com.geecommerce.core.config.SystemConfig;
import com.geecommerce.core.db.annotation.Persistence;
import com.geecommerce.core.db.api.ConnectionProvider;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.google.inject.Singleton;

@Singleton
public class Connections {
    protected static ConnectionProvider systemConnectionProvider;

    protected static final Map<String, List<ConnectionProvider>> merchantConnectionProviders = new HashMap<>();

    protected static final Object SYSTEM_LOCK = new Object();
    protected static final Object MERCHANT_LOCK = new Object();

    protected static Set<Class<?>> connectionProviderTypes() {
        return Reflect.getTypesAnnotatedWith(Persistence.class, false);
    }

    protected static Set<Class<?>> systemConnectionProviderTypes() {
        return Reflect.getSystemTypesAnnotatedWith(Persistence.class, false);
    }

    @SuppressWarnings("unchecked")
    protected static Class<? extends ConnectionProvider> locateConnectionProviderType(String persistenceName) {

        Set<Class<?>> connectionProviderTypes = connectionProviderTypes();
        Class<? extends ConnectionProvider> systemConnectionProviderType = null;

        for (Class<?> type : connectionProviderTypes) {
            if (ConnectionProvider.class.isAssignableFrom(type)) {
                Persistence connAnnotation = type.getDeclaredAnnotation(Persistence.class);
                String annotatedName = connAnnotation.value();

                if (!Str.isEmpty(annotatedName) && annotatedName.equalsIgnoreCase(persistenceName)) {
                    systemConnectionProviderType = (Class<? extends ConnectionProvider>) type;
                    break;
                }
            }
        }

        return systemConnectionProviderType;
    }

    @SuppressWarnings("unchecked")
    protected static Class<? extends ConnectionProvider> locateSystemConnectionProviderType(String persistenceName) {

        Set<Class<?>> connectionProviderTypes = systemConnectionProviderTypes();
        Class<? extends ConnectionProvider> systemConnectionProviderType = null;

        for (Class<?> type : connectionProviderTypes) {
            if (ConnectionProvider.class.isAssignableFrom(type)) {
                Persistence connAnnotation = type.getDeclaredAnnotation(Persistence.class);
                String annotatedName = connAnnotation.value();

                if (!Str.isEmpty(annotatedName) && annotatedName.equalsIgnoreCase(persistenceName)) {
                    systemConnectionProviderType = (Class<? extends ConnectionProvider>) type;
                    break;
                }
            }
        }

        return systemConnectionProviderType;
    }

    public static void initSystemConnection() {
        if (systemConnectionProvider == null) {
            synchronized (SYSTEM_LOCK) {
                if (systemConnectionProvider == null) {
                    String systemPersistenceName = SystemConfig.GET.val(SystemConfig.SYSTEM_PERSISTENCE);
                    Map<String, String> persistenceProperties = SystemConfig.GET.values(systemPersistenceName);

                    Class<? extends ConnectionProvider> systemConnectionProviderType = locateSystemConnectionProviderType(
                        systemPersistenceName);

                    if (systemConnectionProviderType != null) {
                        try {
                            systemConnectionProvider = systemConnectionProviderType.newInstance();
                            systemConnectionProvider.init(systemPersistenceName, persistenceProperties);
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static void initMerchantConnections() {
        ApplicationContext appCtx = App.get().context();
        Merchant m = appCtx.getMerchant();

        // Just use the unique merchant path as the key.
        String key = m.getAbsoluteBaseSystemPath();

        List<ConnectionProvider> merchantConnectionProviderList = merchantConnectionProviders.get(key);

        if (merchantConnectionProviderList == null) {
            synchronized (MERCHANT_LOCK) {
                merchantConnectionProviderList = merchantConnectionProviders.get(key);

                if (merchantConnectionProviderList == null) {
                    merchantConnectionProviderList = new ArrayList<>();

                    String[] merchantPersistenceNames = MerchantConfig.GET.array(MerchantConfig.MERCHANT_PERSISTENCE);

                    for (String merchantPersistenceName : merchantPersistenceNames) {

                        String regex = new StringBuilder(Str.CARET).append(merchantPersistenceName)
                            .append("\\.[^\\.]+$").toString();

                        Map<String, String> connProperties = MerchantConfig.GET.values(merchantPersistenceName, regex);

                        System.out.println(connProperties);

                        Class<? extends ConnectionProvider> merchantConnectionProviderType = locateConnectionProviderType(
                            merchantPersistenceName);

                        if (merchantConnectionProviderType == null) {
                            String keyConnectionProvider = new StringBuilder(merchantPersistenceName).append(".connection_provider").toString();
                            String persistenceName = connProperties.get(keyConnectionProvider);

                            merchantConnectionProviderType = locateConnectionProviderType(persistenceName);
                        }

                        if (merchantConnectionProviderType != null) {
                            try {
                                ConnectionProvider merchantConnectionProvider = merchantConnectionProviderType.newInstance();
                                merchantConnectionProvider.init(merchantPersistenceName, connProperties);
                                merchantConnectionProviderList.add(merchantConnectionProvider);
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                            }

                            merchantConnectionProviders.putIfAbsent(key, merchantConnectionProviderList);
                        }
                    }
                }
            }
        }
    }

    public static void init() {
        initMerchantConnections();
    }

    public static void closeSqlConnections() {
        ApplicationContext appCtx = App.get().context();

        if (appCtx != null) {
            Merchant m = appCtx.getMerchant();
            String key = m.getAbsoluteBaseSystemPath();

            List<ConnectionProvider> connProviders = merchantConnectionProviders.get(key);

            if (connProviders != null && !connProviders.isEmpty()) {
                for (ConnectionProvider connectionProvider : connProviders) {
                    if (connectionProvider != null && "sql".equals(connectionProvider.group())) {
                        connectionProvider.close();
                    }
                }
            }
        }
    }

    public static void destroy() {
        // TODO
    }

    protected boolean isCoreClass(Class<? extends Model> modelClass) {
        return Reflect.hasCorePackagePrefix(modelClass);
    }

    protected boolean isSystemDatabase(Class<? extends Model> modelClass) {
        String[] systemPersistenceModels = SystemConfig.GET.array(SystemConfig.SYSTEM_PERSISTENCE_MODELS);
        Class<? extends Model> modelInterface = Reflect.getModelInterface(modelClass);
        return Arrays.asList(systemPersistenceModels).contains(modelInterface.getName());
    }

    public Object getConnection(Class<? extends Model> modelClass) {
        if (isCoreClass(modelClass) && isSystemDatabase(modelClass)) {
            return systemConnectionProvider.provide();
        } else {
            ApplicationContext appCtx = App.get().context();
            Merchant m = appCtx.getMerchant();

            String key = m.getAbsoluteBaseSystemPath();

            List<ConnectionProvider> merchantConnectionProviderList = merchantConnectionProviders.get(key);

            return merchantConnectionProviderList.get(0).provide();
        }
    }

    public Object getSystemConnection() {
        return systemConnectionProvider.provide();
    }

    public Object getConnection(String persistenceName) {
        ApplicationContext appCtx = App.get().context();
        Merchant m = appCtx.getMerchant();

        String key = m.getAbsoluteBaseSystemPath();

        List<ConnectionProvider> merchantConnectionProviderList = merchantConnectionProviders.get(key);

        for (ConnectionProvider connectionProvider : merchantConnectionProviderList) {
            if (!Str.isEmpty(connectionProvider.name()) && connectionProvider.name().equals(persistenceName))
                return connectionProvider.provide();
        }

        return null;
    }

    public Object getFirstConnection(String groupName) {
        if (groupName == null)
            return null;

        ApplicationContext appCtx = App.get().context();
        Merchant m = appCtx.getMerchant();

        String key = m.getAbsoluteBaseSystemPath();

        List<ConnectionProvider> merchantConnectionProviderList = merchantConnectionProviders.get(key);

        for (ConnectionProvider connectionProvider : merchantConnectionProviderList) {
            if (groupName.equals(connectionProvider.group())) {
                return connectionProvider.provide();
            }
        }

        return merchantConnectionProviderList.get(0).provide();
    }

    public Object getDefaultMerchantConnection() {
        ApplicationContext appCtx = App.get().context();
        Merchant m = appCtx.getMerchant();
        String key = m.getAbsoluteBaseSystemPath();

        return merchantConnectionProviders.get(key).get(0).provide();
    }

    public Connection getSqlConnection() {
        ApplicationContext appCtx = App.get().context();
        Merchant m = appCtx.getMerchant();
        String key = m.getAbsoluteBaseSystemPath();

        List<ConnectionProvider> connProviders = merchantConnectionProviders.get(key);

        for (ConnectionProvider connectionProvider : connProviders) {
            if ("sql".equals(connectionProvider.group())) {
                return (Connection) connectionProvider.provide();
            }
        }

        return null;
    }
}
