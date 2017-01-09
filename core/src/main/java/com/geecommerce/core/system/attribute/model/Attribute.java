package com.geecommerce.core.system.attribute.model;

import java.util.List;
import java.util.Set;

import com.geecommerce.core.enums.AttributeType;
import com.geecommerce.core.enums.BackendType;
import com.geecommerce.core.enums.FilterIndexField;
import com.geecommerce.core.enums.FilterType;
import com.geecommerce.core.enums.FrontendInput;
import com.geecommerce.core.enums.FrontendOutput;
import com.geecommerce.core.enums.InputType;
import com.geecommerce.core.enums.ProductType;
import com.geecommerce.core.enums.Scope;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.owlike.genson.annotation.JsonIgnore;

public interface Attribute extends MultiContextModel {
    public Id getId();

    public Attribute setId(Id id);

    public Id getId2();

    public Attribute setId2(Id id2);

    public String getCode();

    public Attribute setCode(String code);

    public String getCode2();

    public Attribute setCode2(String code2);

    public Id getTargetObjectId();

    public Attribute setTargetObjectId(Id targetObjectId);

    public AttributeTargetObject getTargetObject();

    public AttributeType getType();

    public Attribute setType(AttributeType type);

    public List<Scope> getScopes();

    public Attribute setScopes(List<Scope> scopes);

    public Attribute addScope(Scope scope);

    public boolean hasScope(Scope scope);

    // -------------------------------------------------------------------
    // Admin panel settings
    // -------------------------------------------------------------------

    public FrontendInput getFrontendInput();

    public Attribute setFrontendInput(FrontendInput frontendInput);

    public FrontendOutput getFrontendOutput();

    public Attribute setFrontendOutput(FrontendOutput frontendOutput);

    public ContextObject<String> getFrontendLabel();

    public Attribute setFrontendLabel(ContextObject<String> frontendLabel);

    public ContextObject<String> getFrontendFormat();

    public Attribute setFrontendFormat(ContextObject<String> frontendFormat);

    public String getFrontendStyle();

    public Attribute setFrontendStyle(String frontendStyle);

    public String getFrontendClass();

    public Attribute setFrontendClass(String frontendClass);

    public ContextObject<String> getBackendLabel();

    public Attribute setBackendLabel(ContextObject<String> backendLabel);

    public BackendType getBackendType();

    public Attribute setBackendType(BackendType backendType);

    public ContextObject<String> getBackendNote();

    public Attribute setBackendNote(ContextObject<String> backendNote);

    public ContextObject<?> getDefaultValue();

    public Attribute setDefaultValue(ContextObject<?> defaultValue);

    public boolean isOptionAttribute();

    public Attribute setOptionAttribute(boolean isOptionAttribute);

    public boolean isAllowMultipleValues();

    public Attribute setAllowMultipleValues(boolean allowMultipleValues);

    public boolean isI18n();

    public Attribute setI18n(boolean i18n);

    // -------------------------------------------------------------------
    // Validation settings
    // -------------------------------------------------------------------

    public ContextObject<Double> getValidationMin();

    public Attribute setValidationMin(ContextObject<Double> validationMin);

    public ContextObject<Double> getValidationMax();

    public Attribute setValidationMax(ContextObject<Double> validationMax);

    public ContextObject<Integer> getValidationMinLength();

    public Attribute setValidationMinLength(ContextObject<Integer> validationMinLength);

    public ContextObject<Integer> getValidationMaxLength();

    public Attribute setValidationMaxLength(ContextObject<Integer> validationMaxlength);

    public ContextObject<Boolean> getValidationFuture();

    public Attribute setValidationFuture(ContextObject<Boolean> validationFuture);

    public ContextObject<Boolean> getValidationPast();

    public Attribute setValidationPast(ContextObject<Boolean> validationPast);

    public ContextObject<Boolean> getValidationAssertTrue();

    public Attribute setValidationAssertTrue(ContextObject<Boolean> validationAssertTrue);

    public ContextObject<Boolean> getValidationAssertFalse();

    public Attribute setValidationAssertFalse(ContextObject<Boolean> validationAssertFalse);

    public ContextObject<String> getValidationPattern();

    public Attribute setValidationPattern(ContextObject<String> validationPattern);

    public ContextObject<String> getValidationScript();

    public Attribute setValidationScript(ContextObject<String> validationScript);

    public ContextObject<String> getValidationMessage();

    public Attribute setValidationMessage(ContextObject<String> validationMessage);

    // -------------------------------------------------------------------
    // Product settings
    // -------------------------------------------------------------------

    public Set<ProductType> getProductTypes();

    public Attribute setProductTypes(Set<ProductType> productTypes);

    public Attribute addProductTypes(ProductType... productTypes);

    // -------------------------------------------------------------------
    // Import settings
    // -------------------------------------------------------------------

    public boolean isAllowNewOptionsViaImport();

    public Attribute setAllowNewOptionsViaImport(boolean allowNewOptionsViaImport);

