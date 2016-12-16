package com.geecommerce.catalog.product.batch.dataimport.helper;

import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;

import com.geecommerce.core.enums.ProductType;
import com.geecommerce.core.service.api.Helper;

public interface ProductImportHelper extends Helper {

    boolean actionExists(CSVRecord row);

    boolean isActionValid(CSVRecord row);

    boolean isNewAction(CSVRecord row);

    boolean isUpdateAction(CSVRecord row);

    boolean isDeleteAction(CSVRecord row);

    boolean hasQuantity(CSVRecord row);

    boolean hasPrice(CSVRecord row);

    boolean hasPriceType(CSVRecord row);

    boolean hasPriceCurrency(CSVRecord row);

    boolean hasMedia(CSVRecord row);

    boolean hasMediaType(CSVRecord row);

    boolean mainProductIdentificationExists(CSVRecord row);

    boolean variantExists(CSVRecord row);

    boolean isAddVariant(CSVRecord row);

    boolean isRemoveVariant(CSVRecord row);

    boolean programmeProductExists(CSVRecord row);

    boolean isAddProgrammeItem(CSVRecord row);

    boolean isRemoveProgrammeItem(CSVRecord row);

    boolean bundleProductExists(CSVRecord row);

    boolean isAddBundleItem(CSVRecord row);

    boolean isRemoveBundleItem(CSVRecord row);

    boolean upsellProductExists(CSVRecord row);

    boolean isAddUpsellItem(CSVRecord row);

    boolean isRemoveUpsellItem(CSVRecord row);

    boolean crossSellProductExists(CSVRecord row);

    boolean isAddCrossSellItem(CSVRecord row);

    boolean isRemoveCrossSellItem(CSVRecord row);

    boolean containsStatus(CSVRecord row);

    boolean containsVisible(CSVRecord row);

    boolean containsVisibleInProductList(CSVRecord row);

    boolean containsVisibility(CSVRecord row);

    boolean containsSaleable(CSVRecord row);

    boolean containsUpdatableIdFields(CSVRecord row);

    boolean containsAttributes(CSVRecord row);

    boolean containsUpdatableFields(CSVRecord row);

    boolean containsArticleNumber(CSVRecord row);

    boolean isValid(CSVRecord row);

    boolean invalidFieldsArePopulated(CSVRecord row, List<String> validFields);

    String errorMessage(CSVRecord csvRecord);

    boolean validProductTypeExists(CSVRecord row);

    ProductType productType(CSVRecord row);

    boolean containsProductGroup(CSVRecord row);

    boolean containsMinimalFieldsForNewProduct(CSVRecord row);

    boolean isEmpty(CSVRecord row);

    Map<String, Object> productIds(CSVRecord row, String fieldName);

    Map<String, Object> productIds(Map<String, String> row, String fieldName);

    String newProductKey(CSVRecord row);

    String productKeys(CSVRecord row);

    String productKeys(Map<String, String> row);
}
