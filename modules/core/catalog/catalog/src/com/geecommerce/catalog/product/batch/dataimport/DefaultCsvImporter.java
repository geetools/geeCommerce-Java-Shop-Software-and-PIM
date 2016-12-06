package com.geecommerce.catalog.product.batch.dataimport;

import java.io.InputStream;

import com.geecommerce.core.adapter.annotation.Adapter;
import com.geecommerce.core.batch.dataimport.ImportAdapter;
import com.geecommerce.core.batch.dataimport.model.ImportProfile;

@Adapter
public class DefaultCsvImporter implements ImportAdapter {

    @Override
    public boolean canProcess(String filePath, ImportProfile importProfile) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void plan(String filePath, ImportProfile importProfile) {
        // TODO Auto-generated method stub

    }

    @Override
    public void plan(byte[] bytes, ImportProfile importProfile) {
        // TODO Auto-generated method stub

    }

    @Override
    public void plan(InputStream inputStream, ImportProfile importProfile) {
        // TODO Auto-generated method stub

    }

    @Override
    public void process(String filePath, ImportProfile importProfile) {

    }

    @Override
    public void process(byte[] bytes, ImportProfile importProfile) {

    }

    @Override
    public void process(InputStream inputStream, ImportProfile importProfile) {

    }

}
