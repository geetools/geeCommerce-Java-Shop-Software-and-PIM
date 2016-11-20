package com.geecommerce.mediaassets.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Requests;
import com.geecommerce.core.util.Strings;
import com.geecommerce.mediaassets.helper.MediaAssetHelper;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.model.MediaAssetFile;
import com.geecommerce.mediaassets.model.MediaAssetUrl;
import com.geecommerce.mediaassets.repository.MediaAssetFiles;
import com.geecommerce.mediaassets.repository.MediaAssets;
import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

@Service
public class DefaultMediaAssetService implements MediaAssetService {
    @Inject
    protected App app;

    protected static final String DMA_SERVLET_PATH_CONFIG_KEY = "dma/default/servlet_path";
    protected static final String DMA_SUBDOMAIN_CONFIG_KEY = "dma/default/subdomain";
    protected static final String DMA_GETPATH_KEY = "dma/default/getpath";
    protected static final String DMA_HTTPS_ACTIVE_KEY = "dma/default/https/active";
    protected static final String DMA_HTTP_SCHEME_KEY = "dma/default/http/scheme";
    protected static final String DMA_HTTPS_SCHEME_KEY = "dma/default/https/scheme";

    protected static final String DMA_DEFAULT_HTTP_SCHEME = "http";
    protected static final String DMA_DEFAULT_HTTPS_SCHEME = "https";
    protected static final String DMA_SCHEME_DEFAULT = "http";
    protected static final String DMA_SERVLET_PATH_DEFAULT = "/dma";
    protected static final String DMA_GETPATH_DEFAULT = "/get";

    protected final MediaAssets mediaAssets;
    protected final MediaAssetHelper mediaAssetHelper;
    protected final Connections connections;
    protected final MediaAssetFiles mediaAssetFiles;

    @Inject
    public DefaultMediaAssetService(MediaAssets mediaAssets, MediaAssetHelper mediaAssetHelper, Connections connections,
        MediaAssetFiles mediaAssetFiles) {
        this.mediaAssets = mediaAssets;
        this.mediaAssetHelper = mediaAssetHelper;
        this.connections = connections;
        this.mediaAssetFiles = mediaAssetFiles;
    }

    @Override
    public MediaAsset update(MediaAsset mediaAsset) {
        mediaAssets.update(mediaAsset);
        setMediaAssetUrl(mediaAsset);
        return mediaAsset;
    }

    @Override
    public void remove(MediaAsset mediaAsset) {
        mediaAssets.remove(mediaAsset);
    }

    @Override
    public MediaAsset update(Id mediaAssetId, InputStream inputStream, String filename) {

        String basename = FilenameUtils.getBaseName(filename);
        String extension = FilenameUtils.getExtension(filename);
        filename = Strings.slugify2(basename) + "." + extension;
        String mimeType = getMimeType(filename);

        MediaAssetFile mediaAssetFile = app.model(MediaAssetFile.class);
        mediaAssetFile.setId(app.nextId());
        mediaAssetFile.setMediaAssetId(mediaAssetId);
        mediaAssetFile.setName(filename);
        mediaAssetFile.setMimeType(mimeType);
        mediaAssetFile.setActive(false);
        mediaAssetFile
            .setSize(createGridFsFile(mediaAssetFile.getId(), inputStream, filename, mediaAssetFile.getMimeType()));
        GridFSDBFile file = getGridFsFile(mediaAssetFile.getId());
        mediaAssetFile.setMetadata(importMetadata(file.getInputStream()));
        mediaAssetHelper.createPreview(mediaAssetFile, file);
        mediaAssetFiles.add(mediaAssetFile);

        return mediaAssets.findById(MediaAsset.class, mediaAssetId);

    }

    @Override
    public MediaAsset get(Id id) {
        MediaAsset mediaAsset = mediaAssets.findById(MediaAsset.class, id);
        if (mediaAsset != null) {
            setMediaAssetUrl(mediaAsset);
            return mediaAsset;
        }
        return null;
    }

    /*
     * @Override public MediaAssetFile getContent(Id id) { MediaAsset mediaAsset
     * = get(id); GridFSDBFile file = getGridFsFile(id); MediaAssetFile
     * mediaAssetFile = app.getModel(MediaAssetFile.class);
     * mediaAssetFile.setName(mediaAsset.getName());
     * mediaAssetFile.setMimeType(mediaAsset.getMimeType());
     * mediaAssetFile.setContent(file.getInputStream()); return mediaAssetFile;
     * }
     */

    @Override
    public List<MediaAsset> get(List<Id> ids) {
        List<MediaAsset> mediaAssetList = mediaAssets.findByIds(MediaAsset.class, ids.toArray(new Id[ids.size()]));
        for (MediaAsset mediaAsset : mediaAssetList) {
            setMediaAssetUrl(mediaAsset);
        }
        return mediaAssetList;
    }

