package com.geecommerce.catalog.product.batch.dataimport.action;

import org.boon.Str;

import com.geecommerce.core.batch.dataimport.ImportAction;
import com.geecommerce.core.batch.dataimport.ImportContext;

public class UpdatePrice implements ImportAction {

    @Override
    public boolean canProcess(ImportContext importContext) {
        return !Str.isEmpty(importContext.data().get("_price")) && !Str.isEmpty(importContext.data().get("_price_type"));
    }

    @Override
    public void process(ImportContext importContext) {

    }

    @Override
    public int order() {
        return 80;
    }
}
