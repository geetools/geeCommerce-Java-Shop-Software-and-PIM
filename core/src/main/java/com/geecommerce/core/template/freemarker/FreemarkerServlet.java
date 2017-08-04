package com.geecommerce.core.template.freemarker;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geemodule.api.Module;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateModel;

public class FreemarkerServlet extends freemarker.ext.servlet.FreemarkerServlet {
    private static final long serialVersionUID = 7045310638565512231L;
    private static final String BASE_PAGES_PATH = "/pages/";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    public void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        App app = App.get();

        String path = requestUrlToTemplatePath(request);
        String viewPath = app.getViewPath();

        if (viewPath == null && !Str.isEmpty(path)) {
            int pos1 = path.indexOf(BASE_PAGES_PATH) + BASE_PAGES_PATH.length();
            int pos2 = path.lastIndexOf(Char.DOT);

            app.setViewPath(path.substring(pos1, pos2));
        }

        StringWriter sw = new StringWriter();
        Template t = null;

        try {
            Module m = app.getTargetModule();

            if (m == null) {
                m = app.getCurrentModule();
            }

            Configuration conf = FreemarkerHelper.newConfig(app.servletContext(), m);

            app.servletResponse().setLocale(conf.getLocale());
            app.servletResponse().setCharacterEncoding("UTF-8");

            TemplateModel tm = FreemarkerHelper.createModel(ObjectWrapper.DEFAULT_WRAPPER, app.servletContext(),
                app.servletRequest(), app.servletResponse());

            app.registryPut(FreemarkerConstant.FREEMARKER_REQUEST_TEMPLATE_MODEL, tm);

            t = conf.getTemplate(path, conf.getLocale(), "UTF-8");

            Environment env = t.createProcessingEnvironment(tm, sw);

            env.setLocale(conf.getLocale());

            env.process(); // process the template

            response.getWriter().write(sw.toString());
        } catch (Throwable th) {
            th.printStackTrace();

            if (app.isDevPrintErrorMessages()) {
                System.out.println("An error occured while rendering template: " + (t == null ? path : t.getName()));
                th.printStackTrace();
            }

            throw new RuntimeException(th.getMessage(), th);
        }
    }
}
