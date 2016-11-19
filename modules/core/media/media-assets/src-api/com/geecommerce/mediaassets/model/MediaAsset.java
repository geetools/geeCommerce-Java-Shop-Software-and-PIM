package com.geecommerce.mediaassets.model;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.elasticsearch.api.SearchIndexSupport;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.repository.MediaAssetFiles;

public interface MediaAsset extends MultiContextModel, AttributeSupport, SearchIndexSupport {
    public Id getId();

    public MediaAsset setId(Id id);

    public Id getDirectoryId();

    public MediaAsset setDirectoryId(Id id);

/*    public String getLabel();

    public ContextObject<String> getLabels();

    public MediaAsset setLabel(ContextObject<String> label);*/

    public MediaAssetFile getFile();

    public List<MediaAssetFile> getFiles();

    public String getUrl();

    public String getUrl(int width, int height);

    public String getMimeType();

    public MediaAsset setMimeType(String mimeType);

    public MediaAsset setName(ContextObject<String> name);

    public ContextObject<String> getName();

    public MediaAsset setGroup(String group);

    public String getGroup();

    public Long getSize();

    public MediaAsset setSize(Long size);

    public MediaAsset setRawMetadata(Map<Object, Object> rawMetadata);

    public Map<Object, Object> getRawMetadata();

    public Boolean getEnabled();

    public MediaAsset setEnabled(Boolean enabled);

    public String getWebThumbnailPath();

    static final class Col {
	public static final String ID = "_id";
    public static final String DIRECTORY = "directory";
	public static final String LABEL = "label";
	public static final String FILE = "file";
	public static final String NAME = "name";
	public static final String GROUP = "group";
	public static final String MIME_TYPE = "mime_type";
	public static final String ENABLED = "enabled";
	public static final String SIZE = "size";
	public static final String RAW_METADATA = "raw_meta";
    }
}
