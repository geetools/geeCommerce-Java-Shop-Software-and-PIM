package com.geecommerce.core.web;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.config.SystemConfig;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.template.Templates;
import com.geecommerce.core.template.freemarker.FreemarkerConstant;
import com.geecommerce.core.template.freemarker.FreemarkerHelper;
import com.geecommerce.core.type.ContextObjects;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Requests;
import com.geemodule.api.Module;
import com.geemodule.api.ModuleClassLoader;
import com.geemodule.util.Strings;
import com.geemvc.Results;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.controller.StripesConstants;
import net.sourceforge.stripes.util.CryptoUtil;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.ValidationErrors;

public abstract class BaseActionBean implements ActionBean {
    @Inject
    protected App app;

    private static final String FREEMARKER_TEMPLATE_SUFFIX = ".ftl";

    private ActionBeanContext context;

    private Id id = null;

    private Integer requestCounter;
    private Long requestTime;

    public HttpServletRequest getRequest() {
        return context.getRequest();
    }

    public HttpServletResponse getResponse() {
        return context.getResponse();
    }

    public String getOriginalURI() {
        return app.getOriginalURI();
    }

    public String getOriginalQueryString() {
        return app.getOriginalQueryString();
    }

    public String getBaseTemplatesPath() {
        return Templates.getBaseTemplatesPath();
    }

    public final String getPagesPath() {
        return Templates.getPagesPath();
    }

    public final String getSlicesPath() {
        return Templates.getSlicesPath();
    }

    public final String getIncludesPath() {
        return Templates.getIncludesPath();
    }

    protected Resolution view(String path) {
        return view(path, null);
    }

    protected Result view2(String path) {
        return view2(path, null);
    }

    protected Resolution view(String path, String cacheFor) {
        app.setViewPath(path);
        app.setActionURI(getRequest().getRequestURI());

        String templateSuffix = SystemConfig.GET.val(SystemConfig.APPLICATION_TEMPLATE_SUFFIX);

        if (templateSuffix == null) {
            throw new IllegalStateException(
                "The System.properties configuration element 'Application.Template.Suffix' cannot be null");
        }

        templateSuffix = templateSuffix.trim();

        if (cacheFor != null) {
            // Set cache header for caching server.
            getResponse().setHeader("X-CB-Cache-Page", cacheFor);
        }

        if (path.startsWith("/WEB-INF/")) {
            if (FREEMARKER_TEMPLATE_SUFFIX.equalsIgnoreCase(".ftl")) {
                // return freemarkerTemplateStream(path);
                return new ForwardResolution(path);
            } else {
                return new ForwardResolution(path);
            }
        } else {
            if (FREEMARKER_TEMPLATE_SUFFIX.equalsIgnoreCase(".ftl")) {
                // return freemarkerTemplateStream(new
                // StringBuilder(getPagesPath()).append("/").append(path).append(templateSuffix).toString());
                return new ForwardResolution(
                    new StringBuilder(getPagesPath()).append("/").append(path).append(templateSuffix).toString());
            } else {
                return new ForwardResolution(
                    new StringBuilder(getPagesPath()).append("/").append(path).append(templateSuffix).toString());
            }
        }
    }

    protected Result view2(String path, String cacheFor) {
        app.setViewPath(path);
        app.setActionURI(getRequest().getRequestURI());

        if (cacheFor != null) {
            // Set cache header for caching server.
            getResponse().setHeader("X-CB-Cache-Page", cacheFor);
        }

        return Results.view(path);
    }