    // -------------------------------------------------------------------
    // ProductList filter settings
    // -------------------------------------------------------------------

    public Boolean getIncludeInProductListFilter();

    public Attribute setIncludeInProductListFilter(Boolean includeInProductListFilter);

    public FilterType getProductListFilterType();

    public Attribute setProductListFilterType(FilterType productListFilterType);

    public List<FilterIndexField> getProductListFilterIndexFields();

    public Attribute setProductListFilterIndexFields(List<FilterIndexField> productListFilterIndexFields);

    public ContextObject<String> getProductListFilterKeyAlias();

    public Attribute setProductListFilterKeyAlias(ContextObject<String> productListFilterKeyAlias);

    public ContextObject<String> getProductListFilterFormatLabel();

    public Attribute setProductListFilterFormatLabel(ContextObject<String> productListFilterFormatLabel);

    public ContextObject<String> getProductListFilterFormatValue();

    public Attribute setProductListFilterFormatValue(ContextObject<String> productListFilterFormatValue);

    public ContextObject<String> getProductListFilterParseValue();

    public Attribute setProductListFilterParseValue(ContextObject<String> productListFilterParseValue);

    public boolean isProductListFilterMulti();

    public Attribute setProductListFilterMulti(boolean productListFilterMulti);

    public boolean isProductListFilterInheritFromParent();

    public Attribute setProductListFilterInheritFromParent(boolean productListFilterInheritFromParent);

    public boolean isProductListFilterIncludeChildren();

    public Attribute setProductListFilterIncludeChildren(boolean productListFilterIncludeChildren);

    public int getProductListFilterPosition();

    public Attribute setProductListFilterPosition(int productListFilterPosition);

    // -------------------------------------------------------------------
    // ProductList query settings
    // -------------------------------------------------------------------

    public Boolean getShowInQuery();

    public Attribute setShowInQuery(Boolean showInQuery);

    // -------------------------------------------------------------------
    // Search filter settings
    // -------------------------------------------------------------------

    public Boolean getIncludeInSearchFilter();

    public Attribute setIncludeInSearchFilter(Boolean includeInSearchFilter);

    public FilterType getSearchFilterType();

    public Attribute setSearchFilterType(FilterType searchFilterType);

    public List<FilterIndexField> getSearchFilterIndexFields();

    public Attribute setSearchFilterIndexFields(List<FilterIndexField> searchFilterIndexFields);

    public ContextObject<String> getSearchFilterKeyAlias();

    public Attribute setSearchFilterKeyAlias(ContextObject<String> searchFilterKeyAlias);

    public ContextObject<String> getSearchFilterFormatLabel();

    public Attribute setSearchFilterFormatLabel(ContextObject<String> searchFilterFormatLabel);

    public ContextObject<String> getSearchFilterFormatValue();

    public Attribute setSearchFilterFormatValue(ContextObject<String> searchFilterFormatValue);

    public ContextObject<String> getSearchFilterParseValue();

    public Attribute setSearchFilterParseValue(ContextObject<String> searchFilterParseValue);

    public boolean isSearchFilterMulti();

    public Attribute setSearchFilterMulti(boolean searchFilterMulti);

    public int getSearchFilterPosition();

    public Attribute setSearchFilterPosition(int searchFilterPosition);

    public boolean isEditable();

    public Attribute setEditable(boolean editable);

    public boolean isEnabled();

    public Attribute setEnabled(boolean enabled);

    public InputType getInputType();

    public Attribute setInputType(InputType inputType);

    public boolean isSearchable();

    public Attribute setSearchable(boolean searchable);

    public boolean isIncludeInSearchIndex();

    public Attribute setIncludeInSearchIndex(boolean includeInSearchIndex);

    public List<Id> getLinkedAttributeIds();

    public void setLinkedAttributeIds(List<Id> linkedAttributeIds);

    public Attribute addLinkedAttribute(Attribute attribute);

    public Attribute removeLinkedAttribute(Attribute attribute);

    @JsonIgnore
    public List<Attribute> getLinkedAttributes();

    public List<AttributeOption> getOptions();

    public Attribute setOptions(List<AttributeOption> options);

    public Attribute addOption(AttributeOption option);

    public Attribute addOptions(String... values);

    public Attribute addOptionsWithGroupingTag(String tag, String... values);

    public Attribute addOptions(List<ContextObject<String>> values);

    public Attribute addOptionsWithGroupingTag(String tag, List<ContextObject<String>> values);

    public AttributeOption getOption(Id optionId);

    public AttributeOption getOptionWithGlobalLabel(String globalLabel);

    public AttributeOption getOption(String language, String label);

    public boolean hasOptionWithGlobalLabel(String globalLabel);
    
    public boolean hasOption(String language, String label);

    public Attribute setSearchFilterOptions(boolean includeInSearchFilter, FilterType searchFilterInputType,
        boolean searchFilterMulti, int searchFilterPosition);

    public Attribute removeFromSearchFilter();

    public Attribute refreshOptions();

