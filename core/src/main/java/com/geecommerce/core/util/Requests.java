package com.geecommerce.core.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.web.DefaultServletRequestWrapper;
import com.geecommerce.core.web.DefaultServletResponseWrapper;

public final class Requests {
    private static final String API_URI_PREFIX = "/api/";

    private static final String CMS_URI_PREFIX = "/cms/";

    private static final String DEFAULT_HTTPS_SCHEME = "https";

    private static final int DEFAULT_HTTPS_PORT = 443;

    private static final String HTTPS_FORWARD_HEADERS_CONFIG_KEY = "general/web/https/forward_headers";

    private static final String HTTPS_SCHEME_CONFIG_KEY = "general/web/https/scheme";

    private static final String HTTPS_PORT_CONFIG_KEY = "general/web/https/port";

    private static final String REFERRER_HEADER_NAME = "Referer";

    private static final String REQUEST_METHOD_POST = "POST";

    private static final String REQUEST_METHOD_GET = "GET";

    private static final String MULTIPART_HEADER = "multipart/form-data";

    private static final String DEFAULT_ERROR_BASE_URI = "/error/";

    private static final Map<String, String> defaultHttpsForwardHeaders = new HashMap<String, String>();

    static {
        defaultHttpsForwardHeaders.put("x-forwarded-protocol", "https");
        defaultHttpsForwardHeaders.put("x-forwarded-proto", "https");
        defaultHttpsForwardHeaders.put("x-forwarded-scheme", "https");
        defaultHttpsForwardHeaders.put("https", "on");
    }

    private static final Set<String> securePostGetRedirects = new HashSet<>();

    private static final String AJAX_REQUEST_HEADER_NAME = "X-Requested-With";

    private static final String AJAX_REQUEST_HEADER_VALUE = "XMLHttpRequest";

    private static final String REGEX_MEDIA_EXTENSIONS_CONFIG_KEY = "general/web/regex_media_extensions";

    private static final String REGEX_MEDIA_PATHS_CONFIG_KEY = "general/web/regex_media_paths";

    private static final String REGEX_PAGE_EXTENSIONS_CONFIG_KEY = "general/web/regex_page_extensions";

    private static final String DEFAULT_REGEX_MEDIA_EXTENSIONS = ".+\\.(gif|jpg|jpeg|png|bmp|ico|rar|css|js|zip|gz|flv|swf|mp3|mp4|m4v|webm|weba|ogm|ogv|ogg|doc|docx|ppt|pptx|xls|xlsx|pdf|txt|csv)$";

    private static final String DEFAULT_REGEX_MEDIA_PATHS = "^\\/(static|skin|js|css|cache|c\\/media)\\/.+";

    private static final String DEFAULT_REGEX_PAGE_EXTENSIONS = ".+\\.(htm|html|ftl|jsp)$";

    public static final String getHost(HttpServletRequest request) {
        if (request == null)
            return null;

        String requestUrl = request.getRequestURL().toString();

        URL url;

        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return url.getHost();
    }

    public static final URL getURL(HttpServletRequest request) {
        if (request == null)
            return null;

        String requestUrl = request.getRequestURL().toString();

        URL url;

        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return url;
    }

    public static final String buildAbsoluteURL(HttpServletRequest request, String uri) {
        return buildAbsoluteURL(request, uri, null, false);
    }

    public static final String buildAbsoluteURL(HttpServletRequest request, String uri, boolean useLocalIP) {
        return buildAbsoluteURL(request, uri, null, useLocalIP);
    }

    public static final String buildAbsoluteURL(HttpServletRequest request, String uri, String queryString) {
        return buildAbsoluteURL(request, uri, queryString, false);
    }

    public static final String buildAbsoluteURL(HttpServletRequest request, String uri, String queryString,
        boolean useLocalIP) {
        if (request == null)
            return null;

        URL url = getURL(request);

        String host = url.getHost();
        int port = url.getPort();
        String localIP = request.getLocalAddr();
        String contextPath = request.getContextPath();

        StringBuilder sb = new StringBuilder(url.getProtocol()).append(Str.PROTOCOL_SUFFIX)
            .append(useLocalIP ? localIP : host).append(port == 80 || port == -1 ? Str.EMPTY : Str.COLON)
            .append(port == 80 || port == -1 ? Str.EMPTY : url.getPort()).append(contextPath).append(uri)
            .append(queryString == null ? Str.EMPTY : queryString);

        return sb.toString();
    }

    public static final String getURLWithoutPortAndContextPath(HttpServletRequest request) {
        URL url = getURL(request);

        StringBuilder newURL = new StringBuilder(url.getProtocol()).append(Str.PROTOCOL_SUFFIX).append(url.getHost())
            .append(request.getServletPath())
            .append(request.getPathInfo() == null ? Str.EMPTY : request.getPathInfo());

        return newURL.toString();
    }

