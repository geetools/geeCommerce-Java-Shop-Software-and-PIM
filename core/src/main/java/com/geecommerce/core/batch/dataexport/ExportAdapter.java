package com.geecommerce.core.batch.dataexport;

import java.util.List;

import com.geecommerce.core.service.api.Model;

public interface ExportAdapter {
    void process(String[] data, ExportProfile exportProfile);

    void process(List<Model> models, ExportProfile exportProfile);
}
