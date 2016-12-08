package com.geecommerce.catalog.product.batch.dataimport.helper;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.geecommerce.core.service.annotation.Helper;
import com.google.inject.Singleton;

@Singleton
@Helper
public class DefaultProductImportHelper implements ProductImportHelper {
    protected List<String> validActions = Arrays.asList(new String[] { "N", "U", "D" });

    public boolean actionExists(CSVRecord row) {
        return row.isSet("_action");
    }

    public boolean isActionValid(CSVRecord row) {
        return actionExists(row) && validActions.contains(row.get("_action").trim());
    }

    public boolean isNewAction(CSVRecord row) {
        return "N".equals(row.get("_action"));
    }

    public boolean isUpdateAction(CSVRecord row) {
        return "U".equals(row.get("_action"));
    }

    public boolean isDeleteAction(CSVRecord row) {
        return "D".equals(row.get("_action"));
    }

    public boolean hasQuantity(CSVRecord row) {
        return row.isSet("_qty");
    }

    public boolean hasPrice(CSVRecord row) {
        return row.isSet("_price");
    }

    public boolean hasPriceType(CSVRecord row) {
        return row.isSet("_price_type");
    }

    public boolean variantExists(CSVRecord row) {
        return row.isSet("_variant");
    }

    public boolean isAddVariant(CSVRecord row) {
        return variantExists(row) && !isDeleteAction(row);
    }

    public boolean isRemoveVariant(CSVRecord row) {
        return variantExists(row) && isDeleteAction(row);
    }

    public boolean programmeProductExists(CSVRecord row) {
        return row.isSet("_programme_product");
    }

    public boolean isAddProgrammeItem(CSVRecord row) {
        return programmeProductExists(row) && !isDeleteAction(row);
    }

    public boolean isRemoveProgrammeItem(CSVRecord row) {
        return programmeProductExists(row) && isDeleteAction(row);
    }

    public boolean bundleProductExists(CSVRecord row) {
        return row.isSet("_bundle_product");
    }

    public boolean isAddBundleItem(CSVRecord row) {
        return bundleProductExists(row) && !isDeleteAction(row);
    }

    public boolean isRemoveBundleItem(CSVRecord row) {
        return bundleProductExists(row) && isDeleteAction(row);
    }

    public boolean upsellProductExists(CSVRecord row) {
        return row.isSet("_upsell");
    }

    public boolean isAddUpsellItem(CSVRecord row) {
        return upsellProductExists(row) && !isDeleteAction(row);
    }

    public boolean isRemoveUpsellItem(CSVRecord row) {
        return upsellProductExists(row) && isDeleteAction(row);
    }

    public boolean crossSellProductExists(CSVRecord row) {
        return row.isSet("_crosssell");
    }

    public boolean isAddCrossSellItem(CSVRecord row) {
        return crossSellProductExists(row) && !isDeleteAction(row);
    }

    public boolean isRemoveCrossSellItem(CSVRecord row) {
        return crossSellProductExists(row) && isDeleteAction(row);
    }

    public boolean containsStatus(CSVRecord row) {
        return row.isSet("status_article");
    }

    public boolean containsVisibility(CSVRecord row) {
        return row.isSet("status_article");
    }

    public boolean containsSaleable(CSVRecord row) {
        return row.isSet("status_article");
    }

}
