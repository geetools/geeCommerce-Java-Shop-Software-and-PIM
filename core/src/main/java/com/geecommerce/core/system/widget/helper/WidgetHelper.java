package com.geecommerce.core.system.widget.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.inject.SystemInjector;
import com.geecommerce.core.system.widget.model.WidgetParameter;
import com.geecommerce.core.util.NullableConcurrentHashMap;
import com.geecommerce.core.web.Assets;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geemodule.api.ModuleClassLoader;
import com.geemodule.api.ModuleLoader;
import com.mongodb.util.JSON;

public class WidgetHelper {
    private static final Logger log = LogManager.getLogger(SystemInjector.class);

    private static final String WIDGET_JS_DEFAULT_FILEPATH = "widgets/%s/%s";
    private static final String WIDGET_JS_SCRIPT_TAG = "<script>require.config({ paths: { '%s/widgets/%s' : '/m/%s/js/widgets/%s' }});require(['%s'], function(widget){if(widget && widget.init) widget.init(%s)});</script>";

    private static final String WIDGET_SKIN_STYLES_FILEPATH = "widgets/%s/css/%s.css";

    private static final String WIDGET_SKIN_STYLES_TAG = "<script>require(['jquery'], function ($) {$('head').append('<link href=\"%s\" rel=\"stylesheet\" />')});</script>";
    // private static final String WIDGET_SKIN_STYLES_TAG = "<link href=\"%s\"
    // rel=\"stylesheet\" />";

    private static final String CACHE_NAME = "gc/widget/paths";
    private static final String KEY_WIDGET_CONTROLLERS = "gc/widget/controllers";

