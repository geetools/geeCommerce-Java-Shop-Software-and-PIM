package com.geecommerce.core.batch.dataimport;

import java.io.InputStream;
import java.util.List;

import com.geecommerce.core.batch.dataimport.model.ImportMessage;
import com.geecommerce.core.batch.dataimport.model.ImportPlan;
import com.geecommerce.core.batch.dataimport.model.ImportProfile;

public interface ImportAdapter {

    public boolean canProcess(String csvPath, ImportProfile importProfile);

    public ImportPlan plan(String csvPath, ImportProfile importProfile, List<ImportMessage> importMessages);

    public ImportPlan plan(byte[] bytes, ImportProfile importProfile);

    public ImportPlan plan(InputStream inputStream, ImportProfile importProfile);

    public void process(String csvPath, ImportProfile importProfile);

    public void process(byte[] bytes, ImportProfile importProfile);

    public void process(InputStream inputStream, ImportProfile importProfile);
}
