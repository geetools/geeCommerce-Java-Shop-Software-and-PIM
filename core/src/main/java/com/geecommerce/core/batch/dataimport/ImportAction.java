package com.geecommerce.core.batch.dataimport;

public interface ImportAction {
    boolean canProcess(ImportContext importContext);

    void process(ImportContext importContext);

    int order();
}
