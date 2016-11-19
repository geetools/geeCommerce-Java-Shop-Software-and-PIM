package com.geecommerce.core.media;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.Char;
import com.geecommerce.core.Str;

public class MimeType {

    public static final String APPLICATION_PREFIX = "application/";
    public static final String TEXT_PREFIX = "text/";
    public static final String IMAGE_PREFIX = "image/";
    public static final String VIDEO_PREFIX = "video/";

    public static final String APPLICATION_WILDCARD = "application/*";
    public static final String TEXT_WILDCARD = "text/*";
    public static final String APPLICATION_PDF = "application/pdf";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_CSV = "text/csv";
    public static final String TEXT_XML = "text/xml";

    public static final String APPLICATION_MSWORD = "application/msword";
    public static final String APPLICATION_WORDPROCESSING = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String APPLICATION_EXCEL = "application/vnd.ms-excel";
    public static final String APPLICATION_SPREADSHEET = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static final String IMAGE_WILDCARD = "image/*";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_GIF = "image/gif";
    public static final String IMAGE_PNG = "image/png";
    public static final String IMAGE_TIFF = "image/tiff";
    public static final String IMAGE_BMP = "image/bmp";

    public static final String VIDEO_WILDCARD = "video/*";
    public static final String VIDEO_MP4 = "video/mp4";
    public static final String VIDEO_OGG = "video/ogg";
    public static final String VIDEO_WEBM = "video/webm";

    public static final String FILE_EXTENSION_PDF = "pdf";
    public static final String FILE_EXTENSION_TXT = "txt";
    public static final String FILE_EXTENSION_CSV = "csv";
    public static final String FILE_EXTENSION_XML = "xml";
    public static final String FILE_EXTENSION_DOC = "doc";
    public static final String FILE_EXTENSION_DOCX = "docx";
    public static final String FILE_EXTENSION_XLS = "xls";
    public static final String FILE_EXTENSION_XLSX = "xlsx";

    public static final String FILE_EXTENSION_JPG = "jpg";
    public static final String FILE_EXTENSION_JPEG = "jpeg";
    public static final String FILE_EXTENSION_GIF = "gif";
    public static final String FILE_EXTENSION_PNG = "png";
    public static final String FILE_EXTENSION_TIFF = "tiff";
    public static final String FILE_EXTENSION_BMP = "bmp";

    public static final String FILE_EXTENSION_MP4 = "mp4";
    public static final String FILE_EXTENSION_M4V = "m4v";
    public static final String FILE_EXTENSION_OGV = "ogv";
    public static final String FILE_EXTENSION_WEBM = "webm";

    public static Set<String> VALID_FILE_EXTENSIONS = new HashSet<>();
    static {
	VALID_FILE_EXTENSIONS.add(FILE_EXTENSION_PDF);
	VALID_FILE_EXTENSIONS.add(FILE_EXTENSION_TXT);
	VALID_FILE_EXTENSIONS.add(FILE_EXTENSION_CSV);
	VALID_FILE_EXTENSIONS.add(FILE_EXTENSION_XML);
	VALID_FILE_EXTENSIONS.add(FILE_EXTENSION_JPG);
	VALID_FILE_EXTENSIONS.add(FILE_EXTENSION_JPEG);
	VALID_FILE_EXTENSIONS.add(FILE_EXTENSION_GIF);
	VALID_FILE_EXTENSIONS.add(FILE_EXTENSION_PNG);
	VALID_FILE_EXTENSIONS.add(FILE_EXTENSION_TIFF);
	VALID_FILE_EXTENSIONS.add(FILE_EXTENSION_BMP);
	VALID_FILE_EXTENSIONS.add(FILE_EXTENSION_MP4);
	VALID_FILE_EXTENSIONS.add(FILE_EXTENSION_OGV);

	VALID_FILE_EXTENSIONS.add(FILE_EXTENSION_DOC);
	VALID_FILE_EXTENSIONS.add(FILE_EXTENSION_DOCX);
	VALID_FILE_EXTENSIONS.add(FILE_EXTENSION_XLS);
	VALID_FILE_EXTENSIONS.add(FILE_EXTENSION_XLSX);
    }

    public static Map<String, String> MIME_TYPE_MAPPING = new HashMap<>();
    static {
	MIME_TYPE_MAPPING.put(FILE_EXTENSION_PDF, APPLICATION_PDF);
	MIME_TYPE_MAPPING.put(FILE_EXTENSION_TXT, TEXT_PLAIN);
	MIME_TYPE_MAPPING.put(FILE_EXTENSION_CSV, TEXT_CSV);
	MIME_TYPE_MAPPING.put(FILE_EXTENSION_XML, TEXT_XML);

	MIME_TYPE_MAPPING.put(FILE_EXTENSION_JPG, IMAGE_JPEG);
	MIME_TYPE_MAPPING.put(FILE_EXTENSION_JPEG, IMAGE_JPEG);
	MIME_TYPE_MAPPING.put(FILE_EXTENSION_GIF, IMAGE_GIF);
	MIME_TYPE_MAPPING.put(FILE_EXTENSION_PNG, IMAGE_PNG);
	MIME_TYPE_MAPPING.put(FILE_EXTENSION_TIFF, IMAGE_TIFF);
	MIME_TYPE_MAPPING.put(FILE_EXTENSION_BMP, IMAGE_BMP);

	MIME_TYPE_MAPPING.put(FILE_EXTENSION_MP4, VIDEO_MP4);
	MIME_TYPE_MAPPING.put(FILE_EXTENSION_M4V, VIDEO_MP4);
	MIME_TYPE_MAPPING.put(FILE_EXTENSION_OGV, VIDEO_OGG);
	MIME_TYPE_MAPPING.put(FILE_EXTENSION_WEBM, VIDEO_WEBM);

	MIME_TYPE_MAPPING.put(FILE_EXTENSION_DOC, APPLICATION_MSWORD);
	MIME_TYPE_MAPPING.put(FILE_EXTENSION_DOCX, APPLICATION_WORDPROCESSING);
	MIME_TYPE_MAPPING.put(FILE_EXTENSION_XLS, APPLICATION_EXCEL);
	MIME_TYPE_MAPPING.put(FILE_EXTENSION_XLSX, APPLICATION_SPREADSHEET);
    }

