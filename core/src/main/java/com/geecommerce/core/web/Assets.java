package com.geecommerce.core.web;

import java.io.File;
import java.net.URL;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.cron.Environment;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.utils.Filenames;
import com.geemodule.api.Module;
import com.geemodule.api.ModuleClassLoader;
import com.geemodule.api.ModuleLoader;

public class Assets {
    private static final String MODULE_RELATIVE_WEB_PATH = "web/";
    private static final String MODULE_WEBPATH_PREFIX = "/m/%s/";

    private static final String JS_EXT = ".js";
    private static final String JS_BASE_PATH = "js/";
    private static final String SKIN_BASE_PATH = "skin/";
    private static final String RESOURCE_BASE_PATH = "resources/";

    private static final String JS_BASE_URI = "/js/";
    private static final String SKIN_BASE_URI = "/skin/";
    private static final String RESOURCE_BASE_URI = "/resources/";

    private static final String DEFAULT_BASE_SKIN_URI = "/skin/default/";
    private static final String MODULES = "modules";
    private static final String PAGES = "pages";

    private static final String CP_KEY_WEB_PATHS_CTX_FOLDER_PREFIX = "general/web/paths/ctx_folder_prefix";
    private static final String CP_KEY_WEB_PATHS_LOCATE_IN_WEBAPP = "general/web/paths/locate_in_webapp";
    private static final String CP_KEY_WEB_PATHS_LOCATE_IN_PROJECTS = "general/web/paths/locate_in_projects";

    public static final String skinModulePath(String relativeURI) {
        return skinModulePath(relativeURI, null);
    }

    public static final String skinModulePath(String relativeURI, String moduleCode) {
        return moduleAssetPath(new StringBuilder(SKIN_BASE_PATH).append(relativeURI).toString(), moduleCode);
    }

    public static final String jsModulePath(String relativeURI) {
        return jsModulePath(relativeURI, null);
    }

    public static final String jsModulePath(String relativeURI, String moduleCode) {
        return moduleAssetPath(new StringBuilder(JS_BASE_PATH).append(appendExtension(relativeURI)).toString(),
            moduleCode);
    }

    public static final String resourceModulePath(String relativeURI) {
        return resourceModulePath(relativeURI, null);
    }

    public static final String resourceModulePath(String relativeURI, String moduleCode) {
        return moduleAssetPath(new StringBuilder(RESOURCE_BASE_PATH).append(relativeURI).toString(), moduleCode);
    }

    public static final String moduleAssetPath(String relativeURI) {
        return moduleAssetPath(relativeURI, null);
    }

    public static final String moduleAssetPath(String relativeURI, String moduleCode) {
        if (moduleCode == null)
            moduleCode = getModuleCode();

        if (moduleCode == null)
            return null;

        ModuleLoader ml = App.get().moduleLoader();

        if (ml == null)
            return null;

        Module m = ml.getLoadedModuleByCode(moduleCode);

        if (m == null)
            return null;

        String webpath = null;

        if (m != null) {
            ModuleClassLoader mcl = (ModuleClassLoader) m.getModuleClassLoader();

            URL url = mcl.findResource(new StringBuilder(MODULE_RELATIVE_WEB_PATH).append(relativeURI).toString());

            // System.out.println("Trying: " + (url == null ? relativeURI :
            // url.toString()));

            if (url != null)
                webpath = new StringBuilder(String.format(MODULE_WEBPATH_PREFIX, moduleCode)).append(relativeURI)
                    .toString();
        }

        return webpath;
    }

    public static final String getModuleCode() {
        return (String) App.get().servletRequest().getAttribute("moduleCode");
    }

    private static final String appendExtension(String relativeURI) {
        if (relativeURI == null)
            return null;

        relativeURI = relativeURI.trim();

        return relativeURI.endsWith(JS_EXT) ? relativeURI : relativeURI + JS_EXT;
    }

    public static final String jsModuleViewPath(String relativeURI, String moduleCode) {
        if (moduleCode == null)
            moduleCode = getModuleCode();

        if (moduleCode == null)
            return null;

        return viewPath(App.get().getWebappJsPath(), App.get().getProjectJsPath(),
            new StringBuilder(MODULES).append(File.separatorChar).append(moduleCode).append(File.separatorChar)
                .append(JS_BASE_PATH).append(appendExtension(relativeURI)).toString());
    }

