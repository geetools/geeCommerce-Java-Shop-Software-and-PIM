package com.geecommerce.core.json.genson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.geecommerce.core.App;
import com.geecommerce.core.enums.AttributeType;
import com.geecommerce.core.enums.BackendType;
import com.geecommerce.core.enums.FilterIndexField;
import com.geecommerce.core.enums.FilterType;
import com.geecommerce.core.enums.FrontendInput;
import com.geecommerce.core.enums.FrontendOutput;
import com.geecommerce.core.enums.InputType;
import com.geecommerce.core.enums.ProductType;
import com.geecommerce.core.enums.Scope;
import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import com.owlike.genson.stream.ValueType;

@Profile
public class AttributeConverter implements Converter<Attribute> {
    private static final String KEY_ID = "id";
    private static final String KEY_ID2 = "id2";
    private static final String KEY_CODE = "code";
    private static final String KEY_CODE2 = "code2";
    private static final String KEY_TARGET_OBJECT_ID = "targetObjectId";
    private static final String KEY_TYPE = "type";
    private static final String KEY_SCOPES = "scopes";
    private static final String KEY_EDITABLE = "editable";
    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_SEARCHABLE = "searchable";
    private static final String KEY_INCLUDE_IN_SEARCH_INDEX = "includeInSearchIndex";
    private static final String KEY_SHOW_IN_PRODUCT_DETAILS = "showInProductDetails";
    private static final String KEY_FRONTEND_INPUT = "frontendInput";
    private static final String KEY_FRONTEND_OUTPUT = "frontendOutput";
    private static final String KEY_FRONTEND_LABEL = "frontendLabel";
    private static final String KEY_FRONTEND_FORMAT = "frontendFormat";
    private static final String KEY_FRONTEND_STYLE = "frontendStyle";
    private static final String KEY_FRONTEND_CLASS = "frontendClass";
    private static final String KEY_BACKEND_LABEL = "backendLabel";
    private static final String KEY_BACKEND_TYPE = "backendType";
    private static final String KEY_BACKEND_NOTE = "backendNote";
    private static final String KEY_DEFAULT_VALUE = "defaultValue";
    private static final String KEY_INPUT_TYPE = "inputType";
    private static final String KEY_IS_OPTION_ATTRIBUTE = "optionAttribute";
    private static final String KEY_IS_MULTIPLE_ALLOWED = "allowMultipleValues";
    private static final String KEY_IS_I18N = "i18n";
    private static final String KEY_OPTIONS = "options";
    private static final String KEY_LINKED_ATTRIBUTE_IDS = "linkedAttributeIds";
    private static final String KEY_PRODUCT_TYPES = "productTypes";
    private static final String KEY_DIMENSION_ATTRIBUTE = "dimensionAttribute";

    // Validation settings
    public static final String KEY_VALIDATION_MIN = "validationMin";
    public static final String KEY_VALIDATION_MAX = "validationMax";
    public static final String KEY_VALIDATION_MIN_LENGTH = "validationMinLength";
    public static final String KEY_VALIDATION_MAX_LENGTH = "validationMaxLength";
    public static final String KEY_VALIDATION_FUTURE = "validationFuture";
    public static final String KEY_VALIDATION_PAST = "validationPast";
    public static final String KEY_VALIDATION_ASSERT_TRUE = "validationAssertTrue";
    public static final String KEY_VALIDATION_ASSERT_FALSE = "validationAssertFalse";
    public static final String KEY_VALIDATION_PATTERN = "validationPattern";
    public static final String KEY_VALIDATION_SCRIPT = "validationScript";
    public static final String KEY_VALIDATION_MESSAGE = "validationMessage";

    // ProductList filter settings
    public static final String KEY_INCLUDE_IN_PRODUCT_LIST_FILTER = "includeInProductListFilter";
    public static final String KEY_PRODUCT_LIST_FILTER_TYPE = "productListFilterType";
    public static final String KEY_PRODUCT_LIST_FILTER_INDEX_FIELDS = "productListFilterIndexFields";
    public static final String KEY_PRODUCT_LIST_FILTER_KEY_ALIAS = "productListFilterKeyAlias";
    public static final String KEY_PRODUCT_LIST_FILTER_FORMAT_LABEL = "productListFilterFormatLabel";
    public static final String KEY_PRODUCT_LIST_FILTER_FORMAT_VALUE = "productListFilterFormatValue";
    public static final String KEY_PRODUCT_LIST_FILTER_PARSE_VALUE = "productListFilterParseValue";
    public static final String KEY_PRODUCT_LIST_FILTER_MULTI = "productListFilterMulti";
    public static final String KEY_PRODUCT_LIST_FILTER_INHERIT_FROM_PARENT = "productListFilterInheritFromParent";
    public static final String KEY_PRODUCT_LIST_FILTER_INCLUDE_CHILDREN = "productListFilterIncludeChildren";
    public static final String KEY_PRODUCT_LIST_FILTER_POSITION = "productListFilterPosition";

