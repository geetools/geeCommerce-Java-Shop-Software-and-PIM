package com.geecommerce.mediaassets.controller;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;

import com.geecommerce.core.media.MimeType;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.BaseController;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.model.MediaAssetFile;
import com.geecommerce.mediaassets.repository.MediaAssetFiles;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.geemvc.Results;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.bind.param.annotation.PathParam;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.gridfs.GridFSDBFile;

import net.sourceforge.stripes.action.StreamingResolution;

@Controller
@Singleton
@Request("/dma")
public class MediaAssetController extends BaseController {
    protected final MediaAssetService mediaAssetService;
    protected final MediaAssetFiles mediaAssetFiles;

    protected Id id = null;

    protected static final Pattern patternIncrement = Pattern.compile(".*__([\\d]+)\\.[a-zA-Z]+$");
    protected static final Pattern patternImageParameters = Pattern.compile("___([^_]+)\\.[a-zA-Z]+$");

    protected static final String IMAGE_PARAM_WIDTH = "width";
    protected static final String IMAGE_PARAM_HEIGHT = "height";
    protected static final String DELETED_SUFFIX = ".deleted";
    public static final String FILE_DOWNLOAD = "dl";

    protected static final char DOT = '.';

    protected static final Map<String, String> imageArgKeys = new HashMap<>();

    static {
        imageArgKeys.put("s", "size");
        imageArgKeys.put("t", "transparent");
        imageArgKeys.put("i", "in");
        imageArgKeys.put("o", "out");
    }

    @Inject
    public MediaAssetController(MediaAssetService mediaAssetService, MediaAssetFiles mediaAssetFiles) {
        this.mediaAssetService = mediaAssetService;
        this.mediaAssetFiles = mediaAssetFiles;
    }

    @Request("^/{filename:.+}")
    public Result getByUrl(@PathParam("filename") String filename) {
        if (filename != null) {
            // See if any parameters have been attached to the image.
            Map<String, Object> params = extractImageParameters(filename);

            if (params.size() == 0) {
                if (getRequest().getParameter(IMAGE_PARAM_WIDTH) != null) {
                    params.put(IMAGE_PARAM_WIDTH, Double.parseDouble(getRequest().getParameter(IMAGE_PARAM_WIDTH)));
                }
                if (getRequest().getParameter(IMAGE_PARAM_HEIGHT) != null) {
                    params.put(IMAGE_PARAM_HEIGHT, Double.parseDouble(getRequest().getParameter(IMAGE_PARAM_HEIGHT)));
                }
            }

            // If so, remove them to get the original image URI.
            if (params.size() > 0) {
                filename = removeImageParamsFromURI(filename);
            }

            MediaAsset ma = mediaAssetService.findByName(filename);

            Result viewStr = null;

            if (ma != null) {
                MediaAssetFile file = ma.getFile();// mediaAssetService.getContent(ma.getId());

                if (ma.getMimeType() != null && ma.getMimeType().contains("image") && params != null && params.size() != 0 && file != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    try {
                        ConvertCmd convert = new ConvertCmd();

                        BufferedImage originalImageBuff = ImageIO.read(file.getContent());
                        BufferedImage targetImageBuff = null;

                        Double targetWidth = (Double) params.get(IMAGE_PARAM_WIDTH);
                        Double targetHeight = (Double) params.get(IMAGE_PARAM_HEIGHT);

                        String imageType = imageType(ma.getMimeType(), filename);

                        // Are parameters for resizing set?
                        if (targetWidth != null || targetHeight != null) {
                            // If so, calculate new size maintaining the aspect-ratio.
                            double originalWidth = originalImageBuff.getWidth();
                            double originalHeight = originalImageBuff.getHeight();

                            double ratio = originalWidth / originalHeight;

                            if (targetWidth == null)
                                targetWidth = ratio < 1 ? targetHeight * ratio : targetHeight / ratio;

                            if (targetHeight == null)
                                targetHeight = ratio < 1 ? targetWidth * ratio : targetWidth / ratio;

                            targetWidth = Math.min(originalWidth, targetWidth);
                            targetHeight = Math.min(originalHeight, targetHeight);

                            if (ratio < 1) {
                                targetWidth = targetHeight * ratio;
                            } else {
                                targetHeight = targetWidth / ratio;
                            }

                            IMOperation op = new IMOperation();

                            // Original image.
                            op.addImage();

                            op.quality(75.0);
                            op.interlace("Plane");
                            op.depth(8);
                            op.thumbnail((int) Math.floor(targetWidth), (int) Math.floor(targetHeight));

                            int colorSpace = originalImageBuff.getColorModel().getColorSpace().getType();
                            if (colorSpace == ColorSpace.TYPE_CMYK)
                                op.colorspace("RGB");

                            // Target image.
                            op.addImage(imageType + ":-");

                            System.out.println(op.toString());

                            Stream2BufferedImage s2b = new Stream2BufferedImage();
                            convert.setOutputConsumer(s2b);

                            convert.run(op, originalImageBuff);

                            targetImageBuff = s2b.getImage();
                        }

                        byte[] outBytes = null;

                        if (targetImageBuff != null) {
                            ImageIO.write(targetImageBuff, imageType, baos);
                            outBytes = baos.toByteArray();
                        } else {
                            ImageIO.write(originalImageBuff, imageType, baos);
                            outBytes = baos.toByteArray();
                        }

                        return Results.stream(file.getMimeType(), new ByteArrayInputStream(outBytes));
                    } catch (Throwable t) {
                        t.printStackTrace();
                    } finally {
                        IOUtils.closeQuietly(baos);
                    }

                    viewStr = Results.stream(file.getMimeType(), file.getContent());

                    if (!ma.getMimeType().contains("image")) {
                        viewStr.filename(file.getName());
                    }

                    if (MimeType.APPLICATION_PDF.equals(ma.getMimeType())) {
                        String downloadParam = getRequest().getParameter(FILE_DOWNLOAD);
                        if (downloadParam != null && "1".equals(downloadParam))
                            viewStr.attachment(true);
                        else
                            viewStr.attachment(false);
                    }

                }

                return viewStr;
            }
        }

        return null;
    }