    public static final String jsModuleViewPath(String relativeURI, String moduleCode, boolean fromParentView) {
        if (moduleCode == null)
            moduleCode = getModuleCode();

        if (moduleCode == null)
            return null;

        return viewPath(App.get().getWebappJsPath(), App.get().getProjectJsPath(),
            new StringBuilder(MODULES).append(File.separatorChar).append(moduleCode).append(File.separatorChar)
                .append(JS_BASE_PATH).append(appendExtension(relativeURI)).toString(),
            fromParentView);
    }

    public static final String jsModuleStorePath(String relativeURI, String moduleCode) {
        if (moduleCode == null)
            moduleCode = getModuleCode();

        if (moduleCode == null)
            return null;

        return storePath(App.get().getWebappJsPath(), App.get().getProjectJsPath(),
            new StringBuilder(MODULES).append(File.separatorChar).append(moduleCode).append(File.separatorChar)
                .append(JS_BASE_PATH).append(appendExtension(relativeURI)).toString());
    }

    public static final String jsModuleStorePath(String relativeURI, String moduleCode, boolean fromParentView) {
        if (moduleCode == null)
            moduleCode = getModuleCode();

        if (moduleCode == null)
            return null;

        return storePath(App.get().getWebappJsPath(), App.get().getProjectJsPath(),
            new StringBuilder(MODULES).append(File.separatorChar).append(moduleCode).append(File.separatorChar)
                .append(JS_BASE_PATH).append(appendExtension(relativeURI)).toString(),
            fromParentView);
    }

    public static final String jsModuleMerchantPath(String relativeURI, String moduleCode) {
        if (moduleCode == null)
            moduleCode = getModuleCode();

        if (moduleCode == null)
            return null;

        return merchantPath(App.get().getWebappJsPath(), App.get().getProjectJsPath(),
            new StringBuilder(MODULES).append(File.separatorChar).append(moduleCode).append(File.separatorChar)
                .append(JS_BASE_PATH).append(appendExtension(relativeURI)).toString());
    }

    // String projectPagesJsPath = new
    // StringBuilder(PAGES).append(File.separatorChar)
    // .append(pagePath).toString();

    public static final String jsPageViewPath(String relativeURI) {
        return viewPath(App.get().getWebappJsPath(), App.get().getProjectJsPath(),
            new StringBuilder(PAGES).append(File.separatorChar).append(appendExtension(relativeURI)).toString());
    }

    public static final String jsPageViewPath(String relativeURI, boolean fromParentView) {
        return viewPath(App.get().getWebappJsPath(), App.get().getProjectJsPath(),
            new StringBuilder(PAGES).append(File.separatorChar).append(appendExtension(relativeURI)).toString(),
            fromParentView);
    }

    public static final String jsPageStorePath(String relativeURI) {
        return storePath(App.get().getWebappJsPath(), App.get().getProjectJsPath(),
            new StringBuilder(PAGES).append(File.separatorChar).append(appendExtension(relativeURI)).toString());
    }

    public static final String jsPageStorePath(String relativeURI, boolean fromParentView) {
        return storePath(App.get().getWebappJsPath(), App.get().getProjectJsPath(),
            new StringBuilder(PAGES).append(File.separatorChar).append(appendExtension(relativeURI)).toString(),
            fromParentView);
    }

    public static final String jsPageMerchantPath(String relativeURI) {
        return merchantPath(App.get().getWebappJsPath(), App.get().getProjectJsPath(),
            new StringBuilder(PAGES).append(File.separatorChar).append(appendExtension(relativeURI)).toString());
    }

    public static final String jsPageModulePath(String relativeURI) {
        return jsPageModulePath(relativeURI, null);
    }

    public static final String jsPageModulePath(String relativeURI, String moduleCode) {
        return jsModulePath(new StringBuilder(PAGES).append(Str.SLASH).append(appendExtension(relativeURI)).toString(),
            moduleCode);
    }

