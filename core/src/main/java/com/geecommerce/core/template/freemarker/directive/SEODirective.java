package com.geecommerce.core.template.freemarker.directive;

import static com.lyncode.jtwig.functions.util.HtmlUtils.stripTags;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.util.Strings;

import com.geecommerce.core.App;
import com.geecommerce.core.Str;
import com.geecommerce.core.service.PageSupport;
import com.geecommerce.core.type.ContextObjects;
import com.geecommerce.core.util.Requests;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class SEODirective implements TemplateDirectiveModel {
    private static final String META_DESC_MAX_LENGTH_CONF_KEY = "general/seo/meta_desc_max_length";

    private static final Set<String> URI_NOINDEX_LIST = new HashSet<String>();
    static {
        URI_NOINDEX_LIST.add("/customer");
        URI_NOINDEX_LIST.add("/cart");
        URI_NOINDEX_LIST.add("/checkout");
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        TemplateModel pPageModel = (TemplateModel) params.get("pageModel");
        SimpleScalar pTitle = (SimpleScalar) params.get("title");
        SimpleScalar pURI = (SimpleScalar) params.get("uri");
        SimpleScalar pMetaDescription = (SimpleScalar) params.get("metaDescription");
        SimpleScalar pMetaRobots = (SimpleScalar) params.get("metaRobots");
        SimpleScalar pMetaKeywords = (SimpleScalar) params.get("metaKeywords");

        App app = App.get();

        PageSupport pageModel = null;

        String title = null;
        String uri = null;
        String canonicalURI = null;
        String metaDescription = null;
        String metaRobots = null;
        String metaKeywords = null;

        if (pPageModel != null) {
            Object beanModel = ((BeanModel) pPageModel).getWrappedObject();

            if (beanModel instanceof PageSupport) {
                pageModel = (PageSupport) beanModel;

                if (pageModel.getTitle() != null
                    && !Str.isEmpty(ContextObjects.findCurrentLanguageOrGlobal(pageModel.getTitle()))) {
                    title = pageModel.getTitle().getString();
                }

                if (pageModel.getURI() != null) {
                    uri = ContextObjects.findCurrentLanguageOrGlobal(pageModel.getURI());
                }

                if (pageModel.getCanonicalURI() != null) {
                    canonicalURI = ContextObjects.findCurrentLanguageOrGlobal(pageModel.getCanonicalURI());
                }

                if (pageModel.getMetaDescription() != null
                    && !Str.isEmpty(ContextObjects.findCurrentLanguageOrGlobal(pageModel.getMetaDescription()))) {
                    metaDescription = pageModel.getMetaDescription().getString();
                }

                if (pageModel.getMetaRobots() != null && !Str.isEmpty(pageModel.getMetaRobots().getString())) {
                    metaRobots = pageModel.getMetaRobots().getString();
                }
            } else {
                throw new IllegalArgumentException("The pageModel-object must be of type PageSupport");
            }
        }

        if (pTitle != null)
            title = pTitle.getAsString();

        if (pURI != null)
            uri = pURI.getAsString();

        if (pMetaDescription != null)
            metaDescription = pMetaDescription.getAsString();

        if (pMetaRobots != null)
            metaRobots = pMetaRobots.getAsString();

        if (metaRobots == null)
            metaRobots = "index,follow";

        if (pMetaKeywords != null)
            metaKeywords = pMetaKeywords.getAsString();

        HttpServletRequest request = app.servletRequest();
        String requestURI = app.getOriginalURI();
        String actionURI = app.getActionURI();

        if (Str.isEmpty(requestURI))
            requestURI = request.getRequestURI();

        boolean isURIOriginal = false;

        if (uri != null) {
            String uri1 = uri.trim();
            String uri2 = requestURI.trim();

            if (uri1.endsWith("/") && !uri2.endsWith("/"))
                uri2 = uri2 + "/";

            else if (!uri1.endsWith("/") && uri2.endsWith("/"))
                uri2 = uri2.substring(0, uri2.length() - 1).trim();

            isURIOriginal = uri1.equals(uri2) && Str.isEmpty(request.getQueryString());
        } else {
            isURIOriginal = Str.isEmpty(request.getQueryString());
        }

        StringBuilder html = new StringBuilder();

        String metaTitleSuffix = app.cpStr_("general/seo/meta_title_suffix", "");

        if (title != null)
            html.append("<title>").append(com.geecommerce.core.util.Strings.truncateNicely(stripTags(title),
                65 - metaTitleSuffix.length(), "...")).append(metaTitleSuffix).append("</title>\n");

        if (Strings.isNotEmpty(metaDescription)) {
            int maxLength = app.cpInt_(META_DESC_MAX_LENGTH_CONF_KEY, 160);

            metaDescription = metaDescription.replaceAll("\"", "'");
            metaDescription = metaDescription.replaceAll("\n", "");

            if (metaDescription.indexOf("<li>") != -1)
                metaDescription = metaDescription.replace("<li>", ", ");

            metaDescription = stripTags(metaDescription).trim();

            if (metaDescription.startsWith(","))
                metaDescription = metaDescription.substring(1).trim();

            metaDescription = metaDescription + metaTitleSuffix;

            html.append("<meta name=\"description\" content=\"")
                .append(com.geecommerce.core.util.Strings.truncateNicely(metaDescription, maxLength, "..."))
                .append("\"/>\n");
        }

        // if (app.isSecureRequest())
        // {
        // metaRobots = "noindex,nofollow";
        // }
        // else
        if (!isURIOriginal || isInNoIndexList(requestURI) || isInNoIndexList(actionURI)) {
            if (!Str.isEmpty(metaRobots) && metaRobots.toLowerCase().indexOf("nofollow") != -1) {
                metaRobots = "noindex,nofollow";
            } else {
                metaRobots = "noindex,follow";
            }
        }

        if (!Str.isEmpty(metaKeywords)) {
            html.append("<meta name=\"keywords\" content=\"").append(metaKeywords).append("\"/>\n");
        }

        if (canonicalURI == null || metaRobots.startsWith("noindex"))
            html.append("<meta name=\"robots\" content=\"").append(metaRobots).append("\"/>\n");

        if (!metaRobots.startsWith("noindex") && canonicalURI == null)
            html.append("<meta name=\"revisit-after\" content=\"").append("7 days").append("\"/>\n");

        if (canonicalURI != null && !metaRobots.startsWith("noindex")) {
            html.append("<link rel=\"canonical\" href=\"")
                .append(Requests.buildAbsoluteURL(request, canonicalURI.trim())).append("\"/>");
        }

        env.getOut().write(html.toString());
    }

    private boolean isInNoIndexList(String requestURI) {
        if (requestURI == null)
            return false;

        for (String uri : URI_NOINDEX_LIST) {
            if (requestURI.trim().startsWith(uri))
                return true;
        }

        return false;
    }
}
