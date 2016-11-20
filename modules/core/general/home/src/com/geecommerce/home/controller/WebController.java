package com.geecommerce.home.controller;

import java.io.File;

import javax.ws.rs.core.MediaType;

import com.geecommerce.core.Char;
import com.geecommerce.core.config.SystemConfig;
import com.geecommerce.core.web.BaseController;
import com.geemodule.api.Module;
import com.geemodule.api.ModuleLoader;
import com.geemvc.Results;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.bind.param.annotation.PathParam;
import com.geemvc.view.bean.Result;

import freemarker.template.Template;

@Controller
@Request("/web")
public class WebController extends BaseController {
    private String module = null;
    private String slice = null;

    @Request("js-app")
    public Result jsApp() {
        return Results.view("web/js-app");
    }

    @Request("js-app-cms")
    public Result jsAppCms() {
        return Results.view("web/js-app-cms");
    }

    @Request("js-settings")
    public Result jsSettings() {
        return Results.view("web/js-settings");
    }

    @Request(path = "/slice/{module}/{slice:.*}", produces = { MediaType.TEXT_HTML, MediaType.TEXT_PLAIN,
        MediaType.TEXT_XML, MediaType.APPLICATION_JSON })
    public Result slice(@PathParam("module") String module, @PathParam("slice") String slice) {
        ModuleLoader ml = app.moduleLoader();
        Template t = null;

        app.setViewPath(slice);
        app.setActionURI(getRequest().getRequestURI());

        String templateSuffix = SystemConfig.GET.val(SystemConfig.APPLICATION_TEMPLATE_SUFFIX);

        if (templateSuffix == null) {
            throw new IllegalStateException(
                "The System.properties configuration element 'Application.Template.Suffix' cannot be null");
        }

        templateSuffix = templateSuffix.trim();

        if (ml != null) {
            Module m = ml.getLoadedModuleByCode(module);

            if (m != null) {
                String basePath = m.getBasePath();

                if (basePath != null) {
                    try {
                        File f = new File(basePath, "templates/slices/" + slice + ".ftl");

                        System.out.println("SLICE PATH:: " + f.getAbsolutePath());

                        if (f.exists()) {
                            app.setTargetModule(m);
                            return Results.view(new StringBuilder(getSlicesPath()).append(Char.SLASH).append(slice)
                                .append(templateSuffix).toString());
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();

                        if (app.isDevPrintErrorMessages()) {
                            System.out.println(
                                "An error occured while rendering template: " + (t == null ? slice : t.getName()));
                            th.printStackTrace();
                        }

                        throw new RuntimeException(th.getMessage(), th);
                    }
                }
            }
        }

        return null;
    }
}
