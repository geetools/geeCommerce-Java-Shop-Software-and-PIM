package com.geecommerce.catalog.product.batch.dataimport.action;

import org.boon.Str;

import com.geecommerce.core.batch.dataimport.ImportAction;
import com.geecommerce.core.batch.dataimport.ImportContext;

public class UpdateMedia implements ImportAction {

    @Override
    public boolean canProcess(ImportContext importContext) {
        return !Str.isEmpty(importContext.data().get("_media")) && !Str.isEmpty(importContext.data().get("_media_type"));
    }

    @Override
    public void process(ImportContext importContext) {

    }

    @Override
    public int order() {
        return 90;
    }
}