    // ---------------------------------------------------------------------
    // Column names
    // ---------------------------------------------------------------------
    class Col {
        public static final String ID = "_id";
        public static final String ID2 = "id2";
        public static final String CODE = "code";
        public static final String CODE2 = "code2";
        public static final String TARGET_OBJECT_ID = "tar_obj";
        public static final String TYPE = "type";
        public static final String SCOPES = "scopes";
        public static final String EDITABLE = "editable";
        public static final String ENABLED = "enabled";

        // Admin panel settings
        public static final String FRONTEND_INPUT = "fnd_input";
        public static final String FRONTEND_OUTPUT = "fnd_output";
        public static final String FRONTEND_LABEL = "fnd_label";
        public static final String FRONTEND_FORMAT = "fnd_format";
        public static final String FRONTEND_STYLE = "fnd_style";
        public static final String FRONTEND_CLASS = "fnd_class";
        public static final String BACKEND_LABEL = "bnd_label";
        public static final String BACKEND_TYPE = "bnd_type";
        public static final String BACKEND_NOTE = "bnd_note";
        public static final String DEFAULT_VALUE = "def_value";
        public static final String INPUT_TYPE = "inp_type";
        public static final String INPUT_TYPE_EXPRESSION = "inp_expr";
        public static final String IS_OPTION_ATTRIBUTE = "option_attr";
        public static final String IS_MULTIPLE_ALLOWED = "multi";
        public static final String IS_I18N = "i18n";

        // Validation settings
        public static final String VALIDATION_MIN = "val_min";
        public static final String VALIDATION_MAX = "val_max";
        public static final String VALIDATION_MIN_LENGTH = "val_min_len";
        public static final String VALIDATION_MAX_LENGTH = "val_max_len";
        public static final String VALIDATION_FUTURE = "val_future";
        public static final String VALIDATION_PAST = "val_past";
        public static final String VALIDATION_ASSERT_TRUE = "val_ass_true";
        public static final String VALIDATION_ASSERT_FALSE = "val_ass_false";
        public static final String VALIDATION_PATTERN = "val_pattern";
        public static final String VALIDATION_SCRIPT = "val_script";
        public static final String VALIDATION_MESSAGE = "val_msg";

        // Virtual attribute setting
        public static final String LINKED_ATTRIBUTE_IDS = "lnk_attr";

        // Display settings
        public static final String SEARCHABLE = "searchable";
        public static final String INCLUDE_IN_SEARCH_INDEX = "search_idx_inc";
        public static final String SHOW_IN_PRODUCT_DETAILS = "show_in_prd_details";

        // Product settings
        public static final String PRODUCT_TYPES = "prd_types";

        // Product settings
        public static final String ALLOW_NEW_OPTIONS_VIA_IMPORT = "new_options_via_import";

        // ProductList filter settings
        public static final String INCLUDE_IN_PRODUCT_LIST_FILTER = "pl_filter_enabled";
        public static final String PRODUCT_LIST_FILTER_TYPE = "pl_filter_type";
        public static final String PRODUCT_LIST_FILTER_INDEX_FIELDS = "pl_filter_index_fields";
        public static final String PRODUCT_LIST_FILTER_KEY_ALIAS = "pl_filter_key_alias";
        public static final String PRODUCT_LIST_FILTER_FORMAT_LABEL = "pl_filter_format_label";
        public static final String PRODUCT_LIST_FILTER_FORMAT_VALUE = "pl_filter_format_value";
        public static final String PRODUCT_LIST_FILTER_PARSE_VALUE = "pl_filter_parse_value";
        public static final String PRODUCT_LIST_FILTER_MULTI = "pl_filter_multi";
        public static final String PRODUCT_LIST_FILTER_INHERIT_FROM_PARENT = "pl_filter_parent_inh";
        public static final String PRODUCT_LIST_FILTER_INCLUDE_CHILDREN = "pl_filter_child_inc";
        public static final String PRODUCT_LIST_FILTER_POSITION = "pl_filter_pos";

        // ProductList query settings
        public static final String SHOW_IN_QUERY = "pl_query_enabled";

        // Search filter settings
        public static final String INCLUDE_IN_SEARCH_FILTER = "s_filter_enabled";
        public static final String SEARCH_FILTER_TYPE = "s_filter_type";
        public static final String SEARCH_FILTER_INDEX_FIELDS = "s_filter_index_fields";
        public static final String SEARCH_FILTER_KEY_ALIAS = "s_filter_key_alias";
        public static final String SEARCH_FILTER_FORMAT_LABEL = "s_filter_format_label";
        public static final String SEARCH_FILTER_FORMAT_VALUE = "s_filter_format_value";
        public static final String SEARCH_FILTER_PARSE_VALUE = "s_filter_parse_value";
        public static final String SEARCH_FILTER_MULTI = "s_filter_multi";
        public static final String SEARCH_FILTER_POSITION = "s_filter_pos";

        public static final String DIMENSION_ATTRIBUTE = "dim_attr";
    }

}