    public static final String jsPageDefaultPath(String relativeURI) {
        String jsWebPath = new StringBuilder(Str.SLASH).append(JS_BASE_PATH).append(PAGES).append(Str.SLASH)
            .append(appendExtension(relativeURI)).toString();
        File f = new File(App.get().getBaseWebappPath(), jsWebPath);

        if (f.exists()) {
            return jsWebPath;
        }

        return null;
    }

    public static final String jsDefaultPath(String relativeURI) {
        String jsWebPath = new StringBuilder(Str.SLASH).append(JS_BASE_PATH).append(appendExtension(relativeURI))
            .toString();
        File f = new File(App.get().getBaseWebappPath(), jsWebPath);

        if (f.exists()) {
            return jsWebPath;
        }

        return null;
    }

    public static final String skinModuleViewPath(String relativeURI, String moduleCode) {
        if (moduleCode == null)
            moduleCode = getModuleCode();

        if (moduleCode == null)
            return null;

        return viewPath(App.get().getWebappSkinPath(), App.get().getProjectSkinPath(),
            new StringBuilder(MODULES).append(File.separatorChar).append(moduleCode).append(File.separatorChar)
                .append(relativeURI).toString());
    }

    public static final String skinModuleViewPath(String relativeURI, String moduleCode, boolean fromParentView) {
        if (moduleCode == null)
            moduleCode = getModuleCode();

        if (moduleCode == null)
            return null;

        return viewPath(App.get().getWebappSkinPath(), App.get().getProjectSkinPath(),
            new StringBuilder(MODULES).append(File.separatorChar).append(moduleCode).append(File.separatorChar)
                .append(relativeURI).toString(),
            fromParentView);
    }

    public static final String skinViewPath(String relativeURI) {
        return viewPath(App.get().getWebappSkinPath(), App.get().getProjectSkinPath(), relativeURI);
    }

    public static final String skinViewPath(String relativeURI, boolean fromParentView) {
        return viewPath(App.get().getWebappSkinPath(), App.get().getProjectSkinPath(), relativeURI, fromParentView);
    }

    public static final String skinModuleStorePath(String relativeURI, String moduleCode) {
        if (moduleCode == null)
            moduleCode = getModuleCode();

        if (moduleCode == null)
            return null;

        return storePath(App.get().getWebappSkinPath(), App.get().getProjectSkinPath(),
            new StringBuilder(MODULES).append(File.separatorChar).append(moduleCode).append(File.separatorChar)
                .append(relativeURI).toString());
    }

    public static final String skinModuleStorePath(String relativeURI, String moduleCode, boolean fromParentView) {
        if (moduleCode == null)
            moduleCode = getModuleCode();

        if (moduleCode == null)
            return null;

        return storePath(App.get().getWebappSkinPath(), App.get().getProjectSkinPath(),
            new StringBuilder(MODULES).append(File.separatorChar).append(moduleCode).append(File.separatorChar)
                .append(relativeURI).toString(),
            fromParentView);
    }

    public static final String skinStorePath(String relativeURI) {
        return storePath(App.get().getWebappSkinPath(), App.get().getProjectSkinPath(), relativeURI);
    }

    public static final String skinStorePath(String relativeURI, boolean fromParentView) {
        return storePath(App.get().getWebappSkinPath(), App.get().getProjectSkinPath(), relativeURI, fromParentView);
    }

    public static final String skinModuleMerchantPath(String relativeURI, String moduleCode) {
        if (moduleCode == null)
            moduleCode = getModuleCode();

        if (moduleCode == null)
            return null;

        return merchantPath(App.get().getWebappSkinPath(), App.get().getProjectSkinPath(),
            new StringBuilder(MODULES).append(File.separatorChar).append(moduleCode).append(File.separatorChar)
                .append(relativeURI).toString());
    }

    public static final String skinMerchantPath(String relativeURI) {
        return merchantPath(App.get().getWebappSkinPath(), App.get().getProjectSkinPath(), relativeURI);
    }

    public static final String skinDefaultPath(String relativeURI) {
        // System.out.println(new
        // StringBuilder(DEFAULT_BASE_SKIN_URI).append(relativeURI).toString());
        return new StringBuilder(DEFAULT_BASE_SKIN_URI).append(relativeURI).toString();
    }

