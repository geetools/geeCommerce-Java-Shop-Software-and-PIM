package com.geecommerce.core.template.freemarker.directive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.geecommerce.core.App;
import com.geecommerce.core.util.Requests;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class ImportDirective implements TemplateDirectiveModel {
    private static final String USER_AGENT = "cb-import";

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        SimpleScalar uriScalar = (SimpleScalar) params.get("uri");
        SimpleScalar paramsScalar = (SimpleScalar) params.get("params");

        TemplateBooleanModel useDispatcher = (TemplateBooleanModel) params.get("useDispatcher");

        if (uriScalar == null)
            throw new IllegalArgumentException("The uri parameter cannot be null when using the import-directive");

        String uri = uriScalar.getAsString().trim();

        if (!uri.startsWith("/")) {
            throw new IllegalArgumentException("The URI must start with a slash (/) and must not be an absolute path (http://). Only relative paths allowed");
        }

        boolean isUseRequestDispatcher = true;
        if (useDispatcher != null) {
            isUseRequestDispatcher = useDispatcher.getAsBoolean();
        }

        App app = App.get();

        // ---------------------------------------------------------
        // Compose query-string with import URL parameters
        // ---------------------------------------------------------
        String queryString = null;
        Map<String, String[]> parameterMap = new HashMap<>();

        if (body != null) {
            StringWriter sw = new StringWriter();

            try {
                body.render(sw);
                String bodyStr = sw.toString().trim();

                if (bodyStr != null && !"".equals(bodyStr)) {
                    String[] sp = bodyStr.split("\n");
                    StringBuilder buildParams = new StringBuilder();

                    int x = 0;
                    for (String keyValuePair : sp) {
                        if (uri.contains("?") || x > 0) {
                            buildParams.append('&');
                        } else {
                            buildParams.append('?');
                        }

                        buildParams.append(keyValuePair.trim());

                        String[] kwp = keyValuePair.split("=");
                        parameterMap.put(kwp[0], new String[] { kwp[1] });

                        x++;
                    }

                    if (buildParams.length() > 0)
                        queryString = buildParams.toString();
                }
            } finally {
                IOUtils.closeQuietly(sw);
            }
        }

        // ---------------------------------------------------------
        // Build complete URL and write to output
        // ---------------------------------------------------------

        HttpServletRequest request = app.getServletRequest();
        HttpServletResponse response = app.getServletResponse();

        try {
            // ------------------------------------------------
            // Get content using RequestDispatcher (default)
            // ------------------------------------------------
            if (isUseRequestDispatcher) {
                String relativeURL = new StringBuilder(uri).append(queryString == null ? "" : queryString).toString();

                ServletContext sc = app.getServletContext();
                ImportRequestWrapper requestWrapper = new ImportRequestWrapper(request, uri);
                requestWrapper.getParameterMap().putAll(parameterMap);
                sc.getRequestDispatcher(relativeURL).include(requestWrapper, new ImportResponseWrapper(response, env.getOut()));
            }
            // ------------------------------------------------
            // Get content using URLConnection
            // ------------------------------------------------
            else {
                String absoluteURL = Requests.buildAbsoluteURL(request, uri, queryString, true);

                String content = getContent(absoluteURL);

                env.getOut().write(content);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void addHeaders(HttpGet httpget) {
        HttpServletRequest request = App.get().getServletRequest();

        httpget.setHeader("Host", Requests.getHost(request));
        httpget.setHeader("User-Agent", USER_AGENT);
        httpget.setHeader("Referer", request.getRequestURI());
        httpget.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        httpget.setHeader("Cookie", request.getHeader("cookie"));
    }

    private String getContent(String requestURL) {
        StringBuilder content = new StringBuilder();

        CloseableHttpResponse response = null;

        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();

            HttpGet httpget = new HttpGet(requestURL);
            addHeaders(httpget);

            response = httpclient.execute(httpget);

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream is = null;
                InputStreamReader ir = null;
                BufferedReader br = null;

                try {
                    is = entity.getContent();
                    ir = new InputStreamReader(is, Charset.forName("UTF-8"));
                    br = new BufferedReader(ir);

                    String line = null;

                    while ((line = br.readLine()) != null) {
                        content.append(line);
                    }
                } finally {
                    IOUtils.closeQuietly(br);
                    IOUtils.closeQuietly(ir);
                    IOUtils.closeQuietly(is);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            IOUtils.closeQuietly(response);
        }

        return content.toString();
    }

    public class ImportRequestWrapper extends HttpServletRequestWrapper {
        private Map<String, String[]> parameterMap = new HashMap<>();
        private String newURI = null;
        private String originalURI = null;

        public ImportRequestWrapper(HttpServletRequest request, String newURI) {
            super(request);

            this.newURI = newURI;
            this.originalURI = request.getRequestURI();
        }

        @Override
        public String getRequestURI() {
            String requestURI = super.getRequestURI();

            if (requestURI != null && requestURI.equals(originalURI)) {
                return newURI;
            } else {
                return requestURI;
            }
        }

        @Override
        public String getServletPath() {
            return getRequestURI();
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return parameterMap;
        }

        @Override
        public String getParameter(String name) {
            Object val = parameterMap.get(name);

            if (val == null)
                return null;

            else if (val instanceof String) {
                return (String) val;
            } else if (val instanceof String[]) {
                return ((String[]) val)[0];
            }

            return val.toString();
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(parameterMap.keySet());
        }

        @Override
        public String[] getParameterValues(String name) {
            return parameterMap.get(name);
        }
    }

    public class ImportResponseWrapper extends HttpServletResponseWrapper {
        private Writer writer = new StringWriter();

        public ImportResponseWrapper(HttpServletResponse response, Writer writer) {
            super(response);
            this.writer = writer;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException("OutputStream not supported. Use Writer intead.");
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return new PrintWriter(writer);
        }
    }
}
