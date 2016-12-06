package com.geecommerce.core.batch.dataimport.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.Callable;

import com.geecommerce.core.service.api.Helper;

public interface ImportHelper extends Helper {

    String saveFile(InputStream in, String fileName) throws IOException;

    boolean isZipFile(String filePath);

    void unpack(String uploadedFilePath, boolean async, Callable<?> callable);

    Set<String> fetchHeaders(String csvPath) throws IOException;

    void createImportPlan(String uploadedFilePath);

}
