package com.geecommerce.core.batch.dataimport;

import java.io.InputStream;

public interface ImportAdapter {
    public void process(String filePath, ImportProfile importProfile);

    public void process(byte[] bytes, ImportProfile importProfile);

    public void process(InputStream inputStream, ImportProfile importProfile);
}
