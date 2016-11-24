package com.geecommerce.core.service.persistence;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.javers.common.properties.PropertyConfiguration;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.config.MerchantConfig;
import com.geecommerce.core.config.SystemConfig;
import com.geecommerce.core.db.annotation.Persistence;
import com.geecommerce.core.inject.SystemInjector;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.RepositorySupport;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.model.ConfigurationProperty;
import com.geemodule.api.Module;
import com.geemodule.api.ModuleClassLoader;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultPersistenceProvider implements PersistenceProvider {
    @Inject
    protected App app;

    protected Map<String, RepositorySupport> repositorySupportMapping = new ConcurrentHashMap<>();
    protected Map<String, Dao> daoMapping = new ConcurrentHashMap<>();
    protected Map<String, String> modelPersistenceMapping = new ConcurrentHashMap<>();

    protected static final String KEY_PERSISTENCE_MODEL = "persistence/model:%s/use";
    protected static final String KEY_PERSISTENCE_MODEL_REGEX = "persistence\\/model:\\^";
    protected static final String KEY_PERSISTENCE_MODULE = "persistence/module:%s/use";

    protected static final String CONFIG_PROPERTY_TYPE_NAME = ConfigurationProperty.class.getName();

    protected Set<Class<?>> persistenceTypes() {
        return Reflect.getTypesAnnotatedWith(Persistence.class, false);
    }

    @SuppressWarnings("unchecked")
    protected Class<? extends RepositorySupport> locateRepositorySupportType(String persistenceName,
        Class<? extends Model> modelClass) {
        Set<Class<?>> repositorySupportTypes = persistenceTypes();
        Class<? extends RepositorySupport> repositorySupportType = null;

        for (Class<?> type : repositorySupportTypes) {
            if (RepositorySupport.class.isAssignableFrom(type)) {
                Persistence persistenceAnnotation = type.getDeclaredAnnotation(Persistence.class);
                String annotatedName = name(persistenceAnnotation);

                if (!Str.isEmpty(annotatedName) && annotatedName.equals(persistenceName)) {
                    Class<? extends Model> annotatedModel = persistenceAnnotation.model();
                    String annotatedModuleName = persistenceAnnotation.module();

                    if (!(annotatedModel == Model.class || annotatedModel == modelClass))
                        continue;

                    if (!Str.isEmpty(annotatedModuleName) && !annotatedModuleName.equals(moduleName(modelClass)))
                        continue;

                    repositorySupportType = (Class<? extends RepositorySupport>) type;
                    break;
                }
            }
        }

        return repositorySupportType;
    }

    @SuppressWarnings("unchecked")
    protected Class<? extends Dao> locateDaoType(String persistenceName, Class<? extends Model> modelClass) {
        Set<Class<?>> daoTypes = persistenceTypes();
        Class<? extends Dao> daoType = null;

        for (Class<?> type : daoTypes) {
            if (Dao.class.isAssignableFrom(type)) {
                Persistence persistenceAnnotation = type.getDeclaredAnnotation(Persistence.class);
                String annotatedName = name(persistenceAnnotation);

                if (!Str.isEmpty(annotatedName) && annotatedName.equals(persistenceName)) {
                    Class<? extends Model> annotatedModel = persistenceAnnotation.model();
                    String annotatedModuleName = persistenceAnnotation.module();

                    if (!(annotatedModel == Model.class || annotatedModel == modelClass))
                        continue;

                    if (!Str.isEmpty(annotatedModuleName) && !annotatedModuleName.equals(moduleName(modelClass)))
                        continue;

                    daoType = (Class<? extends Dao>) type;
                    break;
                }
            }
        }

        return daoType;
    }

    protected String moduleName(Class<? extends Model> modelClass) {
        ClassLoader cl = Reflect.getModuleClassLoader(modelClass);

        if (cl != null) {
            ModuleClassLoader mcl = (ModuleClassLoader) cl;
            Module m = mcl.getModule();
            return m.getCode();
        }

        return null;
    }

    protected String name(Persistence persistenceAnnotation) {
        if (persistenceAnnotation == null)
            return null;

        return !Str.isEmpty(persistenceAnnotation.value()) ? persistenceAnnotation.value()
            : persistenceAnnotation.name();
    }

    protected boolean isCoreClass(Class<? extends Model> modelClass) {
        return Reflect.hasCorePackagePrefix(modelClass);
    }

    protected boolean isSystemModel(Class<? extends Model> modelClass) {
        String[] systemPersistenceModels = SystemConfig.GET.array(SystemConfig.SYSTEM_PERSISTENCE_MODELS);

        Class<? extends Model> modelInterface = Reflect.getModelInterface(modelClass);

        return Arrays.asList(systemPersistenceModels).contains(modelInterface.getName());
    }

    protected String systemPersistenceName() {
        return SystemConfig.GET.val(SystemConfig.SYSTEM_PERSISTENCE);
    }

    protected String merchantPersistenceName(Class<? extends Model> modelClass) {

        String persistenceName = modelPersistenceMapping.get(modelClass.getName());

        if (persistenceName == null) {
            
            if (!CONFIG_PROPERTY_TYPE_NAME.equals(modelClass.getName())) {
                
                persistenceName = app.cpStr_(String.format(KEY_PERSISTENCE_MODEL, modelClass.getName()));

                if (persistenceName == null) {
                    List<ConfigurationProperty> configProperties = app.getConfigProperties(KEY_PERSISTENCE_MODEL_REGEX);

                    if (configProperties != null && !configProperties.isEmpty()) {
                        for (ConfigurationProperty cp : configProperties) {
                            String key = cp.getKey();
                            String regex = key.substring(key.indexOf(Char.CARET), key.lastIndexOf("/use"));

                            if (modelClass.getName().matches(regex)) {
                                persistenceName = cp.getStringValue();
                            }
                        }
                    }

                    if (persistenceName == null) {
                        ClassLoader cl = Reflect.getModuleClassLoader(modelClass);

                        if (cl != null) {
                            ModuleClassLoader mcl = (ModuleClassLoader) cl;
                            Module m = mcl.getModule();

                            if (m != null) {
                                persistenceName = app.cpStr_(String.format(KEY_PERSISTENCE_MODULE, m.getCode()));
                            }
                        }
                    }
                }
            }

            if (persistenceName == null) {
                String[] persistenceNames = MerchantConfig.GET.array(MerchantConfig.MERCHANT_PERSISTENCE);
                persistenceName = persistenceNames == null || persistenceNames.length == 0 ? null : persistenceNames[0];
            }

            modelPersistenceMapping.put(modelClass.getName(), persistenceName);
        }

        return persistenceName;
    }

    @Override
    public boolean isCompatible(Class<?> clazz) {
        Persistence persistenceAnnotation = clazz.getDeclaredAnnotation(Persistence.class);
        String annotatedName = name(persistenceAnnotation);
        String persistenceName = null;

        if (!Str.isEmpty(annotatedName)) {
            if (persistenceAnnotation.model() != Model.class) {
                persistenceName = merchantPersistenceName(persistenceAnnotation.model());
            } else {
                String[] persistenceNames = MerchantConfig.GET.array(MerchantConfig.MERCHANT_PERSISTENCE);
                persistenceName = persistenceNames == null || persistenceNames.length == 0 ? null : persistenceNames[0];
            }
        }

        return persistenceName != null && persistenceName.equals(annotatedName);
    }

    protected RepositorySupport repositorySupport(String persistenceName, Dao dao, Class<? extends Model> modelClass) {
        Class<? extends RepositorySupport> repositorySupportType = locateRepositorySupportType(persistenceName,
            modelClass);

        if (repositorySupportType == null)
            throw new IllegalStateException(
                "Unable to find a RepositorySupport class for the persistance name '" + persistenceName
                    + "'. Make sure that your RepositorySupport is annotated with @RepositorySupport and @Persistence(\""
                    + persistenceName + "\").");

        RepositorySupport repositorySupport = SystemInjector.get().getInstance(repositorySupportType);

        if (dao != null)
            repositorySupport.dao(dao);

        return repositorySupport;
    }

    protected Dao dao(String persistenceName, Class<? extends Model> modelClass) {
        Class<? extends Dao> daoType = locateDaoType(persistenceName, modelClass);

        if (daoType == null)
            throw new IllegalStateException("Unable to find a Dao class for the persistance name '" + persistenceName
                + "'. Make sure that your Dao is annotated with @Dao and @Persistence(\"" + persistenceName
                + "\").");

        return SystemInjector.get().getInstance(daoType);
    }

    public RepositorySupport provideRepositorySupport(Class<? extends Model> modelClass, Dao dao) {
        modelClass = Reflect.getModelInterface(modelClass);

        RepositorySupport repositorySupport = repositorySupportMapping.get(modelClass.getName());

        // If we do not know yet, we will have to find out and cache the result.
        if (repositorySupport == null) {
            // Are we dealing with a system model class (RequestContext,
            // Merchant ...)?
            if (isCoreClass(modelClass) && isSystemModel(modelClass)) {
                String systemPersistenceName = systemPersistenceName();

                if (Str.isEmpty(systemPersistenceName))
                    throw new IllegalStateException(
                        "You must provide a system persistence configuration in System.properties");

                RepositorySupport newRepositorySupport = repositorySupport(systemPersistenceName, dao, modelClass);

                repositorySupport = repositorySupportMapping.putIfAbsent(modelClass.getName(), newRepositorySupport);

                if (repositorySupport == null)
                    repositorySupport = newRepositorySupport;
            } else {
                String merchantPersistenceName = merchantPersistenceName(modelClass);

                if (Str.isEmpty(merchantPersistenceName))
                    throw new IllegalStateException(
                        "You must provide a merchant persistence configuration in Merchant.properties");

                RepositorySupport newRepositorySupport = repositorySupport(merchantPersistenceName, dao, modelClass);

                repositorySupport = repositorySupportMapping.putIfAbsent(modelClass.getName(), newRepositorySupport);

                if (repositorySupport == null)
                    repositorySupport = newRepositorySupport;
            }
        }

        return repositorySupport;
    }

    @Override
    public Dao provideDao(Class<? extends Model> modelClass) {
        modelClass = Reflect.getModelInterface(modelClass);

        Dao dao = daoMapping.get(modelClass.getName());

        // If we do not know yet, we will have to find out and cache the result.
        if (dao == null) {
            // Are we dealing with a system model class (RequestContext,
            // Merchant ...)?
            if (isCoreClass(modelClass) && isSystemModel(modelClass)) {
                String systemConnectionName = systemPersistenceName();

                if (Str.isEmpty(systemConnectionName))
                    throw new IllegalStateException(
                        "You must provide a system database configuration in System.properties");

                Dao newDao = dao(systemConnectionName, modelClass);

                dao = daoMapping.putIfAbsent(modelClass.getName(), newDao);

                if (dao == null)
                    dao = newDao;
            } else {
                String merchantPersistenceName = merchantPersistenceName(modelClass);

                if (Str.isEmpty(merchantPersistenceName))
                    throw new IllegalStateException(
                        "You must provide a merchant persistence configuration in Merchant.properties");

                Dao newDao = dao(merchantPersistenceName, modelClass);

                dao = daoMapping.putIfAbsent(modelClass.getName(), newDao);

                if (dao == null)
                    dao = newDao;
            }
        }

        return dao;
    }
}
