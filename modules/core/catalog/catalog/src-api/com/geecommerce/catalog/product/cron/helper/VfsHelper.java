package com.geecommerce.catalog.product.cron.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;

import org.apache.commons.vfs2.FileObject;

import com.geecommerce.core.service.api.Helper;

public interface VfsHelper extends Helper {
    public void archiveOldFiles(String targetDir, String archiveDir) throws FileSystemException, IOException;

    public void saveToTargetLocation(File fileToSave, String targetFile) throws FileSystemException, IOException;

    public void close(FileObject fileObject);
}
