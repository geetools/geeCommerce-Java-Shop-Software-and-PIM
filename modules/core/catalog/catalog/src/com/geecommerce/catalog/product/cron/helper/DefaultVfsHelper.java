package com.geecommerce.catalog.product.cron.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.geecommerce.catalog.product.cron.ExportProductData;
import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Helper;
import com.google.inject.Inject;

@Helper
public class DefaultVfsHelper implements VfsHelper {
    @Inject
    protected App app;

    protected static final String KEY_VFS_TARGET_PROVIDER = "catalog/cron/product_export/vfs/target_provider";
    protected static final String KEY_VFS_TARGET_HOST = "catalog/cron/product_export/vfs/target_host";
    protected static final String KEY_VFS_TARGET_PORT = "catalog/cron/product_export/vfs/target_port";
    protected static final String KEY_VFS_TARGET_USER = "catalog/cron/product_export/vfs/target_user";
    protected static final String KEY_VFS_TARGET_PASS = "catalog/cron/product_export/vfs/target_pass";

    protected static final Logger log = LogManager.getLogger(ExportProductData.class);

    @Override
    public void archiveOldFiles(String targetDir, String archiveDir) throws FileSystemException, IOException {
        FileSystemManager fsManager = fileSystemManager();

        log.info("Archiving old files: [targetDir=" + targetDir + ", archiveDir=" + archiveDir + "].");

        if (archiveDir != null && !"".equals(archiveDir.trim())) {
            FileObject targetFO = fsManager.resolveFile(getConnectionString(targetDir));
            FileObject archiveFO = fsManager.resolveFile(getConnectionString(archiveDir));

            archiveFO.createFolder();

            FileSelector csvSelector = new FileSelector() {
                public boolean includeFile(FileSelectInfo fileInfo) throws Exception {
                    if (fileInfo.getDepth() != 1)
                        return false;

                    FileObject fo = fileInfo.getFile();
                    String ext = fo.getName().getExtension();

                    return ext != null && ext.equalsIgnoreCase("csv");
                }

                public boolean traverseDescendents(FileSelectInfo fileInfo) throws Exception {
                    return true;
                }
            };

            FileObject[] csvFiles = targetFO.findFiles(csvSelector);

            if (csvFiles != null && csvFiles.length > 0) {
                for (FileObject csvFile : csvFiles) {
                    FileObject destFO = fsManager.resolveFile(archiveFO, csvFile.getName().getBaseName());

                    log.info("Moving file'" + csvFile.getURL() + "' to '" + destFO.getURL() + "'.");

                    csvFile.moveTo(destFO);
                }
            }
        }
    }

    @Override
    public void saveToTargetLocation(File tmpFile, String targetDir) throws FileSystemException, IOException {
        if (tmpFile == null)
            throw new NullPointerException("tmpFile cannot be null");

        if (!tmpFile.exists())
            throw new FileNotFoundException("Unable to save file '" + tmpFile.getAbsolutePath() + "' to target location as it does not exist");

        FileObject tmpFO = null;
        FileObject targetDirFO = null;
        FileObject targetFileFO = null;

        try {
            FileSystemManager fsManager = fileSystemManager();

            // Tmp file.
            tmpFO = fsManager.resolveFile(tmpFile.getAbsolutePath());

            // Where to copy tmp file to.
            targetDirFO = get(targetDir);
            targetFileFO = get(new File(targetDir, tmpFile.getName()).getAbsolutePath());

            // Attempt to create directories if they do not exist.
            if (!targetDirFO.exists())
                targetDirFO.createFolder();

            if (targetDirFO.exists()) {
                log.info("Saving file'" + tmpFO.getURL() + "' to '" + targetFileFO.getURL() + "'.");

                targetFileFO.copyFrom(tmpFO, Selectors.SELECT_SELF);
            } else {
                throw new FileSystemException("Attempt to create target dir '" + targetDirFO.getURL().toString() + "' failed");
            }

            if (targetDirFO == null || !targetDirFO.exists())
                throw new FileSystemException("Tmp file '" + tmpFO.getURL() + "' could not be copied to target locaton '" + targetFileFO.getURL() + "'");
        } finally {
            close(tmpFO);
            close(targetDirFO);
            close(targetFileFO);
        }
    }

    protected FileObject get(String filePath) throws FileSystemException {
        FileSystemManager fsManager = fileSystemManager();

        String conn = getConnectionString(filePath);

        StringBuilder connFilePath = new StringBuilder(conn);

        if (!conn.endsWith("/") && !filePath.trim().startsWith("/"))
            connFilePath.append("/");

        // connFilePath.append(filePath);

        return fsManager.resolveFile(connFilePath.toString());
    }

    protected String getConnectionString(String targetDir) {
        String provider = app.cpStr_(KEY_VFS_TARGET_PROVIDER);
        String host = app.cpStr_(KEY_VFS_TARGET_HOST);
        String port = app.cpStr_(KEY_VFS_TARGET_PORT);
        String user = app.cpStr_(KEY_VFS_TARGET_USER);
        String pass = app.cpStr_(KEY_VFS_TARGET_PASS);

        StringBuilder vfsUrl = new StringBuilder(provider.trim()).append("://");

        if (user != null)
            vfsUrl.append(user.trim());

        if (pass != null)
            vfsUrl.append(":").append(pass.trim());

        if (user != null)
            vfsUrl.append("@");

        if (host != null)
            vfsUrl.append(host.trim());

        if (port != null)
            vfsUrl.append(":").append(port.trim());

        if (targetDir != null) {
            if (!targetDir.trim().startsWith("/"))
                vfsUrl.append("/");

            vfsUrl.append(targetDir.trim());
        }

        System.out.println(vfsUrl.toString());

        return vfsUrl.toString();
    }

    @Override
    public void close(FileObject fileObject) {
        try {
            fileObject.close();
        } catch (FileSystemException e) {
            e.printStackTrace();
        }
    }

    protected FileSystemManager fileSystemManager() throws FileSystemException {
        StandardFileSystemManager fsManager = new StandardFileSystemManager();
        fsManager.setClassLoader(DefaultVfsHelper.class.getClassLoader());
        fsManager.init();

        return fsManager;
    }
}
