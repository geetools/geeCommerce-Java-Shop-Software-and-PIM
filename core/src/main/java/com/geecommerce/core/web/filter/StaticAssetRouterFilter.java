package com.geecommerce.core.web.filter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.config.SystemConfig;
import com.geemodule.api.Module;
import com.geemodule.api.ModuleLoader;

public class StaticAssetRouterFilter implements Filter {
    protected ServletContext servletContext;
    protected FilterConfig filterConfig;

    // Rewrite for locating all module assets.
    protected Pattern moduleAssetURIPattern = Pattern.compile("^\\/m\\/([^\\/]+)\\/(js|resources|skin)\\/(.+)");

    // Rewrite for module specific assets in project's folder.
    protected Pattern projectModuleAssetURIPattern = Pattern
        .compile("^(\\/[^\\/]+\\/web\\/(?:js|resources|skin).*)\\/m\\/(.+)");

    // Rewrite for all remaining assets in project's folder.
    protected Pattern projectAssetURIPattern = Pattern.compile("^\\/[^\\/]+\\/web\\/(?:js|resources|skin)\\/.+");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        App app = App.get();
        ApplicationContext appCtx = app.context();

        String requestURI = httpRequest.getRequestURI();

        Matcher m = projectModuleAssetURIPattern.matcher(requestURI);

        File asset = null;

        if (m.matches()) {
            String projectsPath = SystemConfig.GET.val(SystemConfig.APPLICATION_PROJECTS_PATH);

            StringBuilder projectModuleAssetSystemPath = new StringBuilder(projectsPath).append(Char.SLASH)
                .append(m.group(1)).append("/modules/").append(m.group(2));

            asset = new File(projectModuleAssetSystemPath.toString());
        }

        if (asset == null) {
            m = projectAssetURIPattern.matcher(requestURI);

            if (m.matches()) {
                String projectsPath = SystemConfig.GET.val(SystemConfig.APPLICATION_PROJECTS_PATH);

                StringBuilder projectAssetSystemPath = new StringBuilder(projectsPath).append(requestURI);

                asset = new File(projectAssetSystemPath.toString());
            }
        }

        if (asset == null) {
            m = moduleAssetURIPattern.matcher(requestURI);

            if (m.matches()) {
                ModuleLoader ml = app.moduleLoader();
                Module module = ml.getLoadedModuleByCode(m.group(1));

                StringBuilder moduleAssetSystemPath = new StringBuilder(module.getBasePath()).append("/web/")
                    .append(m.group(2)).append(Char.SLASH).append(m.group(3));

                asset = new File(moduleAssetSystemPath.toString());
            }
        }

        if (asset != null && !asset.exists()) {
            System.out.println("Asset file '" + asset.getAbsolutePath() + "' does not exist.");
            
            File[] files = asset.getParentFile().listFiles();
            
            if(files != null) {
                for (int i = 0; i < files.length; i++) {
                    System.out.println("Asset file " + files[i]);
                }
            }
        }
        
        if (asset != null && asset.exists()) {
            String mimeType = URLConnection.guessContentTypeFromName(asset.getName());
            response.setContentType(mimeType);
            response.setContentLength(Long.valueOf(asset.length()).intValue());

            try (OutputStream out = response.getOutputStream()) {
                Path path = asset.toPath();
                Files.copy(path, out);
                out.flush();
            } catch (IOException e) {
                // handle exception
            }
        } else {
            // If request is not a static asset located in module or project's
            // folder, continue as normal.
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
