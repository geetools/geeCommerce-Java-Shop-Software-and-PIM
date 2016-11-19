package com.geecommerce.core.template.freemarker;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Str;
import com.geecommerce.core.inject.ModuleInjector;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.template.Templates;
import com.geecommerce.core.template.freemarker.directive.AttributeDirective;
import com.geecommerce.core.template.freemarker.directive.AttributeEqualsDirective;
import com.geecommerce.core.template.freemarker.directive.AttributeExistsDirective;
import com.geecommerce.core.template.freemarker.directive.AttributeHasValueDirective;
import com.geecommerce.core.template.freemarker.directive.BundleDirective;
import com.geecommerce.core.template.freemarker.directive.CacheDirective;
import com.geecommerce.core.template.freemarker.directive.ConditionDirective;
import com.geecommerce.core.template.freemarker.directive.ConfigurationDirective;
import com.geecommerce.core.template.freemarker.directive.FlushDirective;
import com.geecommerce.core.template.freemarker.directive.GetDirective;
import com.geecommerce.core.template.freemarker.directive.ImportDirective;
import com.geecommerce.core.template.freemarker.directive.JsDirective;
import com.geecommerce.core.template.freemarker.directive.JsonDirective;
import com.geecommerce.core.template.freemarker.directive.MessageDirective;
import com.geecommerce.core.template.freemarker.directive.ParamDirective;
import com.geecommerce.core.template.freemarker.directive.PrintDirective;
import com.geecommerce.core.template.freemarker.directive.SEODirective;
import com.geecommerce.core.template.freemarker.directive.SessionDirective;
import com.geecommerce.core.template.freemarker.directive.SetDirective;
import com.geecommerce.core.template.freemarker.directive.SkinDirective;
import com.geecommerce.core.template.freemarker.directive.TimeDirective;
import com.geecommerce.core.template.freemarker.directive.TruncateDirective;
import com.geecommerce.core.template.freemarker.directive.URLDirective;
import com.geecommerce.core.template.freemarker.directive.WaitDirective;
import com.geecommerce.core.template.freemarker.directive.WidgetWrapperDirective;
import com.geecommerce.core.util.NullableConcurrentHashMap;
import com.geecommerce.core.utils.Annotations;
import com.geecommerce.core.web.annotation.Directive;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.WidgetController;
import com.geemodule.api.Module;
import com.geemodule.api.ModuleLoader;

