package com.geecommerce.guiwidgets.controller;

import com.geecommerce.core.web.BaseActionBean;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.google.inject.Inject;
import net.sourceforge.stripes.action.*;

import javax.ws.rs.POST;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;

@UrlBinding("/cms/files/{$event}")
public class FileManagerCKEditorAction extends BaseActionBean {
    private static final String GROUP_CMS = "cms";

    private final MediaAssetService mediaAssetService;

    private FileBean upload;
    private HashMap<String, String> imageMap;
    private String filename;
    private MediaAsset mediaAsset;
    static final String[] EXTENSIONS = new String[]{"jpg", "png", "jpeg" // and other formats you need
    };
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    @Inject
    public FileManagerCKEditorAction(MediaAssetService mediaAssetService) {
        this.mediaAssetService = mediaAssetService;
    }

    @DefaultHandler
    public Resolution view() {
        imageMap = new HashMap();

        for (MediaAsset ma : mediaAssetService.findByGroup(GROUP_CMS)) {
            imageMap.put(ma.getName().getStr(), ma.getUrl());
        }

        return view("ckeditor/view");
    }

    @HandlesEvent("test")
    public Resolution test() {

        return view("ckeditor/test");
    }

    @POST
    @HandlesEvent("upload")
    public Resolution uploadFile() {

        String errorMsg = null;

        if (upload != null) {

            try {
                mediaAsset = mediaAssetService.create(upload.getInputStream(), upload.getFileName());
                mediaAsset.setGroup(GROUP_CMS);
                mediaAssetService.update(mediaAsset);
                filename = mediaAsset.getName().getStr();
            } catch (IOException e) {
                errorMsg = e.getMessage();
                return new StreamingResolution("text/xml", errorMsg);
            }
            return view("ckeditor/file_select");
        }
        return new StreamingResolution("text/xml", "An unknown error has occurred!");
    }

    public FileBean getUpload() {
        return upload;
    }

    public void setUpload(FileBean upload) {
        this.upload = upload;
    }

    public HashMap<String, String> getImageMap() {
        return imageMap;
    }

    public void setImageMap(HashMap<String, String> imageMap) {
        this.imageMap = imageMap;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public MediaAsset getMediaAsset() {
        return mediaAsset;
    }

    public void setMediaAsset(MediaAsset mediaAsset) {
        this.mediaAsset = mediaAsset;
    }
}