package com.geecommerce.catalog.product.batch.dataimport.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVRecord;

import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.enums.ProductType;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.type.Id;
import com.google.inject.Singleton;

@Singleton
@Helper
public class DefaultProductImportHelper implements ProductImportHelper {
    protected List<String> validActions = Arrays.asList(new String[] { "N", "U", "D" });
    protected List<String> validVariantFields = Arrays.asList(new String[] { "_action", "_id", "_id2", "_ean", "article_number", "_variant" });
    protected List<String> validProgrammeFields = Arrays.asList(new String[] { "_action", "_id", "_id2", "_ean", "article_number", "_programme_product" });
    protected List<String> validBundleFields = Arrays.asList(new String[] { "_action", "_id", "_id2", "_ean", "article_number", "_bundle_product" });
    protected List<String> validUpsellFields = Arrays.asList(new String[] { "_action", "_id", "_id2", "_ean", "article_number", "_upsell" });
    protected List<String> validCrossSellFields = Arrays.asList(new String[] { "_action", "_id", "_id2", "_ean", "article_number", "_crosssell" });
    protected Pattern isNumberRegex = Pattern.compile("^[0-1]+$");

    @Override
    public boolean actionExists(CSVRecord row) {
        return row.isSet("_action") && !Str.isEmpty(row.get("_action"));
    }

    @Override
    public boolean isActionValid(CSVRecord row) {
        return actionExists(row) && validActions.contains(row.get("_action").trim());
    }

    @Override
    public boolean isNewAction(CSVRecord row) {
        return actionExists(row) && "N".equals(row.get("_action"));
    }

    @Override
    public boolean isUpdateAction(CSVRecord row) {
        return actionExists(row) && "U".equals(row.get("_action"));
    }

    @Override
    public boolean isDeleteAction(CSVRecord row) {
        return actionExists(row) && "D".equals(row.get("_action"));
    }

    @Override
    public boolean hasQuantity(CSVRecord row) {
        return row.isSet("_qty") && !Str.isEmpty(row.get("_qty"));
    }

    @Override
    public boolean hasPrice(CSVRecord row) {
        return row.isSet("_price") && !Str.isEmpty(row.get("_price"));
    }

    @Override
    public boolean hasPriceType(CSVRecord row) {
        return row.isSet("_price_type") && !Str.isEmpty(row.get("_price_type"));
    }

    @Override
    public boolean hasPriceCurrency(CSVRecord row) {
        return row.isSet("_price_currency") && !Str.isEmpty(row.get("_price_currency"));
    }

    @Override
    public boolean hasMedia(CSVRecord row) {

        System.out.println("row.isSet(\"_media\"): " + row.isSet("_media") + " - " + (!row.isSet("_media") ? null : row.get("_media")));

        return row.isSet("_media") && !Str.isEmpty(row.get("_media"));
    }

    @Override
    public boolean hasMediaType(CSVRecord row) {
        System.out.println("row.isSet(\"_media_type\"): " + row.isSet("_media_type") + " - " + (!row.isSet("_media_type") ? null : row.get("_media_type")));

        return row.isSet("_media_type") && !Str.isEmpty(row.get("_media_type"));
    }

    @Override
    public boolean mainProductIdentificationExists(CSVRecord row) {
        System.out.println("mainProductIdentificationExists: " + (row.isSet("_id") || row.isSet("_id2") || row.isSet("_ean") || row.isSet("article_number")));

        return row.isSet("_id") || row.isSet("_id2") || row.isSet("_ean") || row.isSet("article_number");
    }

    @Override
    public boolean variantExists(CSVRecord row) {
        return row.isSet("_variant") && !Str.isEmpty(row.get("_variant"));
    }

    @Override
    public boolean isAddVariant(CSVRecord row) {
        return variantExists(row) && !isDeleteAction(row);
    }

    @Override
    public boolean isRemoveVariant(CSVRecord row) {
        return variantExists(row) && isDeleteAction(row);
    }

    @Override
    public boolean programmeProductExists(CSVRecord row) {
        return row.isSet("_programme_product") && !Str.isEmpty(row.get("_programme_product"));
    }