    private static final Map<String, List<WidgetController>> cache = new NullableConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static List<WidgetController> locateWidgets() {
        long startx = System.currentTimeMillis();
        if (log.isTraceEnabled()) {
            log.trace("ENTER locateWidgets()");
        }

        List<WidgetController> widgetControllers = cache.get(KEY_WIDGET_CONTROLLERS);

        if (widgetControllers == null) {
            widgetControllers = new ArrayList<>();

            App app = App.get();

            ApplicationContext appCtx = app.getApplicationContext();

            if (appCtx != null) {
                // Find widget controllers in modules
                ModuleLoader loader = app.getModuleLoader();

                Class<WidgetController>[] types = (Class<WidgetController>[]) loader.findAllTypesAnnotatedWith(Widget.class, false);

                for (Class<WidgetController> type : types) {
                    WidgetController widgetControllerInstance = null;

                    try {
                        widgetControllerInstance = app.inject(type);

                        if (widgetControllerInstance != null && widgetControllerInstance.isCmsEnabled())
                            widgetControllers.add(widgetControllerInstance);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }

            List<WidgetController> cachedWidgetControllers = cache.putIfAbsent(KEY_WIDGET_CONTROLLERS, widgetControllers);

            if (cachedWidgetControllers != null)
                widgetControllers = cachedWidgetControllers;
        }

        if (log.isTraceEnabled()) {
            log.trace("EXIT locateWidgets() - " + (System.currentTimeMillis() - startx));
        }

        return widgetControllers;
    }

    public static void loadOptions(List<WidgetController> widgetControllers) {
        for (WidgetController widgetController : widgetControllers) {
            for (WidgetParameter widgetParameter : widgetController.getParameters()) {

            }
        }
    }

    public static WidgetController findWidgetByCode(String widgetCode) {
        if (widgetCode == null)
            return null;

        List<WidgetController> widgetControllers = locateWidgets();

        WidgetController foundWidgetController = null;

        for (WidgetController widgetController : widgetControllers) {
            if (widgetCode.equals(widgetController.getCode())) {
                foundWidgetController = widgetController;
                break;
            }
        }

        return foundWidgetController;
    }

    public static String getJsScriptTag(WidgetController widgetController, WidgetContext ctx) {
        if (widgetController == null)
            return null;

        String widgetJsPath = getWidgetJsPath(widgetController, ctx);

        String moduleCode = null;
        String webpath = null;

        ModuleClassLoader mcl = (ModuleClassLoader) widgetController.getClass().getClassLoader();

        if (mcl != null)
            moduleCode = mcl.getModule().getCode();

        String widgetCode = widgetController.getCode();

        if (moduleCode != null && widgetCode != null) {
            String jsParams = JSON.serialize(ctx.getJsParams());
            return widgetJsPath == null ? null : String.format(WIDGET_JS_SCRIPT_TAG, moduleCode, widgetCode, moduleCode, widgetCode, widgetJsPath, jsParams);
        }

        // "<script>require.config({ paths: { '%s/widgets/%s' :
        // '/m/%s/js/widgets/%s' }});require(['%s']);</script>";

        return null;
    }

    public static String getSkinStylesTag(WidgetController widgetController, WidgetContext ctx) {
        if (widgetController == null)
            return null;

        String defaultWidgetView = widgetController.getDefaultView();
        String defaultWidgetStylesPath = getWidgetStylesPath(widgetController, ctx, defaultWidgetView);
        String defaultWidgetStyles = defaultWidgetStylesPath == null ? null : String.format(WIDGET_SKIN_STYLES_TAG, defaultWidgetStylesPath);

        String widgetView = widgetController.getView(ctx);
        if (!defaultWidgetView.equals(widgetView)) {
            String widgetStylesPath = getWidgetStylesPath(widgetController, ctx, widgetView);
            String widgetStyles = widgetStylesPath == null ? null : String.format(WIDGET_SKIN_STYLES_TAG, widgetStylesPath);

            return Stream.of(defaultWidgetStyles, widgetStyles).filter(s -> s != null).collect(Collectors.joining());
        }

        return defaultWidgetStyles;
    }

    public static String getWidgetJsPath(WidgetController widgetController, WidgetContext ctx) {
        if (widgetController == null)
            return null;

        String moduleCode = null;
        String webpath = null;

        ModuleClassLoader mcl = (ModuleClassLoader) widgetController.getClass().getClassLoader();

        if (mcl != null)
            moduleCode = mcl.getModule().getCode();

        String widgetCode = widgetController.getCode();
        String widgetView = widgetController.getView(ctx);

        if (moduleCode != null && widgetCode != null) {
            CacheManager cm = App.get().inject(CacheManager.class);
            Cache<String, String> c = cm.getCache(CACHE_NAME);

            String relativeWidgetJsPath = String.format(WIDGET_JS_DEFAULT_FILEPATH, widgetCode, widgetView);

            webpath = c.get(relativeWidgetJsPath);

            if (webpath == null) {
                // ---------------------------------------------------------------------------
                // First we check if the file has been overridden in the
                // project's web folder.
                // ---------------------------------------------------------------------------

                webpath = Assets.jsModuleViewPath(relativeWidgetJsPath, moduleCode);

                if (webpath == null) {
                    webpath = Assets.jsModuleStorePath(relativeWidgetJsPath, moduleCode);
                }

                if (webpath == null) {
                    webpath = Assets.jsModuleMerchantPath(relativeWidgetJsPath, moduleCode);
                }

                if (webpath == null) {
                    webpath = Assets.jsModuleViewPath(relativeWidgetJsPath, moduleCode, true);
                }

                if (webpath == null) {
                    webpath = Assets.jsModuleStorePath(relativeWidgetJsPath, moduleCode, true);
                }

                if (webpath == null) {
                    webpath = Assets.jsModulePath(relativeWidgetJsPath, moduleCode);
                }

                c.put(relativeWidgetJsPath, webpath);
            }
        }

        return webpath;
    }

    public static String getWidgetStylesPath(WidgetController widgetController, WidgetContext ctx, String widgetView) {
        if (widgetController == null)
            return null;

        String moduleCode = null;
        String webpath = null;

        ModuleClassLoader mcl = (ModuleClassLoader) widgetController.getClass().getClassLoader();

        if (mcl != null)
            moduleCode = mcl.getModule().getCode();

        String widgetCode = widgetController.getCode();
        // String widgetView = widgetController.getView(ctx);

        if (moduleCode != null && widgetCode != null) {
            CacheManager cm = App.get().inject(CacheManager.class);
            Cache<String, String> c = cm.getCache(CACHE_NAME);

            String relativeWidgetStylesPath = String.format(WIDGET_SKIN_STYLES_FILEPATH, widgetCode, widgetView);

            webpath = c.get(relativeWidgetStylesPath);

            if (webpath == null) {
                // ---------------------------------------------------------------------------
                // First we check if the file has been overridden in the
                // project's web folder.
                // ---------------------------------------------------------------------------

                webpath = Assets.skinModuleViewPath(relativeWidgetStylesPath, moduleCode);

                if (webpath == null) {
                    webpath = Assets.skinModuleStorePath(relativeWidgetStylesPath, moduleCode);
                }

                if (webpath == null) {
                    webpath = Assets.skinModuleMerchantPath(relativeWidgetStylesPath, moduleCode);
                }

                if (webpath == null) {
                    webpath = Assets.skinModuleViewPath(relativeWidgetStylesPath, moduleCode, true);
                }

                if (webpath == null) {
                    webpath = Assets.skinModuleStorePath(relativeWidgetStylesPath, moduleCode, true);
                }

                if (webpath == null) {
                    webpath = Assets.skinModulePath(relativeWidgetStylesPath, moduleCode);
                }

                c.put(relativeWidgetStylesPath, webpath);
            }
        }

        return webpath;
    }
}