    public static final String jsViewPath(String relativeURI) {
        return jsViewPath(relativeURI, false);
    }

    public static final String jsViewPath(String relativeURI, boolean fromParentStore) {
        return viewPath(App.get().getWebappJsPath(), App.get().getProjectJsPath(), appendExtension(relativeURI),
            fromParentStore);
    }

    public static final String jsStorePath(String relativeURI) {
        return jsStorePath(relativeURI, false);
    }

    public static final String jsStorePath(String relativeURI, boolean fromParentStore) {
        return storePath(App.get().getWebappJsPath(), App.get().getProjectJsPath(), appendExtension(relativeURI),
            fromParentStore);
    }

    public static final String jsMerchantPath(String relativeURI) {
        return merchantPath(App.get().getWebappJsPath(), App.get().getProjectJsPath(), appendExtension(relativeURI));
    }

    public static final String resourceViewPath(String relativeURI) {
        return viewPath(App.get().getWebappResourcePath(), App.get().getProjectResourcePath(), relativeURI);
    }

    public static final String resourceStorePath(String relativeURI) {
        return storePath(App.get().getWebappResourcePath(), App.get().getProjectResourcePath(), relativeURI);
    }

    public static final String resourceMerchantPath(String relativeURI) {
        return merchantPath(App.get().getWebappResourcePath(), App.get().getProjectResourcePath(), relativeURI);
    }

    public static final String viewPath(String webappBasePath, String projectsBasePath, String relativeURI) {
        return viewPath(webappBasePath, projectsBasePath, relativeURI, false);
    }

    public static final String viewPath(String webappBasePath, String projectsBasePath, String relativeURI,
        boolean fromParentView) {
        ApplicationContext appCtx = App.get().context();
        Merchant m = appCtx.getMerchant();
        Store s = m.getStoreFor(appCtx.getRequestContext());
        View v = m.getViewFor(appCtx.getRequestContext());

        if (fromParentView && v.getParentViewId() != null) {
            v = m.getView(v.getParentViewId());
        } else if (fromParentView && v.getParentViewId() == null) {
            return null;
        }

        if (isLocateInProjects()) {
            StringBuilder path = new StringBuilder(projectsBasePath).append(File.separatorChar)
                .append(ctxFolderPrefix())
                .append(Filenames.ensureSafeName(s.getCode() != null ? s.getCode() : s.getName(), true))
                .append(File.separatorChar).append(ctxFolderPrefix())
                .append(Filenames.ensureSafeName(v.getCode() != null ? v.getCode() : v.getName(), true))
                .append(File.separatorChar).append(relativeURI);

            File f = new File(path.toString());
            // System.out.println("Trying: " + f.getAbsolutePath());

            if (f.exists()) {
                return toProjectURI(f.getAbsolutePath());
            }
        }

        if (isLocateInWebapp()) {
            StringBuilder path = new StringBuilder(webappBasePath).append(File.separatorChar).append(ctxFolderPrefix())
                .append(Filenames.ensureSafeName(m.getCode() != null ? m.getCode() : m.getCompanyName(), true))
                .append(File.separatorChar).append(ctxFolderPrefix())
                .append(Filenames.ensureSafeName(s.getCode() != null ? s.getCode() : s.getName(), true))
                .append(File.separatorChar).append(ctxFolderPrefix())
                .append(Filenames.ensureSafeName(v.getCode() != null ? v.getCode() : v.getName(), true))
                .append(File.separatorChar).append(relativeURI);

            File f = new File(path.toString());
            // System.out.println("Trying: " + f.getAbsolutePath());

            if (f.exists()) {
                return toWebappURI(f.getAbsolutePath());
            }
        }

        return null;
    }

    public static final String storePath(String webappBasePath, String projectsBasePath, String relativeURI) {
        return storePath(webappBasePath, projectsBasePath, relativeURI, false);
    }

