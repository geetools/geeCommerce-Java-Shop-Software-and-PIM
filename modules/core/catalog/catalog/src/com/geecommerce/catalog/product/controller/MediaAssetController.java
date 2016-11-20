package com.geecommerce.catalog.product.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.commons.io.FileUtils;

import com.geecommerce.catalog.product.helper.CatalogMediaHelper;
import com.geecommerce.core.media.MimeType;
import com.geecommerce.core.util.Requests;
import com.geemvc.Results;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

@Controller
@Request("/c/media")
public class MediaAssetController {
    protected final CatalogMediaHelper mediaHelper;

    @Inject
    public MediaAssetController(CatalogMediaHelper mediaHelper) {
        this.mediaHelper = mediaHelper;
    }

    @Request("/**")
    public Result render(@Context HttpServletRequest request) {
        String mediaAssetURI = Requests.getURIWithoutContextPath(request);
        String absSystemPath = mediaHelper.toAbsoluteSystemPath(mediaAssetURI);

        InputStream is = null;

        if (MimeType.isImage(absSystemPath)) {
            byte[] bytes = mediaHelper.getImage(mediaAssetURI);

            if (bytes != null && bytes.length > 0) {
                is = new ByteArrayInputStream(bytes);
            }

            if (is == null) {
                return null; // TODO 404
            } else {
                Result strResult = Results.stream(MimeType.fromFilename(mediaAssetURI), is);
                setDownloadingMode(strResult, request, mediaAssetURI);
                return strResult;
            }
        } else {
            try {
                byte[] buf = FileUtils.readFileToByteArray(new File(absSystemPath));

                if (buf == null || buf.length <= 0) {
                    return null; // TODO 404
                } else {
                    Result strResult = Results.stream(MimeType.fromFilename(mediaAssetURI),
                        new ByteArrayInputStream(buf));
                    strResult.rangeSupport(true);
                    strResult.length(buf.length);

                    return strResult;
                }

            } catch (Throwable t) {
                System.out.println(t.getLocalizedMessage());
            }

            return null; // TODO 404;
        }
    }

    protected void setDownloadingMode(Result strResult, HttpServletRequest request, String path) {
        String val = request.getParameter("d");
        if (val != null && Boolean.parseBoolean(val)) {
            strResult.attachment(true);

            Path p = Paths.get(path);
            String file = p.getFileName().toString();
            strResult.filename(file);
        }
    }
}
