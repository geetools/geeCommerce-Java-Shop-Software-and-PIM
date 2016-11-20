package com.geecommerce.core.web.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.type.ContextObjects;
import com.geecommerce.core.util.Requests;

public class HttpsFilter implements Filter {
    @SuppressWarnings("unused")
    private ServletContext servletContext;

    @SuppressWarnings("unused")
    private FilterConfig filterConfig;

    private static final String HTTPS_ACTIVE_CONFIG_KEY = "general/web/https/active";

    private static final String HTTPS_FORCE_SSL_FOR_URIS_CONFIG_KEY = "general/web/https/force_ssl_for_uris";

    private static final String HTTPS_FORCE_HTTP_FOR_NON_SECURE_URIS_CONFIG_KEY = "general/web/force_http_for_nonsecure_uris";

    private static final String HTTP_SCHEME_CONFIG_KEY = "general/web/http/scheme";

    private static final String HTTPS_SCHEME_CONFIG_KEY = "general/web/https/scheme";

    private static final String DEFAULT_HTTP_SCHEME = "http";

    private static final String DEFAULT_HTTPS_SCHEME = "https";

    private static final String RESPONSE_HEADER_LOCATION = "location";

    private static final String INCREMENT_CONFIG_KEY = "cb.increment";

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        App app = App.get();

        // -------------------------------------------------------------------------------------------
        // Make sure that we do not end up in an endless loop by mistake. This
        // can happen when
        // Stripes attempts to forward to the same URI after a validation error.
        // -------------------------------------------------------------------------------------------

        int incr = app.registryGet(INCREMENT_CONFIG_KEY, 0);