    public static final String storePath(String webappBasePath, String projectsBasePath, String relativeURI,
        boolean fromParentStore) {
        ApplicationContext appCtx = App.get().context();
        Merchant m = appCtx.getMerchant();
        Store s = m.getStoreFor(appCtx.getRequestContext());

        if (fromParentStore && s.getParentStoreId() != null) {
            s = m.getStore(s.getParentStoreId());
        } else if (fromParentStore && s.getParentStoreId() == null) {
            return null;
        }

        if (isLocateInProjects()) {
            StringBuilder path = new StringBuilder(projectsBasePath).append(File.separatorChar)
                .append(ctxFolderPrefix())
                .append(Filenames.ensureSafeName(s.getCode() != null ? s.getCode() : s.getName(), true))
                .append(File.separatorChar).append(relativeURI);

            File f = new File(path.toString());
            // System.out.println("Trying: " + f.getAbsolutePath());

            if (f.exists()) {
                return toProjectURI(f.getAbsolutePath());
            }
        }

        if (isLocateInWebapp()) {
            StringBuilder path = new StringBuilder(webappBasePath).append(File.separatorChar).append(ctxFolderPrefix())
                .append(Filenames.ensureSafeName(m.getCode() != null ? m.getCode() : m.getCompanyName(), true))
                .append(File.separatorChar).append(ctxFolderPrefix())
                .append(Filenames.ensureSafeName(s.getCode() != null ? s.getCode() : s.getName(), true))
                .append(File.separatorChar).append(relativeURI);

            File f = new File(path.toString());
            // System.out.println("Trying: " + f.getAbsolutePath());

            if (f.exists()) {
                return toWebappURI(f.getAbsolutePath());
            }
        }

        return null;
    }

    public static final String merchantPath(String webappBasePath, String projectsBasePath, String relativeURI) {
        ApplicationContext appCtx = App.get().context();
        Merchant m = appCtx.getMerchant();

        if (isLocateInProjects()) {
            StringBuilder path = new StringBuilder(projectsBasePath).append(File.separatorChar).append(relativeURI);

            File f = new File(path.toString());
            // System.out.println("Trying: " + f.getAbsolutePath());

            if (f.exists()) {
                return toProjectURI(f.getAbsolutePath());
            }
        }

        if (isLocateInWebapp()) {
            StringBuilder path = new StringBuilder(webappBasePath).append(File.separatorChar).append(ctxFolderPrefix())
                .append(Filenames.ensureSafeName(m.getCode() != null ? m.getCode() : m.getCompanyName(), true))
                .append(File.separatorChar).append(relativeURI);

            File f = new File(path.toString());
            // System.out.println("Trying: " + f.getAbsolutePath());

            if (f.exists()) {
                return toWebappURI(f.getAbsolutePath());
            }
        }

        return null;
    }

    public static final String toWebappURI(String systemPath) {
        String webpath = systemPath.replace(Char.BACKSLASH, Char.SLASH);

        if (webpath.contains(SKIN_BASE_URI)) {
            return webpath.substring(webpath.indexOf(SKIN_BASE_URI));
        } else if (webpath.contains(JS_BASE_URI)) {
            return webpath.substring(webpath.indexOf(JS_BASE_URI));
        } else if (webpath.contains(RESOURCE_BASE_URI)) {
            return webpath.substring(webpath.indexOf(RESOURCE_BASE_URI));
        } else {
            return null;
        }
    }

    public static final String toProjectURI(String systemPath) {
        String projectWebPath = App.get().getProjectWebPath().replace(Char.BACKSLASH, Char.SLASH);

        File f = new File(projectWebPath);
        String baseProjectsDir = f.getParentFile().getParent().replace(Char.BACKSLASH, Char.SLASH);

        return systemPath.replace(Char.BACKSLASH, Char.SLASH).replace(baseProjectsDir, Str.EMPTY);
    }

    private static final String ctxFolderPrefix() {
        return App.get().cpStr_(CP_KEY_WEB_PATHS_CTX_FOLDER_PREFIX, Str.UNDERSCORE);
    }

    private static final boolean isLocateInWebapp() {
        return App.get().cpBool_(CP_KEY_WEB_PATHS_LOCATE_IN_WEBAPP, false);
    }

    private static final boolean isLocateInProjects() {
        return App.get().cpBool_(CP_KEY_WEB_PATHS_LOCATE_IN_PROJECTS, true);
    }
}
