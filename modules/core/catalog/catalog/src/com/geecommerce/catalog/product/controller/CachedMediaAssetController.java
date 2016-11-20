package com.geecommerce.catalog.product.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;

import com.geecommerce.catalog.product.helper.CatalogMediaHelper;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.media.MimeType;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.util.Requests;
import com.geecommerce.core.web.BaseController;
import com.geemvc.Results;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.bind.param.annotation.PathParam;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

@Controller
@Request("/cache")
public class CachedMediaAssetController extends BaseController {
    private final CatalogMediaHelper mediaHelper;

    @Inject
    public CachedMediaAssetController(CatalogMediaHelper mediaHelper) {
        this.mediaHelper = mediaHelper;
    }

    @Request("/{cacheGroup}/c/media/**")
    public Result render(@PathParam("cacheGroup") String cacheGroup) {
        HttpServletRequest request = getRequest();

        String mediaAssetURI = Requests.getURIWithoutContextPath(request);

        ApplicationContext appCtx = app.context();
        Store store = appCtx.getStore();
        String mimeType = MimeType.fromFilename(mediaAssetURI);

        String baseCacheSystemPath = mediaHelper.getBaseCacheSystemPath(mimeType, store.getId());

        String absSystemPath = mediaAssetURI.replace("/cache", baseCacheSystemPath);

        File f = new File(absSystemPath);

        if (f.exists()) {
            try {
                byte[] buf = FileUtils.readFileToByteArray(new File(absSystemPath));

                if (buf == null || buf.length <= 0) {
                    return view("/error/404");
                } else {
                    // StreamingResolution streamingResolution = new
                    // StreamingResolution(mimeType, new
                    // ByteArrayInputStream(buf));
                    // setDownloadingMode(streamingResolution, request,
                    // mediaAssetURI);
                    // streamingResolution.setRangeSupport(true);
                    // streamingResolution.setLength(buf.length);

                    Result strResult = Results.stream(mimeType, new ByteArrayInputStream(buf));
                    setDownloadingMode(strResult, request, mediaAssetURI);
                    strResult.rangeSupport(true);
                    strResult.length(buf.length);
                    return strResult;

                    // return streamingResolution;
                }

            } catch (Throwable t) {
                System.out.println(t.getLocalizedMessage());
            }
        }

        return view("/error/404");
    }

    private void setDownloadingMode(Result strResult, HttpServletRequest request, String path) {
        String val = request.getParameter("d");
        if (val != null && Boolean.parseBoolean(val)) {
            strResult.attachment(true);

            Path p = Paths.get(path);
            String file = p.getFileName().toString();
            strResult.filename(file);
        }
    }
}