    public static final String KEY_INCLUDE_IN_PRODUCT_LIST_QUERY = "includeInProductListQuery";

    @SuppressWarnings("unchecked")
    @Override
    public Attribute deserialize(ObjectReader reader, Context ctx) throws IOException {
        Attribute attr = App.get().model(Attribute.class);

        ContextObjectConverter ctxObjConverter = new ContextObjectConverter();

        ObjectReader obj = reader.beginObject();

        while (obj.hasNext()) {
            ValueType type = obj.next();

            if (KEY_ID.equals(obj.name())) {
                attr.setId(Id.valueOf(obj.valueAsString()));
            } else if (KEY_ID2.equals(obj.name())) {
                attr.setId2(Id.valueOf(obj.valueAsString()));
            } else if (KEY_CODE.equals(obj.name())) {
                attr.setCode(obj.valueAsString());
            } else if (KEY_CODE2.equals(obj.name())) {
                attr.setCode2(obj.valueAsString());
            } else if (KEY_TARGET_OBJECT_ID.equals(obj.name())) {
                attr.setTargetObjectId(Id.valueOf(obj.valueAsString()));
            } else if (KEY_TYPE.equals(obj.name())) {
                attr.setType(AttributeType.valueOf(obj.valueAsString()));
            } else if (KEY_SCOPES.equals(obj.name())) {
                ObjectReader arr = obj.beginArray();

                List<Scope> scopes = new ArrayList<>();

                while (arr.hasNext()) {
                    ValueType vt = arr.next();

                    if (vt == ValueType.STRING)
                        scopes.add(Scope.valueOf(obj.valueAsString()));
                }

                attr.setScopes(scopes);

                obj.endArray();
            } else if (KEY_EDITABLE.equals(obj.name())) {
                attr.setEditable(obj.valueAsBoolean());
            } else if (KEY_ENABLED.equals(obj.name())) {
                attr.setEnabled(obj.valueAsBoolean());
            } else if (KEY_SEARCHABLE.equals(obj.name())) {
                attr.setSearchable(obj.valueAsBoolean());
            } else if (KEY_INCLUDE_IN_SEARCH_INDEX.equals(obj.name())) {
                attr.setIncludeInSearchIndex(obj.valueAsBoolean());
            } else if (KEY_SHOW_IN_PRODUCT_DETAILS.equals(obj.name())) {
                attr.setShowInProductDetails(obj.valueAsBoolean());
            } else if (KEY_FRONTEND_INPUT.equals(obj.name())) {
                attr.setFrontendInput(FrontendInput.valueOf(obj.valueAsString()));
            } else if (KEY_FRONTEND_OUTPUT.equals(obj.name())) {
                attr.setFrontendOutput(FrontendOutput.valueOf(obj.valueAsString()));
            } else if (KEY_FRONTEND_LABEL.equals(obj.name())) {
                attr.setFrontendLabel((ContextObject<String>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_FRONTEND_FORMAT.equals(obj.name())) {
                attr.setFrontendFormat((ContextObject<String>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_FRONTEND_STYLE.equals(obj.name())) {
                attr.setFrontendStyle(obj.valueAsString());
            } else if (KEY_FRONTEND_CLASS.equals(obj.name())) {
                attr.setFrontendClass(obj.valueAsString());
            } else if (KEY_BACKEND_LABEL.equals(obj.name())) {
                attr.setBackendLabel((ContextObject<String>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_BACKEND_TYPE.equals(obj.name())) {
                attr.setBackendType(BackendType.valueOf(obj.valueAsString()));
            } else if (KEY_BACKEND_NOTE.equals(obj.name())) {
                attr.setBackendNote((ContextObject<String>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_DEFAULT_VALUE.equals(obj.name())) {
                attr.setDefaultValue((ContextObject<String>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_VALIDATION_MIN.equals(obj.name())) {
                attr.setValidationMin((ContextObject<Double>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_VALIDATION_MAX.equals(obj.name())) {
                attr.setValidationMax((ContextObject<Double>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_VALIDATION_MIN_LENGTH.equals(obj.name())) {
                attr.setValidationMinLength((ContextObject<Integer>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_VALIDATION_MAX_LENGTH.equals(obj.name())) {
                attr.setValidationMaxLength((ContextObject<Integer>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_VALIDATION_FUTURE.equals(obj.name())) {
                attr.setValidationFuture((ContextObject<Boolean>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_VALIDATION_PAST.equals(obj.name())) {
                attr.setValidationPast((ContextObject<Boolean>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_VALIDATION_ASSERT_TRUE.equals(obj.name())) {
                attr.setValidationAssertTrue((ContextObject<Boolean>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_VALIDATION_ASSERT_FALSE.equals(obj.name())) {
                attr.setValidationAssertFalse((ContextObject<Boolean>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_VALIDATION_PATTERN.equals(obj.name())) {
                attr.setValidationPattern((ContextObject<String>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_VALIDATION_SCRIPT.equals(obj.name())) {
                attr.setValidationScript((ContextObject<String>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_VALIDATION_MESSAGE.equals(obj.name())) {
                attr.setValidationMessage((ContextObject<String>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_INPUT_TYPE.equals(obj.name())) {
                attr.setInputType(InputType.valueOf(obj.valueAsString()));
            } else if (KEY_IS_OPTION_ATTRIBUTE.equals(obj.name())) {
                attr.setOptionAttribute(obj.valueAsBoolean());
            } else if (KEY_IS_MULTIPLE_ALLOWED.equals(obj.name())) {
                attr.setAllowMultipleValues(obj.valueAsBoolean());
            } else if (KEY_DIMENSION_ATTRIBUTE.equals(obj.name())) {
                attr.setDimensionAttribute(obj.valueAsBoolean());
            } else if (KEY_IS_I18N.equals(obj.name())) {
                attr.setI18n(obj.valueAsBoolean());
            } else if (KEY_INCLUDE_IN_PRODUCT_LIST_FILTER.equals(obj.name())) {
                attr.setIncludeInProductListFilter(obj.valueAsBoolean());
            } else if (KEY_PRODUCT_LIST_FILTER_TYPE.equals(obj.name())) {
                attr.setProductListFilterType(FilterType.valueOf(obj.valueAsString()));
            } else if (KEY_PRODUCT_LIST_FILTER_INDEX_FIELDS.equals(obj.name())) {
                ObjectReader arr = obj.beginArray();

                List<FilterIndexField> indexFields = new ArrayList<>();

                while (arr.hasNext()) {
                    ValueType vt = arr.next();

                    if (vt == ValueType.STRING)
                        indexFields.add(FilterIndexField.valueOf(obj.valueAsString()));
                }

                attr.setProductListFilterIndexFields(indexFields);

                obj.endArray();
            } else if (KEY_PRODUCT_LIST_FILTER_KEY_ALIAS.equals(obj.name())) {
                attr.setProductListFilterKeyAlias((ContextObject<String>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_PRODUCT_LIST_FILTER_FORMAT_LABEL.equals(obj.name())) {
                attr.setProductListFilterFormatLabel((ContextObject<String>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_PRODUCT_LIST_FILTER_FORMAT_VALUE.equals(obj.name())) {
                attr.setProductListFilterFormatValue((ContextObject<String>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_PRODUCT_LIST_FILTER_PARSE_VALUE.equals(obj.name())) {
                attr.setProductListFilterParseValue((ContextObject<String>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_PRODUCT_LIST_FILTER_MULTI.equals(obj.name())) {
                attr.setProductListFilterMulti(obj.valueAsBoolean());
            } else if (KEY_PRODUCT_LIST_FILTER_INHERIT_FROM_PARENT.equals(obj.name())) {
                attr.setProductListFilterInheritFromParent(obj.valueAsBoolean());
            } else if (KEY_PRODUCT_LIST_FILTER_INCLUDE_CHILDREN.equals(obj.name())) {
                attr.setProductListFilterIncludeChildren(obj.valueAsBoolean());
            } else if (KEY_PRODUCT_LIST_FILTER_POSITION.equals(obj.name())) {
                attr.setProductListFilterPosition(obj.valueAsInt());
            } else if (KEY_INCLUDE_IN_PRODUCT_LIST_QUERY.equals(obj.name())) {
                attr.setIncludeInProductListQuery(obj.valueAsBoolean());
            } else if (KEY_LINKED_ATTRIBUTE_IDS.equals(obj.name())) {
                List<Id> linkedAttributeIds = new ArrayList<>();

                if (type == ValueType.ARRAY) {
                    ObjectReader array = obj.beginArray();

                    while (array.hasNext()) {
                        array.next();
                        linkedAttributeIds.add(Id.valueOf(array.valueAsString()));
                    }

                    obj.endArray();
                } else {
                    linkedAttributeIds.add(Id.valueOf(obj.valueAsString()));
                }

                if (linkedAttributeIds.size() > 0)
                    attr.setLinkedAttributeIds(linkedAttributeIds);
            } else if (KEY_PRODUCT_TYPES.equals(obj.name())) {
                Set<ProductType> productTypes = new HashSet<>();

                if (type == ValueType.ARRAY) {
                    ObjectReader array = obj.beginArray();

                    while (array.hasNext()) {
                        array.next();
                        productTypes.add(ProductType.valueOf(array.valueAsString()));
                    }

                    obj.endArray();
                } else {
                    productTypes.add(ProductType.valueOf(obj.valueAsString()));
                }

                if (productTypes.size() > 0)
                    attr.setProductTypes(productTypes);
            }
        }

        reader.endObject();

        return attr;
    }

    @Override
    public void serialize(Attribute attr, ObjectWriter writer, Context ctx) throws IOException {
        if (attr != null) {
            ContextObjectConverter ctxObjConverter = new ContextObjectConverter();
            AttributeOptionConverter attributeOptionConverter = new AttributeOptionConverter();

            writer.beginObject();

            if (attr.getId() != null && !ConvertUtil.ignoreProperty(KEY_ID)) {
                writer.writeName(KEY_ID);
                writer.writeValue(attr.getId().str());
            }

            if (attr.getId2() != null && !ConvertUtil.ignoreProperty(KEY_ID2)) {
                writer.writeName(KEY_ID2);
                writer.writeValue(attr.getId2().str());
            }

            if (attr.getCode() != null && !ConvertUtil.ignoreProperty(KEY_CODE)) {
                writer.writeName(KEY_CODE);
                writer.writeValue(attr.getCode());
            }

            if (attr.getCode2() != null && !ConvertUtil.ignoreProperty(KEY_CODE2)) {
                writer.writeName(KEY_CODE2);
                writer.writeValue(attr.getCode2());
            }

            if (attr.getTargetObjectId() != null && !ConvertUtil.ignoreProperty(KEY_TARGET_OBJECT_ID)) {
                writer.writeName(KEY_TARGET_OBJECT_ID);
                writer.writeValue(attr.getTargetObjectId().str());
            }

            if (attr.getType() != null && !ConvertUtil.ignoreProperty(KEY_TYPE)) {
                writer.writeName(KEY_TYPE);
                writer.writeValue(attr.getType().name());
            }

            if (attr.getScopes() != null && !ConvertUtil.ignoreProperty(KEY_SCOPES)) {
                writer.writeName(KEY_SCOPES);

                writer.beginArray();

                for (Scope scope : attr.getScopes()) {
                    writer.writeValue(scope.name());
                }

                writer.endArray();
            }

            if (!ConvertUtil.ignoreProperty(KEY_EDITABLE)) {
                writer.writeName(KEY_EDITABLE);
                writer.writeValue(attr.isEditable());
            }

            if (!ConvertUtil.ignoreProperty(KEY_ENABLED)) {
                writer.writeName(KEY_ENABLED);
                writer.writeValue(attr.isEnabled());
            }

            if (!ConvertUtil.ignoreProperty(KEY_SEARCHABLE)) {
                writer.writeName(KEY_SEARCHABLE);
                writer.writeValue(attr.isSearchable());
            }

            if (!ConvertUtil.ignoreProperty(KEY_INCLUDE_IN_SEARCH_INDEX)) {
                writer.writeName(KEY_INCLUDE_IN_SEARCH_INDEX);
                writer.writeValue(attr.isIncludeInSearchIndex());
            }

            if (!ConvertUtil.ignoreProperty(KEY_SHOW_IN_PRODUCT_DETAILS)) {
                writer.writeName(KEY_SHOW_IN_PRODUCT_DETAILS);
                writer.writeValue(attr.isShowInProductDetails());
            }

            if (attr.getFrontendInput() != null && !ConvertUtil.ignoreProperty(KEY_FRONTEND_INPUT)) {
                writer.writeName(KEY_FRONTEND_INPUT);
                writer.writeValue(attr.getFrontendInput().name());
            }

            if (attr.getFrontendOutput() != null && !ConvertUtil.ignoreProperty(KEY_FRONTEND_OUTPUT)) {
                writer.writeName(KEY_FRONTEND_OUTPUT);
                writer.writeValue(attr.getFrontendOutput().name());
            }

            if (attr.getFrontendLabel() != null && !ConvertUtil.ignoreProperty(KEY_FRONTEND_LABEL)) {
                writer.writeName(KEY_FRONTEND_LABEL);
                ctxObjConverter.serialize(attr.getFrontendLabel(), writer, ctx);
            }

            if (attr.getFrontendFormat() != null && !ConvertUtil.ignoreProperty(KEY_FRONTEND_FORMAT)) {
                writer.writeName(KEY_FRONTEND_FORMAT);
                ctxObjConverter.serialize(attr.getFrontendFormat(), writer, ctx);
            }

            if (attr.getFrontendStyle() != null && !ConvertUtil.ignoreProperty(KEY_FRONTEND_STYLE)) {
                writer.writeName(KEY_FRONTEND_STYLE);
                writer.writeValue(attr.getFrontendStyle());
            }

            if (attr.getFrontendClass() != null && !ConvertUtil.ignoreProperty(KEY_FRONTEND_CLASS)) {
                writer.writeName(KEY_FRONTEND_CLASS);
                writer.writeValue(attr.getFrontendClass());
            }

            if (attr.getBackendLabel() != null && !ConvertUtil.ignoreProperty(KEY_BACKEND_LABEL)) {
                writer.writeName(KEY_BACKEND_LABEL);
                ctxObjConverter.serialize(attr.getBackendLabel(), writer, ctx);
            }

            if (attr.getBackendType() != null && !ConvertUtil.ignoreProperty(KEY_BACKEND_TYPE)) {
                writer.writeName(KEY_BACKEND_TYPE);
                writer.writeValue(attr.getBackendType().name());
            }

            if (attr.getBackendNote() != null && !ConvertUtil.ignoreProperty(KEY_BACKEND_NOTE)) {
                writer.writeName(KEY_BACKEND_NOTE);
                ctxObjConverter.serialize(attr.getBackendNote(), writer, ctx);
            }

            if (attr.getDefaultValue() != null && !ConvertUtil.ignoreProperty(KEY_DEFAULT_VALUE)) {
                writer.writeName(KEY_DEFAULT_VALUE);
                ctxObjConverter.serialize(attr.getDefaultValue(), writer, ctx);
            }

            if (attr.getValidationMin() != null && !ConvertUtil.ignoreProperty(KEY_VALIDATION_MIN)) {
                writer.writeName(KEY_VALIDATION_MIN);
                ctxObjConverter.serialize(attr.getValidationMin(), writer, ctx);
            }

            if (attr.getValidationMax() != null && !ConvertUtil.ignoreProperty(KEY_VALIDATION_MAX)) {
                writer.writeName(KEY_VALIDATION_MAX);
                ctxObjConverter.serialize(attr.getValidationMax(), writer, ctx);
            }

            if (attr.getValidationMinLength() != null && !ConvertUtil.ignoreProperty(KEY_VALIDATION_MIN_LENGTH)) {
                writer.writeName(KEY_VALIDATION_MIN_LENGTH);
                ctxObjConverter.serialize(attr.getValidationMinLength(), writer, ctx);
            }

            if (attr.getValidationMaxLength() != null && !ConvertUtil.ignoreProperty(KEY_VALIDATION_MAX_LENGTH)) {
                writer.writeName(KEY_VALIDATION_MAX_LENGTH);
                ctxObjConverter.serialize(attr.getValidationMaxLength(), writer, ctx);
            }

            if (attr.getValidationFuture() != null && !ConvertUtil.ignoreProperty(KEY_VALIDATION_FUTURE)) {
                writer.writeName(KEY_VALIDATION_FUTURE);
                ctxObjConverter.serialize(attr.getValidationFuture(), writer, ctx);
            }

            if (attr.getValidationPast() != null && !ConvertUtil.ignoreProperty(KEY_VALIDATION_PAST)) {
                writer.writeName(KEY_VALIDATION_PAST);
                ctxObjConverter.serialize(attr.getValidationPast(), writer, ctx);
            }

            if (attr.getValidationAssertTrue() != null && !ConvertUtil.ignoreProperty(KEY_VALIDATION_ASSERT_TRUE)) {
                writer.writeName(KEY_VALIDATION_ASSERT_TRUE);
                ctxObjConverter.serialize(attr.getValidationAssertTrue(), writer, ctx);
            }

            if (attr.getValidationAssertFalse() != null && !ConvertUtil.ignoreProperty(KEY_VALIDATION_ASSERT_FALSE)) {
                writer.writeName(KEY_VALIDATION_ASSERT_FALSE);
                ctxObjConverter.serialize(attr.getValidationAssertFalse(), writer, ctx);
            }

            if (attr.getValidationPattern() != null && !ConvertUtil.ignoreProperty(KEY_VALIDATION_PATTERN)) {
                writer.writeName(KEY_VALIDATION_PATTERN);
                ctxObjConverter.serialize(attr.getValidationPattern(), writer, ctx);
            }

            if (attr.getValidationScript() != null && !ConvertUtil.ignoreProperty(KEY_VALIDATION_SCRIPT)) {
                writer.writeName(KEY_VALIDATION_SCRIPT);
                ctxObjConverter.serialize(attr.getValidationScript(), writer, ctx);
            }

            if (attr.getValidationMessage() != null && !ConvertUtil.ignoreProperty(KEY_VALIDATION_MESSAGE)) {
                writer.writeName(KEY_VALIDATION_MESSAGE);
                ctxObjConverter.serialize(attr.getValidationMessage(), writer, ctx);
            }

            if (attr.getInputType() != null && !ConvertUtil.ignoreProperty(KEY_INPUT_TYPE)) {
                writer.writeName(KEY_INPUT_TYPE);
                writer.writeValue(attr.getInputType().name());
            }

            if (!ConvertUtil.ignoreProperty(KEY_IS_OPTION_ATTRIBUTE)) {
                writer.writeName(KEY_IS_OPTION_ATTRIBUTE);
                writer.writeValue(attr.isOptionAttribute());
            }

            if (!ConvertUtil.ignoreProperty(KEY_IS_MULTIPLE_ALLOWED)) {
                writer.writeName(KEY_IS_MULTIPLE_ALLOWED);
                writer.writeValue(attr.isAllowMultipleValues());
            }

            if (!ConvertUtil.ignoreProperty(KEY_DIMENSION_ATTRIBUTE)) {
                writer.writeName(KEY_DIMENSION_ATTRIBUTE);
                writer.writeValue(attr.isDimensionAttribute());
            }

            if (!ConvertUtil.ignoreProperty(KEY_IS_I18N)) {
                writer.writeName(KEY_IS_I18N);
                writer.writeValue(attr.isI18n());
            }

            if (!ConvertUtil.ignoreProperty(KEY_INCLUDE_IN_PRODUCT_LIST_FILTER)) {
                writer.writeName(KEY_INCLUDE_IN_PRODUCT_LIST_FILTER);
                writer.writeValue(attr.getIncludeInProductListFilter());
            }

            if (attr.getProductListFilterType() != null && !ConvertUtil.ignoreProperty(KEY_PRODUCT_LIST_FILTER_TYPE)) {
                writer.writeName(KEY_PRODUCT_LIST_FILTER_TYPE);
                writer.writeValue(attr.getProductListFilterType().name());
            }

            List<FilterIndexField> indexFields = attr.getProductListFilterIndexFields();

            if (!ConvertUtil.ignoreProperty(KEY_PRODUCT_LIST_FILTER_INDEX_FIELDS) && indexFields != null
                && indexFields.size() > 0) {
                writer.writeName(KEY_PRODUCT_LIST_FILTER_INDEX_FIELDS);
                writer.beginArray();

                for (FilterIndexField filterIndexField : indexFields) {
                    writer.writeValue(filterIndexField.name());
                }

                writer.endArray();
            }

            if (attr.getProductListFilterKeyAlias() != null
                && !ConvertUtil.ignoreProperty(KEY_PRODUCT_LIST_FILTER_KEY_ALIAS)) {
                writer.writeName(KEY_PRODUCT_LIST_FILTER_KEY_ALIAS);
                ctxObjConverter.serialize(attr.getProductListFilterKeyAlias(), writer, ctx);
            }

            if (attr.getProductListFilterFormatLabel() != null
                && !ConvertUtil.ignoreProperty(KEY_PRODUCT_LIST_FILTER_FORMAT_LABEL)) {
                writer.writeName(KEY_PRODUCT_LIST_FILTER_FORMAT_LABEL);
                ctxObjConverter.serialize(attr.getProductListFilterFormatLabel(), writer, ctx);
            }

            if (attr.getProductListFilterFormatValue() != null
                && !ConvertUtil.ignoreProperty(KEY_PRODUCT_LIST_FILTER_FORMAT_VALUE)) {
                writer.writeName(KEY_PRODUCT_LIST_FILTER_FORMAT_VALUE);
                ctxObjConverter.serialize(attr.getProductListFilterFormatValue(), writer, ctx);
            }

            if (attr.getProductListFilterParseValue() != null
                && !ConvertUtil.ignoreProperty(KEY_PRODUCT_LIST_FILTER_PARSE_VALUE)) {
                writer.writeName(KEY_PRODUCT_LIST_FILTER_PARSE_VALUE);
                ctxObjConverter.serialize(attr.getProductListFilterParseValue(), writer, ctx);
            }

            if (!ConvertUtil.ignoreProperty(KEY_PRODUCT_LIST_FILTER_MULTI)) {
                writer.writeName(KEY_PRODUCT_LIST_FILTER_MULTI);
                writer.writeValue(attr.isProductListFilterMulti());
            }

            if (!ConvertUtil.ignoreProperty(KEY_PRODUCT_LIST_FILTER_INHERIT_FROM_PARENT)) {
                writer.writeName(KEY_PRODUCT_LIST_FILTER_INHERIT_FROM_PARENT);
                writer.writeValue(attr.isProductListFilterInheritFromParent());
            }

            if (!ConvertUtil.ignoreProperty(KEY_PRODUCT_LIST_FILTER_INCLUDE_CHILDREN)) {
                writer.writeName(KEY_PRODUCT_LIST_FILTER_INCLUDE_CHILDREN);
                writer.writeValue(attr.isProductListFilterIncludeChildren());
            }

            if (!ConvertUtil.ignoreProperty(KEY_PRODUCT_LIST_FILTER_POSITION)) {
                writer.writeName(KEY_PRODUCT_LIST_FILTER_POSITION);
                writer.writeValue(attr.getProductListFilterPosition());
            }

            if (!ConvertUtil.ignoreProperty(KEY_INCLUDE_IN_PRODUCT_LIST_QUERY)) {
                writer.writeName(KEY_INCLUDE_IN_PRODUCT_LIST_QUERY);
                writer.writeValue(attr.getIncludeInProductListQuery());
            }

            if (attr.getLinkedAttributeIds() != null && !ConvertUtil.ignoreProperty(KEY_LINKED_ATTRIBUTE_IDS)) {
                writer.writeName(KEY_LINKED_ATTRIBUTE_IDS);

                writer.beginArray();

                for (Id linkedAttrId : attr.getLinkedAttributeIds()) {
                    writer.writeValue(linkedAttrId.str());
                }

                writer.endArray();
            }

            if (attr.getProductTypes() != null && !ConvertUtil.ignoreProperty(KEY_PRODUCT_TYPES)) {
                writer.writeName(KEY_PRODUCT_TYPES);

                writer.beginArray();

                for (ProductType productType : attr.getProductTypes()) {
                    writer.writeValue(productType.name());
                }

                writer.endArray();
            }

            if (attr.getOptions() != null && !ConvertUtil.ignoreProperty(KEY_OPTIONS)) {
                writer.writeName(KEY_OPTIONS);

                writer.beginArray();

                for (AttributeOption option : attr.getOptions()) {
                    attributeOptionConverter.serialize(option, writer, ctx);
                }

                writer.endArray();
            }

            writer.endObject();
        }
    }
}