        if (incr > 100) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Possible stackoverflow detected while processing URI '" + path + "'.");
            return;
        } else {
            app.registryPut(INCREMENT_CONFIG_KEY, ++incr);
        }

        // -------------------------------------------------------------------------------------------
        // If response has already been commited or a redirect has already been
        // issued,
        // just continue the chain.
        // -------------------------------------------------------------------------------------------

        if (httpResponse.containsHeader(RESPONSE_HEADER_LOCATION) || httpResponse.isCommitted()) {
            filterChain.doFilter(request, response);
            return;
        }

        // If we are dealing with n internal template request, just continue
        // chain.
        if (app.isTemplateRequest()) {
            filterChain.doFilter(request, response);
            return;
        }

        // -------------------------------------------------------------------------------------------
        // We do not want to redirect if a form has already been processed and
        // validation-errors have
        // been detected as we would lose them (and we would expect the context
        // to be correct already).
        // -------------------------------------------------------------------------------------------

        boolean hasValidationErrors = hasValidationErrors();
        String sourcePage = getSourcePage();

        if (hasValidationErrors) {
            if (path.endsWith(Str.SLASH))
                path = path.substring(0, path.length() - 1);

            // if (app.isMultipartRequest() && !path.endsWith(".ftl"))
            // {
            // try
            // {
            //
            // if(1 == 1)
            // {
            // httpResponse.sendRedirect(path);
            // return;
            // }
            //
            //
            // // FlashScope fs = FlashScope.getCurrent(httpRequest, true);
            // // fs.put("claimAfterSave", claimAfterSave);
            //
            //
            // boolean isCommited = httpResponse.isCommitted();
            // httpResponse.reset();
            //
            // System.out.println("IS COMMITED::::::::: " + isCommited);
            //
            // // RequestDispatcher dispatcher =
            // app.getServletContext().getRequestDispatcher(sourcePage);
            // // dispatcher.forward(request, response);
            //
            //
            // // httpResponse.getOutputStream().write("TEST!!!".getBytes());
            // // httpResponse.getOutputStream().flush();
            //
            // StripesRequestWrapper srw =
            // StripesRequestWrapper.findStripesWrapper(request);
            //
            // request.getRequestDispatcher(sourcePage).forward(request,
            // response);
            // }
            // catch(Throwable t)
            // {
            // t.printStackTrace();
            // }
            //
            // return;
            // }
            // else
            {
                if (path.equals(sourcePage)) {
                    httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot forward to oneself ('" + path
                        + "'). Make sure you have a proper Stripes _sourcePage set or redirect to a different page.");
                    return;
                }

                filterChain.doFilter(request, response);
                return;
            }
        }

        // -------------------------------------------------------------------------------------------
        // In most cases we will end up here. Now we need to find out if the
        // request is meant
        // to be secure and it is not or vice versa. In both cases we make
        // redirect if the scheme
        // is not as expected.
        // -------------------------------------------------------------------------------------------

        boolean isGET = app.isGetRequest();
        boolean isHttpsActive = app.cpBool__(HTTPS_ACTIVE_CONFIG_KEY, false);
        boolean isForceHttpForNonSecureURIs = app.cpBool__(HTTPS_FORCE_HTTP_FOR_NON_SECURE_URIS_CONFIG_KEY, false);
        boolean isSecurePostGetRedirect = Requests.isSecurePostGetRedirect(path);
        boolean isErrorPage = app.isErrorPage();

        // Only do automatic redirects for GET-requests.
        if (isGET && !isErrorPage && isHttpsActive && !app.isAjaxRequest() && !app.isMediaRequest()) {
            List<String> forceSSLForURIs = app.cpStrList_(HTTPS_FORCE_SSL_FOR_URIS_CONFIG_KEY);

            if (forceSSLForURIs != null && !forceSSLForURIs.isEmpty()) {
                boolean isSecureURI = false;

                for (String uriPrefix : forceSSLForURIs) {
                    if (path.startsWith(uriPrefix))
                        isSecureURI = true;
                }

                // If request should be secure and it is not, we initiate a
                // redirect.
                if (isSecureURI && !app.isSecureRequest()) {
                    // System.out.println("Redirect PATH " + path + " to
                    // HTTPS");

                    String httpsScheme = app.cpStr_(HTTPS_SCHEME_CONFIG_KEY, DEFAULT_HTTPS_SCHEME);

                    StringBuilder url = new StringBuilder(httpsScheme).append(Str.PROTOCOL_SUFFIX)
                        .append(Requests.getHost(httpRequest))
                        .append(Str.isEmpty(app.getOriginalURI()) ? path : app.getOriginalURI())
                        .append(httpRequest.getQueryString() == null ? Str.EMPTY : Char.QUESTION_MARK)
                        .append(httpRequest.getQueryString() == null ? Str.EMPTY : httpRequest.getQueryString());

                    httpResponse.sendRedirect(url.toString());
                }
                // If request should not be secure, but it is, and we have
                // redirects back to http enabled, we initiate a
                // redirect.
                else if (isGET && !isErrorPage && isForceHttpForNonSecureURIs && !isSecurePostGetRedirect
                    && !isSecureURI && app.isSecureRequest()) {
                    // System.out.println("Redirect PATH " + path + " to
                    // ***HTTP***");

                    String httpScheme = app.cpStr_(HTTP_SCHEME_CONFIG_KEY, DEFAULT_HTTP_SCHEME);

                    StringBuilder url = new StringBuilder(httpScheme).append(Str.PROTOCOL_SUFFIX)
                        .append(Requests.getHost(httpRequest))
                        .append(Str.isEmpty(app.getOriginalURI()) ? path : app.getOriginalURI())
                        .append(httpRequest.getQueryString() == null ? Str.EMPTY : Char.QUESTION_MARK)
                        .append(httpRequest.getQueryString() == null ? Str.EMPTY : httpRequest.getQueryString());

                    httpResponse.sendRedirect(url.toString());
                }
                // Otherwise we can continue with processing the request.
                else {
                    filterChain.doFilter(request, response);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @SuppressWarnings("unused")
    private String getFriendlyURI(String path) {
        String friendlyURI = null;

        if (!Str.isEmpty(path) && !Str.SLASH.equals(path.trim())) {
            UrlRewrite urlRewrite = App.get().repository(UrlRewrites.class).forTargetURI(path);

            if (urlRewrite != null) {
                friendlyURI = ContextObjects.findCurrentLanguage(urlRewrite.getRequestURI());
            }
        }

        return friendlyURI;
    }

    private boolean hasValidationErrors() {
        // TODO: Change for Geemvc.
        // ActionBean actionBean = app.getActionBean();
        //
        // if (actionBean != null) {
        // ActionBeanContext actionBeanCtx = actionBean.getContext();
        //
        // if (actionBeanCtx != null && actionBeanCtx.getValidationErrors() !=
        // null && !actionBeanCtx.getValidationErrors().isEmpty())
        // return true;
        // }

        return false;
    }

    private String getSourcePage() {
        // TODO: Change for Geemvc.
        // ActionBean actionBean = app.getActionBean();
        //
        // if (actionBean != null) {
        // ActionBeanContext actionBeanCtx = actionBean.getContext();
        //
        // if (actionBeanCtx != null) {
        // String sourcePage = actionBeanCtx.getSourcePage();
        //
        // if (sourcePage != null) {
        // if (sourcePage.endsWith(Str.SLASH))
        // sourcePage = sourcePage.substring(0, sourcePage.length() - 1);
        // }
        //
        // return sourcePage;
        // }
        // }

        return null;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
        this.filterConfig = filterConfig;
    }
}