    protected Resolution redirect(String path) {
        if (!StringUtils.isBlank(path) && path.indexOf("http") == 0) {
            return new RedirectResolution(path);
        }

        if (!Str.isEmpty(path) && !Str.SLASH.equals(path.trim())) {
            UrlRewrite urlRewrite = app.repository(UrlRewrites.class).forTargetURI(path);

            if (urlRewrite != null) {
                String requestPath = ContextObjects.findCurrentLanguage(urlRewrite.getRequestURI());

                if (!Str.isEmpty(requestPath))
                    path = requestPath;
            }

            // Do not redirect back to HTTP if we are in a secure context.
            if (app.isSecureRequest()) {
                // Make sure that the HttpsFilter knows that we have come from a
                // secure post request.
                if (app.isPostRequest())
                    Requests.rememberSecurePostGetRedirect(path);

                path = new StringBuilder(getSecureBasePath()).append(path).toString();

            }
        }

        return new RedirectResolution(path);
    }

    protected Result redirect2(String path) {
        if (!StringUtils.isBlank(path) && path.indexOf("http") == 0) {
            return Results.redirect(path);
        }

        if (!Str.isEmpty(path) && !Str.SLASH.equals(path.trim())) {
            UrlRewrite urlRewrite = app.repository(UrlRewrites.class).forTargetURI(path);

            if (urlRewrite != null) {
                String requestPath = ContextObjects.findCurrentLanguage(urlRewrite.getRequestURI());

                if (!Str.isEmpty(requestPath))
                    path = requestPath;
            }

            // Do not redirect back to HTTP if we are in a secure context.
            if (app.isSecureRequest()) {
                // Make sure that the HttpsFilter knows that we have come from a
                // secure post request.
                if (app.isPostRequest())
                    Requests.rememberSecurePostGetRedirect(path);

                path = new StringBuilder(getSecureBasePath()).append(path).toString();
            }
        }

        return Results.redirect(path);
    }

    protected Resolution freemarkerTemplateStream(String content) {
        return freemarkerTemplateStream(content, null);
    }

    protected Resolution freemarkerTemplateStream(String content, String cacheFor) {
        StringWriter sw = new StringWriter();

        try {
            if (cacheFor != null) {
                // Set cache header for caching server.
                getResponse().setHeader("X-CB-Cache-Page", cacheFor);
            }

            ClassLoader cl = getClass().getClassLoader();
            Module m = null;

            if (cl instanceof ModuleClassLoader) {
                ModuleClassLoader mcl = (ModuleClassLoader) (cl);
                m = mcl.getModule();
            }

            Configuration conf = FreemarkerHelper.newConfig(app.servletContext(), m);

            getResponse().setLocale(conf.getLocale());
            getResponse().setCharacterEncoding("UTF-8");

            TemplateModel tm = FreemarkerHelper.createModel(ObjectWrapper.DEFAULT_WRAPPER, app.servletContext(),
                getRequest(), getResponse());

            app.registryPut(FreemarkerConstant.FREEMARKER_REQUEST_TEMPLATE_MODEL, tm);

            Template t = new Template("templateName", new StringReader(content), conf);

            Environment env = t.createProcessingEnvironment(tm, sw);
            env.setLocale(conf.getLocale());
            env.process();
        } catch (Throwable th) {
            if (app.isDevPrintErrorMessages()) {
                System.out.println("An error occured while rendering template from string :  " + content);
                th.printStackTrace();
            }

            throw new RuntimeException(th.getMessage(), th);
        }

        Resolution r = new StreamingResolution("text/html", sw.toString());

        return r;
    }

    protected Resolution json(String json) {
        return new StreamingResolution("application/javascript", json);
    }

    protected Resolution json(String key, Object value) {
        return new JsonResponse().append(key, value).toResolution();
    }

    protected Resolution json(Map<String, Object> params) {
        return new JsonResponse().appendAll(params).toResolution();
    }

    protected Resolution jsonSuccess(String message) {
        return new JsonResponse(message, MessageType.SUCCESS).toResolution();
    }

    protected Resolution jsonSuccess(String message, Map<String, Object> params) {
        return new JsonResponse(message, MessageType.SUCCESS).appendAll(params).toResolution();
    }

    protected Resolution jsonError(String message) {
        return new JsonResponse(message, MessageType.ERROR).toResolution();
    }