    @Request("/get/{id}")
    public Result get(@PathParam("id") Id id) {
        MediaAsset mediaAsset = mediaAssetService.get(id);

        MediaAssetFile file = mediaAsset.getFile();
        StreamingResolution streamingResolution = new StreamingResolution(file.getMimeType(), file.getContent());
        streamingResolution.setFilename(file.getName());

        Result viewStr = Results.stream(file.getMimeType(), file.getContent());

        if (!file.getMimeType().contains("image")) {
            viewStr.filename(file.getName());
        }

        if (MimeType.APPLICATION_PDF.equals(file.getMimeType())) {
            String downloadParam = getRequest().getParameter(FILE_DOWNLOAD);
            if (downloadParam != null && "1".equals(downloadParam))
                viewStr.attachment(true);
            else
                viewStr.attachment(false);
        }

        return viewStr;
    }

    @Request("/file/{id}")
    public Result file(@PathParam("id") Id id) {

        GridFSDBFile file = mediaAssetService.getGridFsFile(id);

        // mediaAssetFiles.
        // MediaAssetFile file = mediaAssetFiles.findById(MediaAssetFile.class, id);
        // StreamingResolution streamingResolution = new StreamingResolution(file.getContentType(), file.getInputStream());
        // streamingResolution.setFilename(file.getFilename());

        Result viewStr = Results.stream(file.getContentType(), file.getInputStream());

        if (!file.getContentType().contains("image")) {
            viewStr.filename(file.getFilename());
        }

        if (MimeType.APPLICATION_PDF.equals(file.getContentType())) {
            String downloadParam = getRequest().getParameter(FILE_DOWNLOAD);
            if (downloadParam != null && "1".equals(downloadParam))
                viewStr.attachment(true);
            else
                viewStr.attachment(false);
        }

        return viewStr;
    }

    protected String removeImageParamsFromURI(String imgUri) {
        return imgUri.replaceFirst("(.+)___(?:.+)\\.(.+)$", "$1.$2");
    }

    protected Map<String, Object> extractImageParameters(String imageUri) {
        Map<String, Object> params = new HashMap<>();

        Matcher m = patternImageParameters.matcher(imageUri);

        if (m.find()) {
            String group = m.group(1);

            String[] groupParts = group.split(",");

            for (String keyValue : groupParts) {
                String[] kv = keyValue.split(":");

                String key = imageArgKeys.get(kv[0]);
                String value = kv[1];

                if ("size".equals(key)) {
                    int xPos = value.indexOf("x");

                    Double width = null;
                    Double height = null;

                    if (xPos > 0) {
                        width = Double.parseDouble(value.substring(0, xPos));
                    }

                    if (xPos + 1 < value.length()) {
                        height = Double.parseDouble(value.substring(xPos + 1, value.length()));
                    }

                    if (width != null)
                        params.put("width", width);

                    if (height != null)
                        params.put("height", height);
                } else if (key != null && value != null) {
                    params.put(key, value);
                }
            }
        }

        return params;
    }

    protected String imageType(String mimeType, String filename) {
        if (mimeType == null)
            mimeType = MimeType.fromFilename(filename);

        return MimeType.toFileExtension(mimeType);
    }
}