    @Override
    public MediaAsset create(InputStream inputStream, String filename) {
        String basename = FilenameUtils.getBaseName(filename);
        String extension = FilenameUtils.getExtension(filename);
        filename = Strings.slugify2(basename) + "." + extension;
        String mimeType = getMimeType(filename);

        MediaAsset mediaAsset = app.model(MediaAsset.class);
        mediaAsset.setEnabled(true);
        mediaAsset.setId(app.nextId());

        mediaAsset.setName(new ContextObject<>(mediaAsset.getId() + "-" + filename));
        // mediaAsset.setName(mediaAsset.getId() + "-" + filename);
        mediaAsset.setMimeType(mimeType);

        MediaAssetFile mediaAssetFile = app.model(MediaAssetFile.class);
        mediaAssetFile.setId(app.nextId());
        mediaAssetFile.setMediaAssetId(mediaAsset.getId());
        mediaAssetFile.setName(filename);
        mediaAssetFile.setMimeType(mimeType);
        mediaAssetFile.setActive(true);
        mediaAssetFile
            .setSize(createGridFsFile(mediaAssetFile.getId(), inputStream, filename, mediaAssetFile.getMimeType()));
        GridFSDBFile file = getGridFsFile(mediaAssetFile.getId());
        mediaAssetFile.setMetadata(importMetadata(file.getInputStream()));
        mediaAssetHelper.createPreview(mediaAssetFile, file);

        mediaAsset = mediaAssets.add(mediaAsset);
        mediaAssetFile = mediaAssetFiles.add(mediaAssetFile);
        setMediaAssetUrl(mediaAsset);
        return mediaAsset;
    }

    private Map<Object, Object> importMetadata(InputStream inputStream) {
        Metadata metadata = new Metadata();
        ContentHandler handler = new BodyContentHandler();
        AutoDetectParser parser = new AutoDetectParser();

        try {
            parser.parse(inputStream, handler, metadata);
        } catch (IOException e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
        } catch (TikaException e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
        } catch (Exception e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace(); // To change body of catch statement use
                                     // File | Settings | File Templates.
            }
        }

        Map<Object, Object> mt = new HashMap<>();
        for (String name : metadata.names()) {
            mt.put(name.replace('.', '_'), metadata.get(name));
        }
        return mt;
    }

    private String getMimeType(String filename) {
        Path fullPath = Paths.get(filename);
        String mimeType = null;
        try {
            mimeType = Files.probeContentType(fullPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mimeType == null) {
            Tika tika = new Tika();
            mimeType = tika.detect(filename);
        }
        return mimeType;
    }

    private long createGridFsFile(Id id, InputStream inputStream, String filename, String mimeType) {
        DB db = (DB) connections.getConnection("mongodb.dma");
        GridFS fs = new GridFS(db);
        GridFSInputFile gridFile = fs.createFile(inputStream, filename);
        gridFile.setId(id);
        gridFile.setContentType(mimeType);
        gridFile.save();
        return gridFile.getLength();
    }

    private void removeGridFsFile(Id id) {
        DB db = (DB) connections.getConnection("mongodb.dma");
        GridFS fs = new GridFS(db);
        GridFSDBFile file = fs.findOne(new BasicDBObject("_id", id.num()));
        if (file != null) {
            fs.remove(new BasicDBObject("_id", id.num()));
        }
    }

    @Override
    public GridFSDBFile getGridFsFile(Id id) {
        DB db = (DB) connections.getConnection("mongodb.dma");
        GridFS fs = new GridFS(db);
        GridFSDBFile file = fs.findOne(new BasicDBObject("_id", id.num()));
        return file;
    }

    @Override
    public void setMediaAssetUrl(MediaAsset mediaAsset) {
        if (mediaAsset == null)
            return;

        MediaAssetUrl mediaAssetUrl = (MediaAssetUrl) mediaAsset;

        StringBuilder dmaURL = new StringBuilder();

        HttpServletRequest request = app.servletRequest();

        String httpScheme = app.cpStr_(DMA_HTTP_SCHEME_KEY, DMA_DEFAULT_HTTP_SCHEME);
        String httpsScheme = app.cpStr_(DMA_HTTPS_SCHEME_KEY, DMA_DEFAULT_HTTPS_SCHEME);
        boolean isHttpsActive = app.cpBool_(DMA_HTTPS_ACTIVE_KEY, false);

        String scheme = request != null ? (isHttpsActive && app.isSecureRequest() ? httpsScheme : httpScheme)
            : DMA_SCHEME_DEFAULT;
        String servletPath = app.cpStr_(DMA_SERVLET_PATH_CONFIG_KEY, DMA_SERVLET_PATH_DEFAULT);
        String subdomain = app.cpStr_(DMA_SUBDOMAIN_CONFIG_KEY, Requests.getHost(request));
        String getPath = app.cpStr_(DMA_GETPATH_KEY, DMA_GETPATH_DEFAULT);

        // if neither request nor sub-domain are available, we can only return a
        // relative path.
        if (request == null && subdomain == null) {
            dmaURL.append(servletPath);
        } else {
            if (subdomain == null) {
                dmaURL.append(app.isSecureRequest() ? app.getSecureBasePath() : app.getBasePath()).append(servletPath);
            } else {
                dmaURL.append(scheme).append(Str.PROTOCOL_SUFFIX).append(subdomain).append(servletPath);
            }
        }

        // TODO:FIX
        /*
         * if (!Str.isEmpty(mediaAsset.getName().)) {
         * dmaURL.append(Char.SLASH).append(mediaAsset.getName()); } else {
         * dmaURL.append(getPath).append(Char.SLASH).append(mediaAsset.getId());
         * }
         */

        dmaURL.append(getPath).append(Char.SLASH).append(mediaAsset.getId());

        mediaAssetUrl.setUrl(dmaURL.toString());
    }

    @Override
    public MediaAsset findByName(String name) {
        return mediaAssets.havingName(name);
    }

    @Override
    public List<MediaAsset> findByGroup(String group) {
        return mediaAssets.havingGroup(group);
    }
}
