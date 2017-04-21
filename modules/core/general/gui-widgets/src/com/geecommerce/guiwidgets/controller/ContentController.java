package com.geecommerce.guiwidgets.controller;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.template.freemarker.FreemarkerConstant;
import com.geecommerce.core.template.freemarker.FreemarkerHelper;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.web.BaseController;
import com.geecommerce.guiwidgets.model.Content;
import com.geecommerce.guiwidgets.model.ContentNode;
import com.geecommerce.guiwidgets.repository.Contents;
import com.geecommerce.guiwidgets.service.ContentService;
import com.geemodule.api.Module;
import com.geemodule.api.ModuleClassLoader;
import com.geemvc.Results;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.bind.param.annotation.Param;
import com.geemvc.bind.param.annotation.PathParam;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateModel;

@Controller
@Request("/content")
public class ContentController extends BaseController {

    private Content content = null;
    private String node;
    private String htmlContent;

    private final Contents contents;
    private final ContentService contentService;
    private final ProductService productService;

    @Inject
    public ContentController(Contents contents, ContentService contentService, ProductService productService) {
        this.contents = contents;
        this.contentService = contentService;
        this.productService = productService;
    }

    @Request
    public Result view() {
        return Results.view("content/view");
    }

    @Request("editor")
    public Result getEditor() {
        return Results.view("content/editor");
    }

    @Request("preview/{id}")
    public Result getPreview(@PathParam("id") Id id) {
        return Results.view("content/preview").bind("contentId", id);
    }

    @Request("storefront")
    public Result getStorefront() {
        return Results.view("content/storefront");
    }

    @Request("page/{id}")
    public Result getPage(@PathParam("id") Id id, @Context HttpServletRequest request,
        @Context HttpServletResponse response) {
        Content content = contents.findById(Content.class, id);

        app.setViewPath("page/content/view_" + id.str());
        app.setActionURI(request.getRequestURI());

        Result result = freemarkerTemplateStream(content.getTemplate(), "3h");
        if (content.getPreviewProductId() != null) {
            result.bind("product", productService.getProduct(content.getPreviewProductId()));
        }
        return result;
    }

    @Request("product/{id}")
    public Result getProductPage(@PathParam("id") Id id, @Context HttpServletRequest request,
                          @Context HttpServletResponse response) {

        Product product = productService.getProduct(id);
        Content content = contents.findById(Content.class, product.getTemplateId());

        app.setViewPath("page/content/view_" + content.getId().str() + "_" + product.getId());
        app.setActionURI(request.getRequestURI());

        Result result = freemarkerTemplateStream(content.getTemplate(), "3h");
        if (content.getPreviewProductId() != null) {
            result.bind("product", product);
        }
        return result;
    }

    @Request("preview-node/{id}")
    public Result getPreviewNode(@Param("node") String node, @PathParam("id") Id contentId) throws IOException {
        Product product = null;
        try {
            Content content = contents.findById(Content.class, contentId);
            if (content != null) {
                if (content.getPreviewProductId() != null) {
                    product = productService.getProduct(content.getPreviewProductId());
                }
            }
            if (!StringUtils.isBlank(node)) {
                node = StringEscapeUtils.unescapeHtml(node);
                Map<String, Object> nodeMap = Json.fromJson(node, HashMap.class);

                ContentNode contentNode = app.model(ContentNode.class);
                contentNode.fromMap(nodeMap);
                System.out.println(node);

                htmlContent = contentService.generateNode(contentNode);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            // just some error
        }
        // return renderContent(htmlContent);
        Result result = Results.view("content/preview-node").bind("htmlContent", htmlContent);
        if (product != null) {
            result.bind("product", product);
        }

        return result;
    }

    protected Result freemarkerTemplateStream(String content, String cacheFor) {
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

        return Results.stream("text/html", sw.toString());
    }

    /*
     * public String getNode() { return node; }
     * 
     * public void setNode(String node) { this.node = node; }
     * 
     * public String getHtmlContent() { return htmlContent; }
     * 
     * public void setHtmlContent(String htmlContent) { this.htmlContent =
     * htmlContent; }
     */
}