    public static Map<String, String[]> MIME_WILDCARD_MAPPING = new HashMap<>();
    static {
	MIME_WILDCARD_MAPPING.put(APPLICATION_WILDCARD, new String[] { APPLICATION_PDF });
	MIME_WILDCARD_MAPPING.put(TEXT_WILDCARD, new String[] { TEXT_PLAIN, TEXT_CSV, TEXT_XML });
	MIME_WILDCARD_MAPPING.put(IMAGE_WILDCARD, new String[] { IMAGE_JPEG, IMAGE_GIF, IMAGE_PNG, IMAGE_TIFF, IMAGE_BMP });
	MIME_WILDCARD_MAPPING.put(VIDEO_WILDCARD, new String[] { VIDEO_MP4, VIDEO_OGG, VIDEO_WEBM });
    }

    public static Map<String, String> FILE_EXTENSION_MAPPING = new HashMap<>();
    static {
	FILE_EXTENSION_MAPPING.put(APPLICATION_PDF, FILE_EXTENSION_PDF);
	FILE_EXTENSION_MAPPING.put(TEXT_PLAIN, FILE_EXTENSION_TXT);
	FILE_EXTENSION_MAPPING.put(TEXT_CSV, FILE_EXTENSION_CSV);
	FILE_EXTENSION_MAPPING.put(TEXT_XML, FILE_EXTENSION_XML);

	FILE_EXTENSION_MAPPING.put(IMAGE_JPEG, FILE_EXTENSION_JPG);
	FILE_EXTENSION_MAPPING.put(IMAGE_GIF, FILE_EXTENSION_GIF);
	FILE_EXTENSION_MAPPING.put(IMAGE_PNG, FILE_EXTENSION_PNG);
	FILE_EXTENSION_MAPPING.put(IMAGE_TIFF, FILE_EXTENSION_TIFF);
	FILE_EXTENSION_MAPPING.put(IMAGE_BMP, FILE_EXTENSION_BMP);

	FILE_EXTENSION_MAPPING.put(VIDEO_MP4, FILE_EXTENSION_MP4);
	FILE_EXTENSION_MAPPING.put(VIDEO_OGG, FILE_EXTENSION_OGV);
	FILE_EXTENSION_MAPPING.put(VIDEO_WEBM, FILE_EXTENSION_WEBM);
    }

    public static String get(String fileExtension) {
	if (fileExtension == null)
	    return null;

	// Make sure that the file-extension-prefix is removed.
	String ext = fileExtension.replace(Str.DOT, Str.EMPTY).toLowerCase().trim();

	return MIME_TYPE_MAPPING.get(ext);
    }

    public static String fromFilename(String filename) {
	if (Str.isEmpty(filename))
	    return null;

	int index = filename.lastIndexOf(Char.DOT);
	if (index >= 0) {
	    return get(filename.substring(index));
	} else {
	    return get(filename);
	}
    }

    public static Set<String> expandWildcards(Collection<String> mimeTypes) {
	Set<String> expandedMimeTypes = new HashSet<>();

	for (String mimeType : mimeTypes) {
	    if (MIME_WILDCARD_MAPPING.containsKey(mimeType)) {
		String[] mappedMimeTypes = MIME_WILDCARD_MAPPING.get(mimeType);

		for (String mappedMimeType : mappedMimeTypes) {
		    expandedMimeTypes.add(mappedMimeType);
		}
	    } else {
		expandedMimeTypes.add(mimeType);
	    }
	}

	return expandedMimeTypes;
    }

    public static boolean isImage(String path) {
	String mimeType = fromFilename(path);
	return mimeType == null ? false : mimeType.startsWith(IMAGE_PREFIX);
    }

    public static boolean isVideo(String path) {
	String mimeType = fromFilename(path);
	return mimeType == null ? false : mimeType.startsWith(VIDEO_PREFIX);
    }

    public static boolean isText(String path) {
	String mimeType = fromFilename(path);
	return mimeType == null ? false : mimeType.startsWith(TEXT_PREFIX);
    }

    public static boolean isApplication(String path) {
	String mimeType = fromFilename(path);
	return mimeType == null ? false : mimeType.startsWith(APPLICATION_PREFIX);
    }

    public static String toWildcard(String mimeType) {
	return mimeType == null || mimeType.indexOf(Char.SLASH) == -1 ? mimeType : mimeType.substring(0, mimeType.indexOf(Char.SLASH) + 1) + Char.ASTERIX;
    }

    public static String toFileExtension(String mimeType) {
	return FILE_EXTENSION_MAPPING.get(mimeType);
    }
}