    protected Resolution jsonError(String message, Map<String, Object> params) {
        return new JsonResponse(message, MessageType.ERROR).appendAll(params).toResolution();
    }

    public String getStripesFormFields() {
        return new StringBuilder().append("<input type=\"hidden\" name=\"").append(StripesConstants.URL_KEY_SOURCE_PAGE)
            .append("\" value=\"").append(CryptoUtil.encrypt(getContext().getRequest().getServletPath()))
            .append("\" />").toString();
    }

    public String getStripesFormQueryString() {
        return new StringBuilder(StripesConstants.URL_KEY_SOURCE_PAGE).append(Char.EQUALS)
            .append(CryptoUtil.encrypt(getContext().getRequest().getServletPath())).toString();
    }

    protected RequestContext getRequestContext() {
        return app.context().getRequestContext();
    }

    protected Store getStore() {
        return app.context().getStore();
    }

    public String getSecureBasePath() {
        return app.getSecureBasePath();
    }

    protected void addValidationError(String message) {
        addValidationError(message, (Object) null);
    }

    protected void addValidationError(String message, Object... parameter) {
        ValidationErrors validationErrors = getContext().getValidationErrors();
        validationErrors.addGlobalError(new SimpleError(message, parameter));
    }

    public <T> T getCartFromSession() {
        return app.getCartFromSession();
    }

    protected <T> void setLoggedInCustomer(T customer) {
        app.setLoggedInCustomer(customer);
    }

    public <T> T getLoggedInCustomer() {
        return app.getLoggedInCustomer();
    }

    public boolean isCustomerLoggedIn() {
        return app.isCustomerLoggedIn();
    }

    protected void cookieSet(String key, Object value) {
        app.cookieSet(key, value);
    }

    protected void cookieSet(String key, Object value, Integer maxAge) {
        app.cookieSet(key, value, maxAge);
    }

    protected void cookieUnset(String key) {
        app.cookieUnset(key);
    }

    public boolean isDevToolbar() {
        return app.isDevToolbar();
    }

    public String cookieGet(String key) {
        return app.cookieGet(key);
    }

    protected void sessionInit() {
        app.sessionInit();
    }

    protected void sessionInvalidate() {
        app.sessionInvalidate();
    }