    public static final String getURIWithoutContextPath(HttpServletRequest request) {
        StringBuilder newURL = new StringBuilder().append(request.getServletPath())
            .append(request.getPathInfo() == null ? Str.EMPTY : request.getPathInfo());

        return newURL.toString();
    }

    public static final boolean isRelativeURL(final String URL) {
        try {
            URI uri = new URI(URL);

            if (uri.isAbsolute())
                return false;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public static final boolean uriPatternMatches(String path, String uriPattern) {
        if ("/*".equals(uriPattern)) {
            return true;
        } else if (uriPattern.startsWith(Str.ASTERIX)) {
            String suffix = uriPattern.substring(1);
            return path.endsWith(suffix);
        } else if (uriPattern.endsWith("/*")) {
            String prefix = uriPattern.substring(0, uriPattern.length() - 1);
            return path.startsWith(prefix);
        } else if (path.startsWith(uriPattern)) {
            return true;
        }

        return false;
    }

    public static final String extractLastURIPart(String uri) {
        if (uri == null)
            return null;

        uri = normalizeURI(uri);

        int lastSlashPos = uri.lastIndexOf(Char.SLASH, uri.length() - 2);

        return uri.substring(lastSlashPos < 0 ? 0 : lastSlashPos, uri.length());
    }

    public static final String stripLastURIPart(String uri) {
        if (uri == null)
            return null;

        uri = normalizeURI(uri);

        int lastSlashPos = uri.lastIndexOf(Char.SLASH, uri.length() - 2) + 1;

        return uri.substring(0, lastSlashPos > 0 ? lastSlashPos : uri.length());
    }

    /**
     * Corrects URI by removing double slashes and trimming it.
     *
     * @param uri
     * @return normalized-uri
     */
    public static final String normalizeURI(String uri) {
        if (uri == null)
            return null;

        uri = uri.trim();

        return uri.replaceAll("\\/+", Str.SLASH);
    }

    public static final boolean isSecureRequest(HttpServletRequest request) {
        App app = App.get();

        // -------------------------------------------------------------
        // We'll do the basic checks first by checking port and scheme.
        // -------------------------------------------------------------
        String httpsScheme = app.registryGet(HTTPS_SCHEME_CONFIG_KEY);
        Integer httpsPort = app.registryGet(HTTPS_PORT_CONFIG_KEY);

        if (httpsScheme == null) {
            httpsScheme = app.cpStr_(HTTPS_SCHEME_CONFIG_KEY, DEFAULT_HTTPS_SCHEME);
            app.registryPut(HTTPS_SCHEME_CONFIG_KEY, httpsScheme);
        }

        if (httpsPort == null) {
            httpsPort = app.cpInt_(HTTPS_PORT_CONFIG_KEY, DEFAULT_HTTPS_PORT);
            app.registryPut(HTTPS_PORT_CONFIG_KEY, httpsPort);
        }

        if (httpsPort.intValue() == request.getServerPort())
            return true;

        if (httpsScheme.equalsIgnoreCase(request.getScheme()))
            return true;

        // -------------------------------------------------------------
        // If the request was forwarded by a load balancer or proxy, it
        // may have sent a header indicating that this is a HTTPS request.
        // Here we check for some of the common possibilities.
        // -------------------------------------------------------------

        Map<String, String> httpsHeaders = app.registryGet(HTTPS_FORWARD_HEADERS_CONFIG_KEY);

        if (httpsHeaders == null) {
            httpsHeaders = app.cpStrMap_(HTTPS_FORWARD_HEADERS_CONFIG_KEY, defaultHttpsForwardHeaders);
            app.registryPut(HTTPS_FORWARD_HEADERS_CONFIG_KEY, httpsHeaders);
        }

        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();

            if (httpsHeaders.containsKey(headerName.toLowerCase())) {
                String reqHeaderValue = request.getHeader(headerName);
                String httpsHeaderValue = httpsHeaders.get(headerName.toLowerCase());

                if (reqHeaderValue.equalsIgnoreCase(httpsHeaderValue)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static final boolean isAjaxRequest(HttpServletRequest request) {
        String ajaxRequestHeaderValue = null;

        if (request instanceof DefaultServletRequestWrapper) {
            ajaxRequestHeaderValue = ((DefaultServletRequestWrapper) request)
                .getUncheckedHeader(AJAX_REQUEST_HEADER_NAME);
        } else {
            ajaxRequestHeaderValue = request.getHeader(AJAX_REQUEST_HEADER_NAME);
        }

        if (!Str.isEmpty(ajaxRequestHeaderValue)) {
            return Str.trimEqualsIgnoreCase(AJAX_REQUEST_HEADER_VALUE, ajaxRequestHeaderValue);
        } else {
            return false;
        }
    }

    public static final boolean isMediaRequest(String uri) {
        App app = App.get();

        String regexMediaExtensions = DEFAULT_REGEX_MEDIA_EXTENSIONS;
        String regexMediaPaths = DEFAULT_REGEX_MEDIA_PATHS;

        if (regexMediaExtensions == null) {
            regexMediaExtensions = app.cpStr_(REGEX_MEDIA_EXTENSIONS_CONFIG_KEY, DEFAULT_REGEX_MEDIA_EXTENSIONS);
            app.registryPut(REGEX_MEDIA_EXTENSIONS_CONFIG_KEY, regexMediaExtensions);
        }

        if (regexMediaPaths == null) {
            regexMediaPaths = app.cpStr_(REGEX_MEDIA_PATHS_CONFIG_KEY, DEFAULT_REGEX_MEDIA_PATHS);
            app.registryPut(REGEX_MEDIA_PATHS_CONFIG_KEY, regexMediaPaths);
        }

        return uri.matches(regexMediaExtensions) || uri.matches(regexMediaPaths);
    }

    public static final boolean isAPIRequest(String uri) {
        return uri.startsWith(API_URI_PREFIX);
    }

    public static final boolean isCMSRequest(String uri) {
        return uri.startsWith(CMS_URI_PREFIX);
    }

    public static final boolean hasPageExtension(String uri) {
        String regexPageExtensions = DEFAULT_REGEX_PAGE_EXTENSIONS;

        if (regexPageExtensions == null) {
            App app = App.get();
            regexPageExtensions = app.cpStr_(REGEX_PAGE_EXTENSIONS_CONFIG_KEY, DEFAULT_REGEX_PAGE_EXTENSIONS);
            app.registryPut(REGEX_PAGE_EXTENSIONS_CONFIG_KEY, regexPageExtensions);
        }

        return uri.matches(regexPageExtensions);
    }

    public static final String getReferrer(HttpServletRequest request) {
        DefaultServletRequestWrapper req = (DefaultServletRequestWrapper) request;

        return req.getUncheckedHeader(REFERRER_HEADER_NAME);
    }

    public static final boolean isInternalRequest(HttpServletRequest request) {
        DefaultServletRequestWrapper req = (DefaultServletRequestWrapper) request;
        String referrer = req.getUncheckedHeader(REFERRER_HEADER_NAME);

        if (!Str.isEmpty(referrer)) {
            try {
                URL url = new URL(referrer);

                String referrerHost = url.getHost();
                String requestHost = getHost(request);

                if (requestHost.equals(referrerHost)) {
                    return true;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static final String getReferrerURI(HttpServletRequest request) {
        DefaultServletRequestWrapper req = (DefaultServletRequestWrapper) request;

        String referrerHeaderVal = req.getUncheckedHeader(REFERRER_HEADER_NAME);
        String referrerURI = null;

        if (!Str.isEmpty(referrerHeaderVal)) {
            String requestHost = getHost(request);

            if (requestHost != null) {
                URL referrerURL = null;

                try {
                    referrerURL = new URL(referrerHeaderVal);
                } catch (MalformedURLException e) {
                }

                if (referrerURL != null) {
                    String referrerHost = referrerURL.getHost();

                    if (referrerHost.equals(requestHost))
                        referrerURI = referrerURL.getPath();
                }
            }
        }

        return referrerURI;
    }

    public static final void rememberSecurePostGetRedirect(String requestURI) {
        if (!requestURI.endsWith(Str.SLASH))
            requestURI += Str.SLASH;

        if (!securePostGetRedirects.contains(requestURI)) {
            securePostGetRedirects.add(requestURI);
        }
    }

    public static final boolean isSecurePostGetRedirect(String requestURI) {
        return securePostGetRedirects.contains(requestURI);
    }

    public static final boolean isGetRequest(HttpServletRequest request) {
        return REQUEST_METHOD_GET.equals(request.getMethod());
    }

    public static final boolean isPostRequest(HttpServletRequest request) {
        return REQUEST_METHOD_POST.equals(request.getMethod());
    }

    public static final boolean isMultipartRequest(HttpServletRequest request) {
        return request.getContentType() != null
            && request.getContentType().toLowerCase().indexOf(MULTIPART_HEADER) > -1;
    }

    public static boolean isErrorPage(HttpServletRequest request, HttpServletResponse response) {
        boolean hasErrorStatus = ((DefaultServletResponseWrapper) response).hasErrorStatus();
        String requestURI = request.getRequestURI();

        return hasErrorStatus || requestURI.startsWith(DEFAULT_ERROR_BASE_URI);
    }
}
