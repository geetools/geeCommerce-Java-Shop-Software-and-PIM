package com.geecommerce.core.batch.dataimport;

import java.io.InputStream;

import com.geecommerce.core.batch.dataimport.model.ImportProfile;

public interface ImportAdapter {

    public boolean canProcess(String filePath, ImportProfile importProfile);

    public void plan(String filePath, ImportProfile importProfile);

    public void plan(byte[] bytes, ImportProfile importProfile);

    public void plan(InputStream inputStream, ImportProfile importProfile);

    public void process(String filePath, ImportProfile importProfile);

    public void process(byte[] bytes, ImportProfile importProfile);

    public void process(InputStream inputStream, ImportProfile importProfile);
}