    @Override
    public boolean isAddProgrammeItem(CSVRecord row) {
        return programmeProductExists(row) && !isDeleteAction(row);
    }

    @Override
    public boolean isRemoveProgrammeItem(CSVRecord row) {
        return programmeProductExists(row) && isDeleteAction(row);
    }

    @Override
    public boolean bundleProductExists(CSVRecord row) {
        return row.isSet("_bundle_product") && !Str.isEmpty(row.get("_bundle_product"));
    }

    @Override
    public boolean isAddBundleItem(CSVRecord row) {
        return bundleProductExists(row) && !isDeleteAction(row);
    }

    @Override
    public boolean isRemoveBundleItem(CSVRecord row) {
        return bundleProductExists(row) && isDeleteAction(row);
    }

    @Override
    public boolean upsellProductExists(CSVRecord row) {
        return row.isSet("_upsell") && !Str.isEmpty(row.get("_upsell"));
    }

    @Override
    public boolean isAddUpsellItem(CSVRecord row) {
        return upsellProductExists(row) && !isDeleteAction(row);
    }

    @Override
    public boolean isRemoveUpsellItem(CSVRecord row) {
        return upsellProductExists(row) && isDeleteAction(row);
    }

    @Override
    public boolean crossSellProductExists(CSVRecord row) {
        return row.isSet("_crosssell") && !Str.isEmpty(row.get("_crosssell"));
    }

    @Override
    public boolean isAddCrossSellItem(CSVRecord row) {
        return crossSellProductExists(row) && !isDeleteAction(row);
    }

    @Override
    public boolean isRemoveCrossSellItem(CSVRecord row) {
        return crossSellProductExists(row) && isDeleteAction(row);
    }

    @Override
    public boolean containsStatus(CSVRecord row) {
        return row.isSet("status_article") && !Str.isEmpty(row.get("status_article"));
    }

    @Override
    public boolean containsVisible(CSVRecord row) {
        return row.isSet("_visible") && !Str.isEmpty(row.get("_visible"));
    }

    @Override
    public boolean containsVisibleInProductList(CSVRecord row) {
        return row.isSet("_visible_in_product_list") && !Str.isEmpty(row.get("_visible_in_product_list"));
    }

    @Override
    public boolean containsVisibility(CSVRecord row) {
        return (row.isSet("_visible") && !Str.isEmpty(row.get("_visible")))
            || (row.isSet("_visible_from") && !Str.isEmpty(row.get("_visible_from")))
            || (row.isSet("_visible_to") && !Str.isEmpty(row.get("_visible_to")))
            || (row.isSet("_visible_in_product_list") && !Str.isEmpty(row.get("_visible_in_product_list")));
    }

    @Override
    public boolean containsSaleable(CSVRecord row) {
        return row.isSet("_saleable") && !Str.isEmpty(row.get("_saleable"));
    }

    @Override
    public boolean containsUpdatableIdFields(CSVRecord row) {
        return (row.isSet("_id2") && !Str.isEmpty(row.get("_id2")))
            || (row.isSet("_ean") && !Str.isEmpty(row.get("_ean")))
            || (row.isSet("article_number") && !Str.isEmpty(row.get("article_number")));
    }

    @Override
    public boolean containsArticleNumber(CSVRecord row) {
        return row.isSet("article_number") && !Str.isEmpty(row.get("article_number"));
    }

