package com.geecommerce.mediaassets.helper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import com.geecommerce.core.App;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.converter.DocumentConverter;
import com.geecommerce.mediaassets.converter.DocxToPdfConverter;
import com.geecommerce.mediaassets.converter.OdtToPdfConverter;
import com.geecommerce.mediaassets.converter.PptToPdfConverter;
import com.geecommerce.mediaassets.converter.PptxToPdfConverter;
import com.geecommerce.mediaassets.converter.XslToPdfConverter;
import com.geecommerce.mediaassets.converter.XslxToPdfConverter;
import com.geecommerce.mediaassets.model.MediaAssetFile;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.google.inject.Inject;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

@Helper
public class DefaultMediaAssetHelper implements MediaAssetHelper {
    @Inject
    protected App app;

    protected final MediaAssetService mediaAssetService;
    protected final Connections connections;

    @Inject
    public DefaultMediaAssetHelper(MediaAssetService mediaAssetService, Connections connections) {
        this.mediaAssetService = mediaAssetService;
        this.connections = connections;
    }

    @Override
    public Id createPreviewPdfFile(GridFSDBFile file) {
        for (DocumentConverter converter : getConverters()) {
            if (converter.canConvert(file.getContentType())) {
                ByteArrayOutputStream stream = converter.convert(file.getInputStream());
                if (stream == null)
                    return null;

                Id id = app.nextId();
                createGridFsFile(id, new ByteArrayInputStream(stream.toByteArray()), "preview_" + file.getFilename(), "application/pdf");
                return id;
            }
        }
        return null;
    }

    @Override
    public Id createPreviewImage(GridFSDBFile file) {

        PDDocument document = null;
        try {
            document = PDDocument.load(file.getInputStream());

            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIOUtil.writeImage(bim, "png", os, 300);
            InputStream is = new ByteArrayInputStream(os.toByteArray());

            Id id = app.nextId();
            createGridFsFile(id, is, "preview_" + file.getFilename().replace('.', '_') + ".png", "image/png");
            return id;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (document != null)
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return null;
    }

    @Override
    public void createPreview(MediaAssetFile mediaAssetFile, GridFSDBFile file) {
        if (file.getContentType().contains("image")) {
            mediaAssetFile.setPreviewImageId(mediaAssetFile.getId());
            mediaAssetFile.setPreviewDocumentMimeType(mediaAssetFile.getMimeType());
        } else if (file.getContentType().contains("pdf")) {
            mediaAssetFile.setPreviewDocId(mediaAssetFile.getId());
            mediaAssetFile.setPreviewDocumentMimeType("aplication/pdf");
            mediaAssetFile.setPreviewImageId(createPreviewImage(file));
            mediaAssetFile.setPreviewDocumentMimeType("image/png");
        } else {
            Id docId = createPreviewPdfFile(file);
            if (docId != null) {
                mediaAssetFile.setPreviewDocId(docId);
                mediaAssetFile.setPreviewDocumentMimeType("aplication/pdf");
                Id imgId = createPreviewImage(mediaAssetService.getGridFsFile(docId));
                if (imgId != null) {
                    mediaAssetFile.setPreviewImageId(imgId);
                    mediaAssetFile.setPreviewDocumentMimeType("image/png");
                }
            }
        }
    }

    public long createGridFsFile(Id id, InputStream inputStream, String filename, String mimeType) {
        DB db = (DB) connections.getConnection("mongodb.dma");
        GridFS fs = new GridFS(db);
        GridFSInputFile gridFile = fs.createFile(inputStream, filename);
        gridFile.setId(id);
        gridFile.setContentType(mimeType);
        gridFile.save();
        return gridFile.getLength();
    }

    protected List<DocumentConverter> getConverters() {
        List<DocumentConverter> converters = new ArrayList<>();
        converters.add(new DocxToPdfConverter());
        converters.add(new OdtToPdfConverter());
        converters.add(new XslToPdfConverter());
        converters.add(new XslxToPdfConverter());
        converters.add(new PptToPdfConverter());
        converters.add(new PptxToPdfConverter());
        return converters;
    }
}
