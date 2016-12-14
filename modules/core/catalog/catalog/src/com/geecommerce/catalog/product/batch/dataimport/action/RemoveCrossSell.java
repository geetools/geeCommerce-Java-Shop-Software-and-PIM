package com.geecommerce.catalog.product.batch.dataimport.action;

import com.geecommerce.core.batch.dataimport.ImportAction;
import com.geecommerce.core.batch.dataimport.ImportContext;

public class RemoveCrossSell implements ImportAction {

    @Override
    public boolean canProcess(ImportContext importContext) {
        return false;
    }

    @Override
    public void process(ImportContext importContext) {

    }

    @Override
    public int order() {
        return 50;
    }
}
