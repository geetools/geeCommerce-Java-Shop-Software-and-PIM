package com.geecommerce.core.web.filter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.Constant;
import com.geecommerce.core.DefaultApplicationContext;
import com.geecommerce.core.Str;
import com.geecommerce.core.ThreadClearer;
import com.geecommerce.core.cache.ContextAwareCacheKeyWrapper;
import com.geecommerce.core.cache.ModuleCacheImpl;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.message.MessageBus;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.system.helper.UrlRewriteHelper;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.service.SystemService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Requests;
import com.geecommerce.core.web.DefaultServletRequestWrapper;
import com.geecommerce.core.web.DefaultServletResponseWrapper;
import com.geemodule.Geemodule;
import com.geemodule.api.Module;
import com.geemodule.api.ModuleCache;
import com.geemodule.api.ModuleLoader;
import com.geemvc.config.Configuration;
import com.geemvc.config.Configurations;

public class ApplicationInitFilter implements Filter {
    protected ServletContext servletContext;
    protected FilterConfig filterConfig;
    protected Logger log = null;

    private static final ModuleCache moduleCache = new ModuleCacheImpl();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");

    // private static final HashMap<ContextAwareCacheKeyWrapper<String>,
    // BootstrapStatus> initializationQueue = new HashMap<>();
    // private static final Map<String, String> maintenancePages = new
    // NullableConcurrentHashMap<>();

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        Enumeration<String> headers = httpRequest.getHeaders("Accept");

        ContextAwareCacheKeyWrapper<String> key = null;
        boolean isMaintenanceRedirectActive = false;
        int originalPriority = Thread.currentThread().getPriority();