import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.AllHttpScopesHashModel;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.IncludePage;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class FreemarkerHelper {
    public static final String KEY_REQUEST = "Request";
    public static final String KEY_INCLUDE = "include_page";
    public static final String KEY_REQUEST_PRIVATE = "__FreeMarkerServlet.Request__";
    public static final String KEY_REQUEST_PARAMETERS = "RequestParameters";
    public static final String KEY_SESSION = "Session";
    public static final String KEY_APPLICATION = "Application";
    public static final String KEY_APPLICATION_PRIVATE = "__FreeMarkerServlet.Application__";
    public static final String KEY_JSP_TAGLIBS = "JspTaglibs";

    // Note these names start with dot, so they're essentially invisible from
    // a freemarker script.
    private static final String ATTR_REQUEST_MODEL = ".freemarker.Request";
    private static final String ATTR_REQUEST_PARAMETERS_MODEL = ".freemarker.RequestParameters";
    private static final String ATTR_SESSION_MODEL = ".freemarker.Session";
    private static final String ATTR_APPLICATION_MODEL = ".freemarker.Application";
    private static final String ATTR_JSP_TAGLIBS_MODEL = ".freemarker.JspTaglibs";

    private static final FreemarkerCache freemarkerCache = new FreemarkerCache();

    private static final String KEY_WIDGET_CONTROLLERS = "gc/widget/controllers";

    private static final Map<String, Configuration> cache = new NullableConcurrentHashMap<>();

    public static Configuration newConfig(ServletContext servletContext, Module module) {
        try {
            App app = App.get();

            String key = module == null ? "default" : module.toUniqueId();

            Configuration conf = cache.get(key);

            if (conf == null) {
                if (conf == null) {
                    conf = new Configuration();

                    // Make sure that error handling is more strict in dev mode.
                    // if (!app.isDevPrintErrorMessages())
                    conf.setTemplateExceptionHandler((TemplateExceptionHandler) new FreemarkerExceptionHandler());

                    ApplicationContext appCtx = App.get().getApplicationContext();

                    if (appCtx != null) {
                        RequestContext reqCtx = appCtx.getRequestContext();
                        conf.setLocale(new Locale(reqCtx.getLanguage(), reqCtx.getCountry()));
                        conf.setLocalizedLookup(true);
                    }

                    conf.setCacheStorage(freemarkerCache);

                    conf.setDefaultEncoding("UTF-8");

                    conf.setSharedVariable("t_includes", Templates.getIncludesPath());
                    conf.setSharedVariable("t_layout", Templates.getLayoutPath());
                    conf.setSharedVariable("t_pages", Templates.getPagesPath());

                    conf.setSharedVariable("set", new SetDirective());
                    conf.setSharedVariable("get", app.inject(GetDirective.class));
                    conf.setSharedVariable("cp", new ConfigurationDirective());
                    conf.setSharedVariable("url", app.inject(URLDirective.class));
                    conf.setSharedVariable("seo", new SEODirective());
                    conf.setSharedVariable("attribute", new AttributeDirective());
                    conf.setSharedVariable("attribute_exists", new AttributeExistsDirective());
                    conf.setSharedVariable("attribute_equals", new AttributeEqualsDirective());
                    conf.setSharedVariable("attribute_has_value", new AttributeHasValueDirective());
                    conf.setSharedVariable("import", new ImportDirective());
                    conf.setSharedVariable("message", new MessageDirective());
                    conf.setSharedVariable("param", new ParamDirective());
                    conf.setSharedVariable("print", new PrintDirective());
                    conf.setSharedVariable("json", new JsonDirective());
                    conf.setSharedVariable("skin", new SkinDirective());
                    conf.setSharedVariable("js", new JsDirective());
                    conf.setSharedVariable("bundle", new BundleDirective());
                    conf.setSharedVariable("truncate", new TruncateDirective());
                    conf.setSharedVariable("session", new SessionDirective());
                    conf.setSharedVariable("wait", new WaitDirective());
                    conf.setSharedVariable("flush", new FlushDirective());
                    conf.setSharedVariable("time", new TimeDirective());
                    conf.setSharedVariable("cache", app.inject(CacheDirective.class));
                    conf.setSharedVariable("when", new ConditionDirective());

                    registerWidgets(conf, servletContext);
                    registerDirectives(conf, servletContext);

                    // Template loaders.
                    TemplateLoader defaultTemplateLoader = conf.getTemplateLoader();
                    TemplateLoader freemarkerModuleTemplateLoader = new FreemarkerModuleTemplateLoader(module);
                    TemplateLoader freemarkerMerchantTemplateLoader = new FreemarkerMerchantTemplateLoader();

                    List<TemplateLoader> loaders = new ArrayList<TemplateLoader>();
                    loaders.add(freemarkerMerchantTemplateLoader);

                    if (module != null)
                        loaders.add(freemarkerModuleTemplateLoader);

                    loaders.add(defaultTemplateLoader);

                    if (defaultTemplateLoader == null || !(defaultTemplateLoader instanceof WebappTemplateLoader)) {
                        TemplateLoader webappTemplateLoader = new WebappTemplateLoader(servletContext);
                        loaders.add(webappTemplateLoader);
                    }

                    FreemarkerMultiTemplateLoader allTemplateLoaders = new FreemarkerMultiTemplateLoader(loaders.toArray(new TemplateLoader[loaders.size()]));

                    conf.setTemplateLoader(allTemplateLoaders);

                    Configuration cachedConf = cache.putIfAbsent(key, conf);

                    if (cachedConf != null)
                        conf = cachedConf;
                }
            }

            return conf;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected static void registerWidgets(Configuration conf, ServletContext servletContext) throws ServletException, IOException {
        // ---------------------------------------------------------------
        // Locate widgets, wrap them in a freemarker directive
        // and add them to the freemarker conf as a shared variable.
        // ---------------------------------------------------------------

        ModuleLoader loader = App.get().getModuleLoader();
        Class<WidgetController>[] foundClasses = (Class<WidgetController>[]) loader.findAllTypesAnnotatedWith(Widget.class, false);

        if (foundClasses != null && foundClasses.length > 0) {
            for (Class<WidgetController> foundClass : foundClasses) {
                try {
                    Annotation declaredAnnotation = Annotations.declaredAnnotation(foundClass, Widget.class);

                    if (declaredAnnotation != null) {
                        // get directive name from annotation (this is the tag
                        // name that will be used in the template)
                        String directiveName = ((Widget) declaredAnnotation).value();

                        if (Str.isEmpty(directiveName))
                            directiveName = ((Widget) declaredAnnotation).name();

                        // Wrap the widget in a freemarker compatible directive
                        WidgetWrapperDirective widgetDirective = new WidgetWrapperDirective(foundClass);

                        if (!Str.isEmpty(directiveName)) {
                            // Add it to the configuration as a shared variable
                            conf.setSharedVariable(directiveName, widgetDirective);
                        }
                    }
                } catch (Throwable t) {
                    RuntimeException re = new RuntimeException(t);
                    re.setStackTrace(t.getStackTrace());
                    throw re;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected static void registerDirectives(Configuration conf, ServletContext servletContext) throws ServletException, IOException {
        // ---------------------------------------------------------------
        // Locate directives and add them to the freemarker conf.
        // ---------------------------------------------------------------

        ModuleLoader loader = App.get().getModuleLoader();
        Class<WidgetController>[] foundClasses = (Class<WidgetController>[]) loader.findAllTypesAnnotatedWith(Directive.class, false);

        if (foundClasses != null && foundClasses.length > 0) {
            for (Class<WidgetController> foundClass : foundClasses) {
                try {
                    Annotation declaredAnnotation = Annotations.declaredAnnotation(foundClass, Directive.class);

                    if (declaredAnnotation != null) {
                        // get directive name from annotation (this is the tag
                        // name that will be used in the template)
                        String directiveName = ((Directive) declaredAnnotation).value();

                        if (Str.isEmpty(directiveName))
                            directiveName = ((Directive) declaredAnnotation).name();

                        if (!Str.isEmpty(directiveName)) {
                            // Add it to the configuration as a shared variable
                            conf.setSharedVariable(directiveName, ModuleInjector.get().getInstance(foundClass));
                        }
                    }
                } catch (Throwable t) {
                    RuntimeException re = new RuntimeException(t);
                    re.setStackTrace(t.getStackTrace());
                    throw re;
                }
            }
        }
    }

    public static TemplateModel createModel(ObjectWrapper wrapper, ServletContext servletContext, final HttpServletRequest request, final HttpServletResponse response) throws TemplateModelException {
        AllHttpScopesHashModel params = new AllHttpScopesHashModel(wrapper, servletContext, request);

        // Create hash model wrapper for servlet context (the application)
        ServletContextHashModel servletContextModel = (ServletContextHashModel) servletContext.getAttribute(ATTR_APPLICATION_MODEL);

        if (servletContextModel == null) {
            servletContextModel = new ServletContextHashModel(new RequestServlet(), wrapper);
            servletContext.setAttribute(ATTR_APPLICATION_MODEL, servletContextModel);
            TaglibFactory taglibs = new TaglibFactory(servletContext);
            servletContext.setAttribute(ATTR_JSP_TAGLIBS_MODEL, taglibs);
        }

        params.putUnlistedModel(KEY_APPLICATION, servletContextModel);
        params.putUnlistedModel(KEY_APPLICATION_PRIVATE, servletContextModel);
        params.putUnlistedModel(KEY_JSP_TAGLIBS, (TemplateModel) servletContext.getAttribute(ATTR_JSP_TAGLIBS_MODEL));

        // Create hash model wrapper for session
        HttpSessionHashModel sessionModel;
        HttpSession session = request.getSession(false);

        if (session != null) {
            sessionModel = (HttpSessionHashModel) session.getAttribute(ATTR_SESSION_MODEL);
            if (sessionModel == null) {
                sessionModel = new HttpSessionHashModel(session, wrapper);
                session.setAttribute(ATTR_SESSION_MODEL, sessionModel);
            }
        } else {
            sessionModel = new HttpSessionHashModel(null, request, response, wrapper);
        }

        params.putUnlistedModel(KEY_SESSION, sessionModel);

        // Create hash model wrapper for request
        HttpRequestHashModel requestModel = (HttpRequestHashModel) request.getAttribute(ATTR_REQUEST_MODEL);

        if (requestModel == null || requestModel.getRequest() != request) {
            requestModel = new HttpRequestHashModel(request, response, wrapper);
            request.setAttribute(ATTR_REQUEST_MODEL, requestModel);
            request.setAttribute(ATTR_REQUEST_PARAMETERS_MODEL, new HttpRequestParametersHashModel(request));
        }

        params.putUnlistedModel(KEY_REQUEST, requestModel);
        params.putUnlistedModel(KEY_INCLUDE, new IncludePage(request, response));
        params.putUnlistedModel(KEY_REQUEST_PRIVATE, requestModel);

        // Create hash model wrapper for request parameters
        HttpRequestParametersHashModel requestParametersModel = (HttpRequestParametersHashModel) request.getAttribute(ATTR_REQUEST_PARAMETERS_MODEL);
        params.putUnlistedModel(KEY_REQUEST_PARAMETERS, requestParametersModel);

        return params;
    }

    public static String requestUrlToTemplatePath(HttpServletRequest request) {
        // First, see if it is an included request
        String includeServletPath = (String) request.getAttribute("javax.servlet.include.servlet_path");

        if (includeServletPath != null) {
            // Try path info; only if that's null (servlet is mapped to an
            // URL extension instead of to prefix) use servlet path.
            String includePathInfo = (String) request.getAttribute("javax.servlet.include.path_info");

            return includePathInfo == null ? includeServletPath : includePathInfo;
        }
        // Seems that the servlet was not called as the result of a
        // RequestDispatcher.include(...). Try pathInfo then servletPath again,
        // only now directly on the request object:
        String path = request.getPathInfo();

        if (path != null)
            return path;
        path = request.getServletPath();

        if (path != null)
            return path;

        // Seems that it is a servlet mapped with prefix, and there was no extra
        // path info.
        return "";
    }
}