    @Override
    public boolean containsAttributes(CSVRecord row) {
        Map<String, String> fields = row.toMap();

        for (Map.Entry<String, String> field : fields.entrySet()) {
            if (!field.getKey().trim().startsWith(Str.UNDERSCORE)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsUpdatableFields(CSVRecord row) {
        return containsAttributes(row)
            || containsSaleable(row)
            || containsStatus(row)
            || containsVisibility(row)
            || containsUpdatableIdFields(row);
    }

    @Override
    public boolean isValid(CSVRecord row) {
        if (variantExists(row) && invalidFieldsArePopulated(row, validVariantFields) && !mainProductIdentificationExists(row))
            return false;

        if (programmeProductExists(row) && invalidFieldsArePopulated(row, validProgrammeFields) && !mainProductIdentificationExists(row))
            return false;

        if (bundleProductExists(row) && invalidFieldsArePopulated(row, validBundleFields) && !mainProductIdentificationExists(row))
            return false;

        if (upsellProductExists(row) && invalidFieldsArePopulated(row, validUpsellFields) && !mainProductIdentificationExists(row))
            return false;

        if (crossSellProductExists(row) && invalidFieldsArePopulated(row, validCrossSellFields) && !mainProductIdentificationExists(row))
            return false;

        if (hasPrice(row) && (!hasPriceType(row) || !hasPriceCurrency(row) || !mainProductIdentificationExists(row)))
            return false;

        if (hasQuantity(row) && !mainProductIdentificationExists(row))
            return false;

        if (hasMedia(row) && (!hasMediaType(row) || !mainProductIdentificationExists(row)))
            return false;

        if (isUpdateAction(row) && !mainProductIdentificationExists(row))
            return false;

        if (isDeleteAction(row) && !mainProductIdentificationExists(row))
            return false;

        return true;
    }

    @Override
    public boolean invalidFieldsArePopulated(CSVRecord row, List<String> validFields) {
        Map<String, String> fields = row.toMap();

        for (Map.Entry<String, String> field : fields.entrySet()) {
            if (!validFields.contains(field.getKey().trim())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String errorMessage(CSVRecord row) {
        if (variantExists(row) && invalidFieldsArePopulated(row, validVariantFields) && !mainProductIdentificationExists(row))
            return "importInvalidVariantRow";

        if (programmeProductExists(row) && invalidFieldsArePopulated(row, validProgrammeFields) && !mainProductIdentificationExists(row))
            return "importInvalidProgrammeProductRow";

        if (bundleProductExists(row) && invalidFieldsArePopulated(row, validBundleFields) && !mainProductIdentificationExists(row))
            return "importInvalidBundleProductRow";

        if (upsellProductExists(row) && invalidFieldsArePopulated(row, validUpsellFields) && !mainProductIdentificationExists(row))
            return "importInvalidUpsellProductRow";

        if (crossSellProductExists(row) && invalidFieldsArePopulated(row, validCrossSellFields) && !mainProductIdentificationExists(row))
            return "importInvalidCrossSellProductRow";

        if (hasPrice(row) && (!hasPriceType(row) || !hasPriceCurrency(row) || !mainProductIdentificationExists(row)))
            return "importInvalidPriceRow";

        if (hasQuantity(row) && !mainProductIdentificationExists(row))
            return "importInvalidQtyRow";

        if (hasMedia(row) && (!hasMediaType(row) || !mainProductIdentificationExists(row)))
            return "importInvalidMediaRow";

        if (isUpdateAction(row) && !mainProductIdentificationExists(row))
            return "importUpdateActionWithoutIdRow";

        if (isDeleteAction(row) && !mainProductIdentificationExists(row))
            return "importDeleteActionWithoutIdRow";

        return "invalidRow";
    }

    @Override
    public boolean validProductTypeExists(CSVRecord row) {
        if (!row.isSet("_type") || Str.isEmpty(row.get("_type")))
            return false;

        String csvType = row.get("_type").trim().toUpperCase();
        ProductType[] productTypes = ProductType.values();

        boolean isNumber = false;
        Matcher m = isNumberRegex.matcher(csvType);

        if (m.matches()) {
            isNumber = true;
        }

        for (ProductType productType : productTypes) {
            if (isNumber) {
                if (Integer.parseInt(csvType) == productType.toId())
                    return true;
            } else {
                if (csvType.equals(productType.name()))
                    return true;
            }
        }

        return false;
    }

    @Override
    public ProductType productType(CSVRecord row) {
        if (!row.isSet("_type") || Str.isEmpty(row.get("_type")))
            return null;

        String csvType = row.get("_type").trim().toUpperCase();

        boolean isNumber = false;
        Matcher m = isNumberRegex.matcher(csvType);

        if (m.matches()) {
            isNumber = true;
        }

        if (isNumber) {
            return ProductType.fromId(Integer.parseInt(csvType));
        } else {
            return ProductType.valueOf(csvType);
        }
    }

    @Override
    public boolean containsProductGroup(CSVRecord row) {
        ProductType productType = productType(row);

        if (productType == null)
            return false;

        switch (productType) {
        case PRODUCT:
        case VARIANT_MASTER:
            return row.isSet("product_group") && !Str.isEmpty(row.get("product_group"));
        case PROGRAMME:
            return row.isSet("programme") && !Str.isEmpty(row.get("programme"));
        case BUNDLE:
            return row.isSet("bundle_group") && !Str.isEmpty(row.get("bundle_group"));
        }

        return false;
    }

    @Override
    public boolean containsMinimalFieldsForNewProduct(CSVRecord row) {
        return validProductTypeExists(row)
            && containsArticleNumber(row)
            && containsProductGroup(row);
    }

    @Override
    public boolean isEmpty(CSVRecord row) {
        Iterator<String> it = row.iterator();
        while (it.hasNext()) {
            String val = it.next();

            if (!Str.isEmpty(val))
                return false;
        }

        return true;
    }

    @Override
    public Map<String, Object> productIds(CSVRecord row, String fieldName) {
        return productIds(row.toMap(), fieldName);
    }

    @Override
    public Map<String, Object> productIds(Map<String, String> row, String fieldName) {
        if (fieldName == null)
            return null;

        String value = row.get(fieldName);

        if (value == null)
            return null;

        value = value.trim();

        Map<String, Object> idMap = new HashMap<>();

        if (value.startsWith("id2:")) {
            idMap.put("id2", value.replace("id2:", Str.EMPTY).trim());
            return idMap;
        }

        if (value.startsWith("ean:")) {
            String ean = value.replace("ean:", Str.EMPTY).trim();

            Matcher m = isNumberRegex.matcher(ean);

            if (m.matches()) {
                idMap.put("ean", Long.valueOf(ean));
            }

            return idMap;
        }

        Matcher m = isNumberRegex.matcher(value);

        if (m.matches()) {
            idMap.put("_id", Id.valueOf(value.trim()));
        }

        idMap.put("article_number", value.trim());

        return idMap;
    }

    @Override
    public String newProductKey(CSVRecord row) {
        StringBuilder key = new StringBuilder();

        if (row.isSet("_id2") && !Str.isEmpty(row.get("_id2")))
            key.append(row.get("_id2")).append(Char.UNDERSCORE);

        if (row.isSet("article_number") && !Str.isEmpty(row.get("article_number")))
            key.append(row.get("article_number"));

        if (row.isSet("ean") && !Str.isEmpty(row.get("ean")))
            key.append(Char.UNDERSCORE).append(row.get("ean"));

        return key.toString();
    }

    @Override
    public String productKeys(CSVRecord row) {
        StringBuilder key = new StringBuilder();

        if (row.isSet("_id2") && !Str.isEmpty(row.get("_id2")))
            key.append(row.get("_id2")).append(Char.SLASH);

        if (row.isSet("article_number") && !Str.isEmpty(row.get("article_number")))
            key.append(row.get("article_number"));

        if (row.isSet("ean") && !Str.isEmpty(row.get("ean")))
            key.append(Char.SLASH).append(row.get("ean"));

        return key.toString();
    }

    @Override
    public String productKeys(Map<String, String> row) {
        StringBuilder key = new StringBuilder();

        if (row.containsKey("_id2") && !Str.isEmpty(row.get("_id2")))
            key.append(row.get("_id2")).append(Char.SLASH);

        if (row.containsKey("article_number") && !Str.isEmpty(row.get("article_number")))
            key.append(row.get("article_number"));

        if (row.containsKey("ean") && !Str.isEmpty(row.get("ean")))
            key.append(Char.SLASH).append(row.get("ean"));

        return key.toString();
    }

}