        try {
            App app = App.get();

            initDefaultLogger();

            app.setServletContext(servletContext);

            // Make sure that geeMVC has access to the configuration at this
            // point.
            Configurations.copyFrom(servletContext);

            initApplicationContext(httpRequest);

            ApplicationContext appCtx = app.context();

            // Don't bother continuing if the application context is null;
            if (appCtx == null) {
                if (httpRequest.getRequestURI().startsWith("/error/")) {
                    httpResponse.getOutputStream().write("An error has occured. Please try again later.".getBytes());
                } else {
                    String info = getRequestInfo(httpRequest, httpResponse);
                    System.out.println(info);
                    System.out.println("Not processing request because applicationContext is null.");
                    httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
                }

                return;
            }
            /*
             * if(1 == 1) { Locale.setDefault(Locale.ENGLISH);
             * 
             * TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
             * 
             * String charset = app.getSystemCharset();
             * 
             * 
             * 
             * DefaultServletRequestWrapper requestWrapper = new
             * DefaultServletRequestWrapper(httpRequest, appCtx == null ? null :
             * appCtx.getRequestContext()); DefaultServletResponseWrapper
             * responseWrapper = new DefaultServletResponseWrapper(httpResponse,
             * appCtx == null ? null : appCtx.getRequestContext());
             * 
             * app.setServletRequest(requestWrapper);
             * app.setServletResponse(responseWrapper);
             * 
             * initMerchantLogger();
             * 
             * initConnections();
             * 
             * initModules(); initReflectionsProvider();
             * 
             * initMessageBus();
             * 
             * filterChain.doFilter(requestWrapper, responseWrapper); return; }
             */

            Locale.setDefault(Locale.ENGLISH);

            // TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

            String charset = app.getSystemCharset();

            httpRequest.setCharacterEncoding(charset);
            httpResponse.setCharacterEncoding(charset);

            String path = httpRequest.getRequestURI();

            // if (httpRequest.getRequestURI().contains("maintenance")) {
            // sendMaintenancePage(httpRequest.getRequestURI(), httpResponse,
            // charset);
            // return;
            // }

            // --------------------------------------------------------------------
            // Wrap request.
            // --------------------------------------------------------------------
            DefaultServletRequestWrapper requestWrapper = new DefaultServletRequestWrapper(httpRequest, appCtx == null ? null : appCtx.getRequestContext());
            DefaultServletResponseWrapper responseWrapper = new DefaultServletResponseWrapper(httpResponse, appCtx == null ? null : appCtx.getRequestContext());

            app.setServletRequest(requestWrapper);
            app.setServletResponse(responseWrapper);

            initRequestType();

            initSecurity();

            // ---------------------------------------------------------------
            // See if there are any url-patterns configured for skipping all
            // filters. This is desirable for loading static content like
            // images where we do not need stripes-filters etc.
            // ---------------------------------------------------------------

            String skipFiltersUrlPatterns = filterConfig.getInitParameter("SkipFilters.UrlPatterns");
            boolean skipFilters = false;

            if (!Str.isEmpty(skipFiltersUrlPatterns)) {
                String[] skipUrlPatterns = skipFiltersUrlPatterns.split(Str.COMMA);

                for (String skipUrlPattern : skipUrlPatterns) {
                    if (Requests.uriPatternMatches(path.toLowerCase(), skipUrlPattern.trim().toLowerCase())) {
                        skipFilters = true;
                        break;
                    }
                }
            }

            if (skipFilters) {
                request.getRequestDispatcher(path).forward(requestWrapper, responseWrapper);
            }
            // Process request normally.
            else {
                // --------------------------------------------------------------------
                // Initialize application context
                // --------------------------------------------------------------------
                if (appCtx.getMerchant() == null)
                    throw new RuntimeException("Merchant not found after it should have been initialited. RequestURL: " + httpRequest.getRequestURL());

                if (appCtx.getRequestContext() == null)
                    throw new RuntimeException("RequestContext not found after it should have been initialited. RequestURL: " + httpRequest.getRequestURL());

                // --------------------------------------------------------------------
                // Initialize logger.
                // --------------------------------------------------------------------
                initMerchantLogger();

                if (log.isTraceEnabled()) {
                    log.trace("Initializing app for: " + httpRequest.getRequestURL());
                }

                // String xpageParam = httpRequest.getParameter("xpage");
                //
                // if (xpageParam != null && xpageParam.equals("status"))
                // {
                // StringBuilder out = new StringBuilder();
                //
                // synchronized (initializationQueue)
                // {
                // Set<ContextAwareCacheKeyWrapper<String>> keys =
                // initializationQueue.keySet();
                //
                // for (ContextAwareCacheKeyWrapper<String> k : keys)
                // {
                // BootstrapStatus bStatus = initializationQueue.get(k);
                //
                // out.append(k.getReqCtxId().str())
                // .append("\t\t\t")
                // .append(k.getKey())
                // .append("\t\t\t")
                // .append(bStatus.name())
                // .append("\n");
                // }
                // }
                //
                // httpResponse.getWriter().write(out.toString());
                // return;
                // }
                // else
                // {
                // if ((path.startsWith("/cart/") || path.startsWith("/kosik/"))
                // && !app.isPostRequest())
                // {
                // key = new ContextAwareCacheKeyWrapper<>(path);
                // }
                // else
                // {
                // key = new ContextAwareCacheKeyWrapper<>(null);
                // }
                //
                // // System.out.println("----------------> " + path);
                //
                // BootstrapStatus bStatus = initializationQueue.get(key);
                //
                // if (bStatus == null || bStatus != BootstrapStatus.COMPLETE)
                // {
                // synchronized (initializationQueue)
                // {
                // bStatus = initializationQueue.get(key);
                //
                // if (initializationQueue.containsKey(key))
                // {
                // // System.out.println("[" + key.getReqCtxId() + "][" +
                // Thread.currentThread().getName()
                // // + "] 111-initializationIsInProgress() :: " +
                // initializationIsInProgress() + " -> " +
                // // appCtx.getRequestContext().getUrlPrefix() + " -> " +
                // initializationQueue.get(key) +
                // // " -> " + path + " -> " + key);
                //
                // // Stripes is already being initialized for some context.
                // if (initializationIsInProgress())
                // {
                // redirectToMaintenancePage(httpResponse);
                // isMaintenanceRedirectActive = true;
                //
                // return;
                // }
                // // Stripes is currently not being initialized. The first next
                // context can therefore
                // // start
                // // the
                // // next initialization.
                // else if (bStatus != BootstrapStatus.COMPLETE)
                // {
                // initializationQueue.put(key, BootstrapStatus.RUNNING);
                // }
                // }
                // else
                // {
                // // System.out.println("[" + key.getReqCtxId() + "][" +
                // Thread.currentThread().getName()
                // // + "] 222-initializationIsInProgress() :: " +
                // initializationIsInProgress() + " -> " +
                // // appCtx.getRequestContext().getUrlPrefix() + " -> " +
                // initializationQueue.get(key) +
                // // " -> " + path + " -> " + key);
                //
                // if (initializationIsInProgress())
                // {
                // initializationQueue.put(key, BootstrapStatus.WAITING);
                //
                // redirectToMaintenancePage(httpResponse);
                // isMaintenanceRedirectActive = true;
                //
                // return;
                // }
                // else if (bStatus != BootstrapStatus.COMPLETE)
                // {
                // initializationQueue.put(key, BootstrapStatus.RUNNING);
                // }
                // }
                //
                // // System.out.println("[" + key.getReqCtxId() + "][" +
                // Thread.currentThread().getName() +
                // // "] INITIALIZING APPLICATION FOR :::: " +
                // appCtx.getRequestContext().getUrlPrefix() +
                // // " -> " + initializationQueue.get(key) + " -> " + path + "
                // -> " + key);
                // }
                //
                // Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                // }
                // }

                // --------------------------------------------------------------------
                // Initialize application modules.
                // --------------------------------------------------------------------
                initModules();

                ModuleLoader loader = app.moduleLoader();

                if (loader == null)
                    throw new RuntimeException("ModuleLoader not found after it should have been initialited. RequestURL: " + httpRequest.getRequestURL());

                Collection<Module> modules = loader.getLoadedModules();

                if (modules == null || modules.size() == 0) {
                    log.warn("WARN: No modules loaded. Please check your project's modules folder.");
                }

                // --------------------------------------------------------------------
                // Initialize connections.
                // --------------------------------------------------------------------
                initConnections();

                // --------------------------------------------------------------------
                // Initialize MVC framework.
                // --------------------------------------------------------------------
                initGeeMVC();

                // --------------------------------------------------------------------
                // Initialize rewrite rule if one exists.
                // --------------------------------------------------------------------
                initURLRewrite(requestWrapper);

                // --------------------------------------------------------------------
                // Initialize thread local message bus.
                // --------------------------------------------------------------------
                initMessageBus();

                // app.setApplicationInitialized(initializationQueue.get(key)
                // == BootstrapStatus.COMPLETE);
                app.setApplicationInitialized(true);

                filterChain.doFilter(requestWrapper, responseWrapper);

                // HttpSession session = requestWrapper.getSession(false);
                //
                // Object customer = app.getLoggedInCustomer();
                //
                // System.out.println("Customer: " + customer);
                //
                // if (session != null)
                // {
                // long start = System.currentTimeMillis();
                // byte[] sessionBytes = null;
                // try
                // {
                // sessionBytes = JavaSerializer.serializeFrom(session);
                // }
                // catch (Throwable t)
                // {
                // System.out.println("EEEEEEEEEEEEEEEEEEE1::: " +
                // requestWrapper.getRequestURI());
                //
                // // TODO Auto-generated catch block
                // t.printStackTrace();
                // }
                // System.out.println("SERIALIZE #1: " +
                // (System.currentTimeMillis() - start));
                //
                // start = System.currentTimeMillis();
                // try
                // {
                // Map<String, ClassLoader> classLoaderMap = new
                // LinkedHashMap<>();
                // classLoaderMap.put("com.geecommerce.core",
                // getClass().getClassLoader());
                // classLoaderMap.put("com.google",
                // getClass().getClassLoader());
                // classLoaderMap.put("freemarker",
                // getClass().getClassLoader());
                //
                // JavaSerializer.deserializeInto(sessionBytes, session,
                // loader.getPublicClassLoader(classLoaderMap,
                // getClass().getClassLoader()));
                // }
                // catch (Throwable t)
                // {
                // System.out.println("EEEEEEEEEEEEEEEEEEE2::: " +
                // requestWrapper.getRequestURI() + " --- " +
                // t.getMessage());
                // t.printStackTrace();
                // }
                // System.out.println("DESERIALIZE #1: " +
                // (System.currentTimeMillis() - start));
                // }
            }
        } catch (Throwable t) {
            String info = getRequestInfo(httpRequest, httpResponse);

            System.out.println(info);

            t.printStackTrace();

            log.error(info + t.getMessage(), t);

            throw t;
        } finally {
            // if (!isMaintenanceRedirectActive && key != null)
            // {
            // synchronized (initializationQueue)
            // {
            // BootstrapStatus bStatus = initializationQueue.get(key);
            //
            // if (bStatus != null && bStatus == BootstrapStatus.RUNNING)
            // {
            // // System.out.println("[" + key.getReqCtxId() + "][" +
            // Thread.currentThread().getName() +
            // // "] SETTING TO COMPLETE!!! :::: " +
            // //
            // app.getApplicationContext().getRequestContext().getUrlPrefix() +
            // " -> " +
            // // initializationQueue.get(key) + " -> " +
            // app.getServletRequest().getRequestURI() + " -> " +
            // // key);
            //
            // initializationQueue.put(key, BootstrapStatus.COMPLETE);
            // Thread.currentThread().setPriority(originalPriority);
            //
            // // System.out.println(initializationQueue);
            // }
            // }
            // }

            Connections.closeSqlConnections();
            ThreadClearer.clear();
        }
    }

    protected void initGeeMVC() {
        // register the ReflectionsProvider for geeMVC to use.
        servletContext.setAttribute(Configuration.REFLECTIONS_PROVIDER_KEY, Reflect.getReflectionsProvider());
    }

    protected String getRequestInfo(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        HttpSession sess = httpRequest.getSession(false);

        Enumeration<String> headerNames = httpRequest.getHeaderNames();

        StringBuilder headers = new StringBuilder(Char.SQUARE_BRACKET_OPEN);

        if (headerNames != null) {
            int x = 0;
            while (headerNames.hasMoreElements()) {
                if (x > 0)
                    headers.append(Char.COMMA).append(Char.SPACE);

                String headerName = headerNames.nextElement();
                headers.append(headerName).append(Char.EQUALS).append(httpRequest.getHeader(headerName));

                x++;
            }
        }

        headers.append(Char.SQUARE_BRACKET_CLOSE);

        StringBuilder info = new StringBuilder();
        info.append("Exception in ApplicationInitFilter at: ").append(sdf.format(new Date())).append(Char.NEWLINE);
        info.append("Request URL: ").append(httpRequest.getRequestURL()).append(Char.NEWLINE);
        info.append("Request URI: ").append(httpRequest.getRequestURI()).append(Char.NEWLINE);
        info.append("Request QueryString: ").append(httpRequest.getQueryString()).append(Char.NEWLINE);
        info.append("Method: ").append(httpRequest.getMethod()).append(Char.NEWLINE);
        info.append("Session Id: ").append(sess == null ? null : sess.getId()).append(Char.NEWLINE);
        info.append("Thread: ").append(Thread.currentThread().getName()).append(Char.NEWLINE);
        info.append("Headers: ").append(headers.toString()).append(Char.NEWLINE);
        info.append("Remote Addr: ").append(httpRequest.getRemoteAddr()).append(Char.NEWLINE);

        Collection<String> respHeaderNames = httpResponse.getHeaderNames();

        StringBuilder respHeaders = new StringBuilder(Char.SQUARE_BRACKET_OPEN);

        if (respHeaderNames != null) {
            int x = 0;
            for (String headerName : respHeaderNames) {
                if (x > 0)
                    headers.append(Char.COMMA).append(Char.SPACE);

                respHeaders.append(headerName).append(Char.EQUALS).append(httpResponse.getHeader(headerName));

                x++;
            }
        }

        respHeaders.append(Char.SQUARE_BRACKET_CLOSE);

        info.append("Response Locale: ").append(httpResponse.getLocale()).append(Char.NEWLINE);
        info.append("Response Encoding: ").append(httpResponse.getCharacterEncoding()).append(Char.NEWLINE);
        info.append("Response Content-Type: ").append(httpResponse.getContentType()).append(Char.NEWLINE);
        info.append("Response Status: ").append(httpResponse.getStatus()).append(Char.NEWLINE);
        info.append("Response Commited: ").append(httpResponse.isCommitted()).append(Char.NEWLINE);
        info.append("Response Headers: ").append(respHeaders).append(Char.NEWLINE);

        ApplicationContext appCtx = App.get().context();

        if (appCtx != null) {
            Merchant m = appCtx.getMerchant();
            Store s = appCtx.getStore();
            View v = appCtx.getView();
            String l = appCtx.getLanguage();

            info.append("ApplicationContext: ").append(Char.SQUARE_BRACKET_OPEN);
            info.append("m=").append(m == null ? null : m.getCode()).append(Char.COMMA).append(Char.SPACE);
            info.append("s=").append(s == null ? null : s.getCode()).append(Char.COMMA).append(Char.SPACE);
            info.append("v=").append(v == null ? null : v.getCode()).append(Char.COMMA).append(Char.SPACE);
            info.append("l=").append(l == null ? null : l).append(Char.SQUARE_BRACKET_CLOSE).append(Char.NEWLINE);
        } else {
            info.append("ApplicationContext: null\n");
        }

        return info.toString();
    }

    protected void initRequestType() {
        App.get().initRequestType();
    }

    protected void initSecurity() {
        // if (app.isAPIRequest())
        // {
        // ServletContext servletContext = app.getServletContext();
        //
        // Ini ini = new Ini();
        // ini.load(servletContext.getResourceAsStream("/WEB-INF/conf/shiro.ini"));
        //
        // Factory<SecurityManager> factory = new
        // WebIniSecurityManagerFactory(ini);
        // SecurityManager securityManager = factory.getInstance();
        // ((DefaultWebSessionManager)securityManager).setSessionFactory(sessionFactory)
        //
        //
        // System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" +
        // securityManager.getClass().getName());
        //
        // SecurityUtils.setSecurityManager(securityManager);
        // }
    }

    protected void initConnections() {
        Connections.init();

        Id id = App.get().nextId();

        if (log.isDebugEnabled()) {
            log.debug("Generated test id: " + id);
        }

        Id seqNumber = App.get().nextSequenceNumber("boot_test_id");

        if (log.isDebugEnabled()) {
            log.debug("Generated test sequence number: " + seqNumber);
        }
    }

    protected void initDefaultLogger() {
        String logPath = App.get().systemConfig().val(Constant.BOOTSTRAP_LOGPATH);

        // Log4j does not like backslashes in directory path.
        logPath = logPath.replace(Char.BACKSLASH, Char.SLASH);

        ThreadContext.put("log.route.name", "CB-App-Rolling-Log");
        ThreadContext.put("log.path", logPath);

        log = LogManager.getLogger(getClass());
    }

    protected void initMerchantLogger() {
        ApplicationContext appCtx = App.get().context();

        Merchant merchant = appCtx.getMerchant();
        Store store = appCtx.getStore();

        String logPath = merchant.getLogPath();
        // Log4j does not like backslashes in directory path.
        logPath = logPath.replace(Char.BACKSLASH, Char.SLASH);

        ThreadContext.put("log.route.name", "CB-App-Rolling-Log");
        ThreadContext.put("log.path", logPath);
        ThreadContext.put("appctx.merchant", merchant.getCompanyName());
        ThreadContext.put("appctx.store", store.getName());

        log = LogManager.getLogger(getClass());
    }

    protected void initApplicationContext(HttpServletRequest httpRequest) {
        String host = Requests.getHost(httpRequest);

        SystemService systemService = App.get().systemService(SystemService.class);

        List<RequestContext> requestContexts = systemService.findRequestContextsForHost(host);
        String url = Requests.getURLWithoutPortAndContextPath(httpRequest);

        RequestContext foundRequestCtx = null;
        for (RequestContext requestCtx : requestContexts) {
            if (requestCtx.getUrlType().getUrlParser().isMatch(url,
                requestCtx)) {
                foundRequestCtx = requestCtx;
                break;
            }
        }

        if (foundRequestCtx != null) {
            Merchant merchant = systemService.findMerchantBy(foundRequestCtx.getMerchantId());

            ApplicationContext appCtx = new DefaultApplicationContext(foundRequestCtx, merchant);

            App.get().setApplicationContext(appCtx);
        }
    }

    protected void initModules() {
        ApplicationContext appCtx = App.get().context();

        ModuleLoader loader = Geemodule.createModuleLoader(appCtx.getMerchant().getModulesPath(), moduleCache);
        Collection<Module> modules = loader.getLoadedModules();

        if (modules != null && modules.size() > 0) {
            for (Module module : modules) {
                if (log.isDebugEnabled()) {
                    log.debug("Bootstrapped module '" + module.toUniqueId() + "'.");
                }
            }

            App.get().setModuleLoader(loader);
        }
    }

    protected void initURLRewrite(HttpServletRequest httpRequest) {
        String path = httpRequest.getRequestURI();

        UrlRewriteHelper helper = App.get().helper(UrlRewriteHelper.class);

        if (!helper.isExcludedFromURLRewriting(path)) {
            // Attempt to find a redirect in the database
            UrlRewrite urlRewrite = App.get().getUrlRewrite(path);

            if (urlRewrite != null) {
                DefaultApplicationContext appCtx = (DefaultApplicationContext) App.get().context();
                appCtx.setUrlRewrite(urlRewrite);
            }
        }
    }

    protected void initMessageBus() {
        MessageBus.registerSubscribers();
    }

    // private void redirectToMaintenancePage(HttpServletResponse httpResponse)
    // throws IOException {
    // if (app.isMediaRequest() || app.isAjaxRequest() || app.isAPIRequest())
    // {
    // httpResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
    // } else {
    // String path = Assets.skinViewPath("html/maintenance.html");
    //
    // if (path == null)
    // path = Assets.skinStorePath("html/maintenance.html");
    //
    // if (path != null) {
    // httpResponse.sendRedirect(path);
    // } else {
    // sendMaintenancePage(null, httpResponse, null);
    // }
    // }
    // }

    // private void sendMaintenancePage(String requestURI, HttpServletResponse
    // httpResponse, String charset) {
    // int originalPriority = Thread.currentThread().getPriority();
    // Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
    //
    // try {
    // Thread.sleep(3000);
    // } catch (InterruptedException e) {
    // }
    //
    // if (requestURI != null) {
    // String pageSource = maintenancePages.get(requestURI);
    //
    // if (pageSource == null) {
    // FileInputStream fis = null;
    //
    // try {
    // File f = new File(app.getBaseWebappPath(), requestURI);
    // fis = new FileInputStream(f);
    // pageSource = IOUtils.toString(fis, charset);
    //
    // String cachedPageSource = maintenancePages.putIfAbsent(requestURI,
    // pageSource);
    //
    // if (cachedPageSource != null)
    // pageSource = cachedPageSource;
    //
    // } catch (Throwable t) {
    // } finally {
    // IOUtils.closeQuietly(fis);
    // }
    // }
    //
    // if (pageSource != null) {
    // try {
    // httpResponse.setContentType("text/html");
    // httpResponse.getWriter().write(pageSource);
    // } catch (Throwable t) {
    // } finally {
    // Thread.currentThread().setPriority(originalPriority);
    // }
    // } else {
    // try {
    // httpResponse.setContentType("text/plain");
    // httpResponse.getWriter().write("Sorry! Maintenance is currently in
    // progress. Please try again later.");
    // } catch (Throwable t) {
    // } finally {
    // Thread.currentThread().setPriority(originalPriority);
    // }
    // }
    // } else {
    // try {
    // httpResponse.setContentType("text/plain");
    // httpResponse.getWriter().write("Sorry! Maintenance is currently in
    // progress. Please try again later.");
    // } catch (Throwable t) {
    // } finally {
    // Thread.currentThread().setPriority(originalPriority);
    // }
    // }
    //
    // }
    //
    // private boolean initializationIsInProgress() {
    // Set<ContextAwareCacheKeyWrapper<String>> keys =
    // initializationQueue.keySet();
    //
    // for (ContextAwareCacheKeyWrapper<String> key : keys) {
    // BootstrapStatus bStatus = initializationQueue.get(key);
    //
    // if (bStatus == BootstrapStatus.RUNNING)
    // return true;
    // }
    //
    // return false;
    // }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
        this.filterConfig = filterConfig;
    }
}