    /**
     * Renew session after login etc. to prevent session hijacking.
     */
    protected void renewSession() {
        HttpSession session = app.servletRequest().getSession(false);

        if (session != null && session.getAttributeNames() != null) {
            Map<String, Object> tmp = new HashMap<>();

            Enumeration<String> names = session.getAttributeNames();

            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                tmp.put(name, session.getAttribute(name));
            }

            sessionInvalidate();

            if (tmp.size() > 0) {
                Set<String> tmpNames = tmp.keySet();

                for (String name : tmpNames) {
                    sessionSet(name, tmp.get(name));
                }
            }
        }
    }

    protected void sessionSet(String key, Object value) {
        app.sessionSet(key, value);
    }

    protected void sessionRemove(String key) {
        app.sessionRemove(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T sessionGet(String key) {
        return (T) app.sessionGet(key);
    }

    @Override
    public ActionBeanContext getContext() {
        return this.context;
    }

    @Override
    public void setContext(ActionBeanContext ctx) {
        this.context = ctx;
    }

    protected Merchant getMerchant() {
        return app.context().getMerchant();
    }

    protected Locale currentLocale() {
        return app.context().getRequestContext().getLocale();
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public int[] getDobDays() {
        int[] days = new int[31];
        for (int i = 0; i < days.length; i++) {
            days[i] = i + 1;
        }

        return days;
    }

    public int[] getDobMonths() {
        int[] months = new int[12];
        for (int i = 0; i < months.length; i++) {
            months[i] = i + 1;
        }

        return months;
    }

    public int[] getDobYears() {
        DateTime dt = new DateTime();
        int currentYear = dt.getYear();

        int yearsfrom = currentYear - 110;
        int yearsTo = currentYear - 16;

        int[] years = new int[yearsTo - yearsfrom];
        int y = 0;
        for (int i = yearsTo; i > yearsfrom; i--) {
            years[y++] = i;
        }

        return years;
    }

    public int[] getCcExpiryMonths() {
        int[] months = new int[12];
        for (int i = 0; i < months.length; i++) {
            months[i] = i + 1;
        }

        return months;
    }

    public int[] getCcExpiryYears() {
        DateTime dt = new DateTime();
        int currentYear = dt.getYear();

        int yearsfrom = currentYear;
        int yearsTo = currentYear + 10;

        int[] years = new int[yearsTo - yearsfrom];
        int y = 0;
        for (int i = yearsfrom; i < yearsTo; i++) {
            years[y++] = i;
        }

        return years;
    }

    public Map<String, String> getCountries() {
        Map<String, String> countries = new LinkedHashMap<>();

        countries.put("AF", "Afghanistan");
        countries.put("AX", "Åland Islands");
        countries.put("AL", "Albania");
        countries.put("DZ", "Algeria");
        countries.put("AS", "American Samoa");
        countries.put("AD", "Andorra");
        countries.put("AO", "Angola");
        countries.put("AI", "Anguilla");
        countries.put("AQ", "Antarctica");
        countries.put("AG", "Antigua and Barbuda");
        countries.put("AR", "Argentina");
        countries.put("AM", "Armenia");
        countries.put("AW", "Aruba");
        countries.put("AU", "Australia");
        countries.put("AT", "Austria");
        countries.put("AZ", "Azerbaijan");
        countries.put("BS", "Bahamas");
        countries.put("BH", "Bahrain");
        countries.put("BD", "Bangladesh");
        countries.put("BB", "Barbados");
        countries.put("BY", "Belarus");
        countries.put("BE", "Belgium");
        countries.put("BZ", "Belize");
        countries.put("BJ", "Benin");
        countries.put("BM", "Bermuda");
        countries.put("BT", "Bhutan");
        countries.put("BO", "Bolivia");
        countries.put("BA", "Bosnia and Herzegovina");
        countries.put("BW", "Botswana");
        countries.put("BV", "Bouvet Island");
        countries.put("BR", "Brazil");
        countries.put("IO", "British Indian Ocean Territory");
        countries.put("VG", "British Virgin Islands");
        countries.put("BN", "Brunei");
        countries.put("BG", "Bulgaria");
        countries.put("BF", "Burkina Faso");
        countries.put("BI", "Burundi");
        countries.put("KH", "Cambodia");
        countries.put("CM", "Cameroon");
        countries.put("CA", "Canada");
        countries.put("CV", "Cape Verde");
        countries.put("KY", "Cayman Islands");
        countries.put("CF", "Central African Republic");
        countries.put("TD", "Chad");
        countries.put("CL", "Chile");
        countries.put("CN", "China");
        countries.put("CX", "Christmas Island");
        countries.put("CC", "Cocos [Keeling] Islands");
        countries.put("CO", "Colombia");
        countries.put("KM", "Comoros");
        countries.put("CG", "Congo - Brazzaville");
        countries.put("CD", "Congo - Kinshasa");
        countries.put("CK", "Cook Islands");
        countries.put("CR", "Costa Rica");
        countries.put("CI", "Côte d’Ivoire");
        countries.put("HR", "Croatia");
        countries.put("CU", "Cuba");
        countries.put("CY", "Cyprus");
        countries.put("CZ", "Czech Republic");
        countries.put("DK", "Denmark");
        countries.put("DJ", "Djibouti");
        countries.put("DM", "Dominica");
        countries.put("DO", "Dominican Republic");
        countries.put("EC", "Ecuador");
        countries.put("EG", "Egypt");
        countries.put("SV", "El Salvador");
        countries.put("GQ", "Equatorial Guinea");
        countries.put("ER", "Eritrea");
        countries.put("EE", "Estonia");
        countries.put("ET", "Ethiopia");
        countries.put("FK", "Falkland Islands");
        countries.put("FO", "Faroe Islands");
        countries.put("FJ", "Fiji");
        countries.put("FI", "Finland");
        countries.put("FR", "France");
        countries.put("GF", "French Guiana");
        countries.put("PF", "French Polynesia");
        countries.put("TF", "French Southern Territories");
        countries.put("GA", "Gabon");
        countries.put("GM", "Gambia");
        countries.put("GE", "Georgia");
        countries.put("DE", "Germany");
        countries.put("GH", "Ghana");
        countries.put("GI", "Gibraltar");
        countries.put("GR", "Greece");
        countries.put("GL", "Greenland");
        countries.put("GD", "Grenada");
        countries.put("GP", "Guadeloupe");
        countries.put("GU", "Guam");
        countries.put("GT", "Guatemala");
        countries.put("GN", "Guinea");
        countries.put("GW", "Guinea-Bissau");
        countries.put("GY", "Guyana");
        countries.put("HT", "Haiti");
        countries.put("HM", "Heard Island and McDonald Islands");
        countries.put("HN", "Honduras");
        countries.put("HK", "Hong Kong SAR China");
        countries.put("HU", "Hungary");
        countries.put("IS", "Iceland");
        countries.put("IN", "India");
        countries.put("ID", "Indonesia");
        countries.put("IR", "Iran");
        countries.put("IQ", "Iraq");
        countries.put("IE", "Ireland");
        countries.put("IL", "Israel");
        countries.put("IT", "Italy");
        countries.put("JM", "Jamaica");
        countries.put("JP", "Japan");
        countries.put("JO", "Jordan");
        countries.put("KZ", "Kazakhstan");
        countries.put("KE", "Kenya");
        countries.put("KI", "Kiribati");
        countries.put("KW", "Kuwait");
        countries.put("KG", "Kyrgyzstan");
        countries.put("LA", "Laos");
        countries.put("LV", "Latvia");
        countries.put("LB", "Lebanon");
        countries.put("LS", "Lesotho");
        countries.put("LR", "Liberia");
        countries.put("LY", "Libya");
        countries.put("LI", "Liechtenstein");
        countries.put("LT", "Lithuania");
        countries.put("LU", "Luxembourg");
        countries.put("MO", "Macau SAR China");
        countries.put("MK", "Macedonia");
        countries.put("MG", "Madagascar");
        countries.put("MW", "Malawi");
        countries.put("MY", "Malaysia");
        countries.put("MV", "Maldives");
        countries.put("ML", "Mali");
        countries.put("MT", "Malta");
        countries.put("MH", "Marshall Islands");
        countries.put("MQ", "Martinique");
        countries.put("MR", "Mauritania");
        countries.put("MU", "Mauritius");
        countries.put("YT", "Mayotte");
        countries.put("MX", "Mexico");
        countries.put("FM", "Micronesia");
        countries.put("MD", "Moldova");
        countries.put("MC", "Monaco");
        countries.put("MN", "Mongolia");
        countries.put("MS", "Montserrat");
        countries.put("MA", "Morocco");
        countries.put("MZ", "Mozambique");
        countries.put("MM", "Myanmar [Burma]");
        countries.put("NA", "Namibia");
        countries.put("NR", "Nauru");
        countries.put("NP", "Nepal");
        countries.put("NL", "Netherlands");
        countries.put("AN", "Netherlands Antilles");
        countries.put("NC", "New Caledonia");
        countries.put("NZ", "New Zealand");
        countries.put("NI", "Nicaragua");
        countries.put("NE", "Niger");
        countries.put("NG", "Nigeria");
        countries.put("NU", "Niue");
        countries.put("NF", "Norfolk Island");
        countries.put("MP", "Northern Mariana Islands");
        countries.put("KP", "North Korea");
        countries.put("NO", "Norway");
        countries.put("OM", "Oman");
        countries.put("PK", "Pakistan");
        countries.put("PW", "Palau");
        countries.put("PS", "Palestinian Territories");
        countries.put("PA", "Panama");
        countries.put("PG", "Papua New Guinea");
        countries.put("PY", "Paraguay");
        countries.put("PE", "Peru");
        countries.put("PH", "Philippines");
        countries.put("PN", "Pitcairn Islands");
        countries.put("PL", "Poland");
        countries.put("PT", "Portugal");
        countries.put("PR", "Puerto Rico");
        countries.put("QA", "Qatar");
        countries.put("RE", "Réunion");
        countries.put("RO", "Romania");
        countries.put("RU", "Russia");
        countries.put("RW", "Rwanda");
        countries.put("SH", "Saint Helena");
        countries.put("KN", "Saint Kitts and Nevis");
        countries.put("LC", "Saint Lucia");
        countries.put("PM", "Saint Pierre and Miquelon");
        countries.put("VC", "Saint Vincent and the Grenadines");
        countries.put("WS", "Samoa");
        countries.put("SM", "San Marino");
        countries.put("ST", "São Tomé and Príncipe");
        countries.put("SA", "Saudi Arabia");
        countries.put("SN", "Senegal");
        countries.put("SC", "Seychelles");
        countries.put("SL", "Sierra Leone");
        countries.put("SG", "Singapore");
        countries.put("SK", "Slovakia");
        countries.put("SI", "Slovenia");
        countries.put("SB", "Solomon Islands");
        countries.put("SO", "Somalia");
        countries.put("ZA", "South Africa");
        countries.put("GS", "South Georgia and the South Sandwich Islands");
        countries.put("KR", "South Korea");
        countries.put("ES", "Spain");
        countries.put("LK", "Sri Lanka");
        countries.put("SD", "Sudan");
        countries.put("SR", "Suriname");
        countries.put("SJ", "Svalbard and Jan Mayen");
        countries.put("SZ", "Swaziland");
        countries.put("SE", "Sweden");
        countries.put("CH", "Switzerland");
        countries.put("SY", "Syria");
        countries.put("TW", "Taiwan");
        countries.put("TJ", "Tajikistan");
        countries.put("TZ", "Tanzania");
        countries.put("TH", "Thailand");
        countries.put("TG", "Togo");
        countries.put("TK", "Tokelau");
        countries.put("TO", "Tonga");
        countries.put("TT", "Trinidad and Tobago");
        countries.put("TN", "Tunisia");
        countries.put("TR", "Turkey");
        countries.put("TM", "Turkmenistan");
        countries.put("TC", "Turks and Caicos Islands");
        countries.put("TV", "Tuvalu");
        countries.put("UG", "Uganda");
        countries.put("UA", "Ukraine");
        countries.put("AE", "United Arab Emirates");
        countries.put("GB", "United Kingdom");
        countries.put("US", "United States");
        countries.put("UY", "Uruguay");
        countries.put("UM", "U.S. Minor Outlying Islands");
        countries.put("VI", "U.S. Virgin Islands");
        countries.put("UZ", "Uzbekistan");
        countries.put("VU", "Vanuatu");
        countries.put("VA", "Vatican City");
        countries.put("VE", "Venezuela");
        countries.put("VN", "Vietnam");
        countries.put("WF", "Wallis and Futuna");
        countries.put("EH", "Western Sahara");
        countries.put("YE", "Yemen");
        countries.put("ZM", "Zambia");
        countries.put("ZW", "Zimbabwe");

        return countries;
    }

    public String getCulture() {
        RequestContext reqCtx = getRequestContext();
        Locale locale = new Locale(reqCtx.getLanguage(), reqCtx.getCountry());
        return locale.toString();
    }

    public String getCurrency() {
        RequestContext reqCtx = getRequestContext();
        Locale locale = new Locale(reqCtx.getLanguage(), reqCtx.getCountry());
        return NumberFormat.getCurrencyInstance(locale).getCurrency().getSymbol(locale);
    }

    public void setRequestCounter(String name) {
        if (sessionGet(name + "_time") == null) {
            requestCounter = 1;
            requestTime = System.currentTimeMillis();
            sessionSet(name + "_time", requestTime);
            sessionSet(name + "_counter", requestCounter);
        } else {
            requestCounter = sessionGet(name + "_counter");
            requestTime = sessionGet(name + "_time");
            if ((System.currentTimeMillis() - requestTime > (60L * 60L * 1000L))) {
                requestTime = System.currentTimeMillis();
                sessionSet(name + "_time", requestTime);
                requestCounter = 1;
            }
        }
    }

    public boolean isRequestCounterValid(String name) {
        this.setRequestCounter(name);

        if (System.currentTimeMillis() - requestTime < (60L * 60L * 1000L) && requestCounter <= 3) {
            sessionSet(name + "_counter", ++requestCounter);
            return true;
        } else {
            return false;
        }
    }

    public boolean isEditMode() {
        return app.editHeaderExists();
    }

    public String getModuleName() {
        ClassLoader cl = controllerClassLoader();

        if (cl instanceof ModuleClassLoader) {
            ModuleClassLoader mcl = (ModuleClassLoader) cl;
            Module m = mcl.getModule();

            return m.getName();
        }

        return Str.EMPTY;
    }

    public String getModuleCode() {
        ClassLoader cl = controllerClassLoader();

        if (cl instanceof ModuleClassLoader) {
            ModuleClassLoader mcl = (ModuleClassLoader) cl;
            Module m = mcl.getModule();

            return m.getCode();
        }

        return Str.EMPTY;
    }

    public String getControllerCode() {
        Class<?> controllerClass = ensureNoneGuiceClass(getClass());

        String controller = controllerClass.getSimpleName().replaceFirst("Controller$", Str.EMPTY)
            .replaceFirst("Action$", Str.EMPTY);

        if (controller.startsWith("My") && controllerClass.getName().startsWith("custom."))
            controller = controller.replaceFirst("^My", Str.EMPTY);

        controller = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(controller), Char.UNDERSCORE);

        return Strings.slugify(controller).replace(Char.MINUS, Char.UNDERSCORE).toLowerCase();
    }

    protected ClassLoader controllerClassLoader() {
        Class<?> clazz = ensureNoneGuiceClass(getClass());
        ClassLoader cl = clazz.getClassLoader();

        if (cl instanceof ModuleClassLoader) {
            return cl;
        } else {
            return ensureNoneGuiceClass(clazz.getSuperclass()).getClassLoader();
        }
    }

    protected Class<?> ensureNoneGuiceClass(Class<?> controllerClass) {
        if (controllerClass.getSimpleName().contains("$$EnhancerByGuice$$")) {
            return controllerClass.getSuperclass();
        } else {
            return controllerClass;
        }
    }

    public String getEventName() {
        return getContext().getEventName();
    }

    public String getV() {
        return app.getVersion();
    }

    public boolean antiSpamCheck() {
        if (true)
            return honeyPotCheck();
        else
            return captureCheck();
    }

    protected boolean honeyPotCheck() {
        return true;
    }

    protected boolean captureCheck() {

        return true;
    }

    public Resolution renderContent(String content) {
        Configuration conf = FreemarkerHelper.newConfig(app.servletContext(), null);

        try {
            Template temp = new Template("templateName", new StringReader(content), conf);
            // Template temp = conf.getTemplate(new
            // StringBuilder(Templates.getWidgetsPath()).append("/").append(path).append(templateSuffix).toString());

            StringWriter sw = new StringWriter();

            temp.process(null, sw);

            return new StreamingResolution("text/html", sw.toString());
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }

    }
}
