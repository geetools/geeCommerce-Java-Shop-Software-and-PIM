package com.geecommerce.core.system.attribute.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.enums.AttributeType;
import com.geecommerce.core.enums.BackendType;
import com.geecommerce.core.enums.FilterIndexField;
import com.geecommerce.core.enums.FilterType;
import com.geecommerce.core.enums.FrontendInput;
import com.geecommerce.core.enums.FrontendOutput;
import com.geecommerce.core.enums.InputType;
import com.geecommerce.core.enums.ProductType;
import com.geecommerce.core.enums.Scope;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.attribute.repository.AttributeOptions;
import com.geecommerce.core.system.attribute.repository.AttributeTargetObjects;
import com.geecommerce.core.system.attribute.repository.Attributes;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.owlike.genson.annotation.JsonIgnore;

@Cacheable
@Model(collection = "attributes", autoPopulate = false, preload = true)
@XmlRootElement(name = "attribute")
@XmlAccessorType(XmlAccessType.FIELD)
public class DefaultAttribute extends AbstractMultiContextModel implements Attribute {
    private static final long serialVersionUID = -4134855355218367101L;
    @Column(Col.ID)
    private Id id = null;
    @Column(Col.ID2)
    private Id id2 = null;
    @Column(Col.CODE)
    private String code = null;
    @Column(Col.CODE2)
    private String code2 = null;
    @Column(Col.TARGET_OBJECT_ID)
    private Id targetObjectId = null;
    @Column(Col.TYPE)
    private AttributeType type = null;
    @Column(Col.SCOPES)
    private List<Scope> scopes = null;
    @Column(Col.EDITABLE)
    private boolean editable = false;
    @Column(Col.ENABLED)
    private boolean enabled = false;

    // Display settings
    @Column(Col.SEARCHABLE)
    private boolean searchable = false;
    @Column(Col.INCLUDE_IN_SEARCH_INDEX)
    private boolean includeInSearchIndex = false;
    @Column(Col.SHOW_IN_PRODUCT_DETAILS)
    private boolean showInProductDetails = false;

    // Virtual attribute setting
    @Column(Col.LINKED_ATTRIBUTE_IDS)
    private List<Id> linkedAttributeIds = null;

    // Admin panel settings
    @Column(Col.FRONTEND_INPUT)
    private FrontendInput frontendInput = null;
    @Column(Col.FRONTEND_OUTPUT)
    private FrontendOutput frontendOutput = null;
    @Column(Col.FRONTEND_LABEL)
    private ContextObject<String> frontendLabel = null;
    @Column(Col.FRONTEND_FORMAT)
    private ContextObject<String> frontendFormat = null;
    @Column(Col.FRONTEND_STYLE)
    private String frontendStyle = null;
    @Column(Col.FRONTEND_CLASS)
    private String frontendClass = null;
    @Column(Col.BACKEND_LABEL)
    private ContextObject<String> backendLabel = null;
    @Column(Col.BACKEND_TYPE)
    private BackendType backendType = null;
    @Column(Col.BACKEND_NOTE)
    private ContextObject<String> backendNote = null;
    @Column(Col.DEFAULT_VALUE)
    private ContextObject<?> defaultValue = null;
    @Column(Col.INPUT_TYPE)
    private InputType inputType = null;
    @Column(Col.IS_OPTION_ATTRIBUTE)
    private boolean optionAttribute = false;
    @Column(Col.IS_MULTIPLE_ALLOWED)
    private boolean allowMultipleValues = false;
    @Column(Col.IS_I18N)
    private boolean i18n = false;

    // Validation settings
    @Column(Col.VALIDATION_MIN)
    private ContextObject<Double> validationMin = null;
    @Column(Col.VALIDATION_MAX)
    private ContextObject<Double> validationMax = null;
    @Column(Col.VALIDATION_MIN_LENGTH)
    private ContextObject<Integer> validationMinLength = null;
    @Column(Col.VALIDATION_MAX_LENGTH)
    private ContextObject<Integer> validationMaxLength = null;
    @Column(Col.VALIDATION_FUTURE)
    private ContextObject<Boolean> validationFuture = null;
    @Column(Col.VALIDATION_PAST)
    private ContextObject<Boolean> validationPast = null;
    @Column(Col.VALIDATION_ASSERT_TRUE)
    private ContextObject<Boolean> validationAssertTrue = null;
    @Column(Col.VALIDATION_ASSERT_FALSE)
    private ContextObject<Boolean> validationAssertFalse = null;
    @Column(Col.VALIDATION_PATTERN)
    private ContextObject<String> validationPattern = null;
    @Column(Col.VALIDATION_SCRIPT)
    private ContextObject<String> validationScript = null;
    @Column(Col.VALIDATION_MESSAGE)
    private ContextObject<String> validationMessage = null;

    // Product settings
    @Column(Col.PRODUCT_TYPES)
    private Set<ProductType> productTypes = null;

    // Product list settings
    @Column(Col.INCLUDE_IN_PRODUCT_LIST_FILTER)
    private Boolean includeInProductListFilter = null;
    @Column(Col.PRODUCT_LIST_FILTER_TYPE)
    private FilterType productListFilterType = null;
    @Column(Col.PRODUCT_LIST_FILTER_INDEX_FIELDS)
    private List<FilterIndexField> productListFilterIndexFields = null;
    @Column(Col.PRODUCT_LIST_FILTER_KEY_ALIAS)
    private ContextObject<String> productListFilterKeyAlias = null;
    @Column(Col.PRODUCT_LIST_FILTER_FORMAT_LABEL)
    private ContextObject<String> productListFilterFormatLabel = null;
    @Column(Col.PRODUCT_LIST_FILTER_FORMAT_VALUE)
    private ContextObject<String> productListFilterFormatValue = null;
    @Column(Col.PRODUCT_LIST_FILTER_PARSE_VALUE)
    private ContextObject<String> productListFilterParseValue = null;
    @Column(Col.PRODUCT_LIST_FILTER_MULTI)
    private boolean productListFilterMulti = false;
    @Column(Col.PRODUCT_LIST_FILTER_INHERIT_FROM_PARENT)
    private boolean productListFilterInheritFromParent = false;
    @Column(Col.PRODUCT_LIST_FILTER_INCLUDE_CHILDREN)
    private boolean productListFilterIncludeChildren = false;
    @Column(Col.PRODUCT_LIST_FILTER_POSITION)
    private int productListFilterPosition = 0;

    @Column(Col.INCLUDE_IN_PRODUCT_LIST_QUERY)
    private Boolean includeInProductListQuery = null;

    // Search settings
    @Column(Col.INCLUDE_IN_SEARCH_FILTER)
    private Boolean includeInSearchFilter = null;
    @Column(Col.SEARCH_FILTER_TYPE)
    private FilterType searchFilterType = null;
    @Column(Col.SEARCH_FILTER_INDEX_FIELDS)
    private List<FilterIndexField> searchFilterIndexFields = null;
    @Column(Col.SEARCH_FILTER_KEY_ALIAS)
    private ContextObject<String> searchFilterKeyAlias = null;
    @Column(Col.SEARCH_FILTER_FORMAT_LABEL)
    private ContextObject<String> searchFilterFormatLabel = null;
    @Column(Col.SEARCH_FILTER_FORMAT_VALUE)
    private ContextObject<String> searchFilterFormatValue = null;
    @Column(Col.SEARCH_FILTER_PARSE_VALUE)
    private ContextObject<String> searchFilterParseValue = null;
    @Column(Col.SEARCH_FILTER_MULTI)
    private boolean searchFilterMulti = false;
    @Column(Col.SEARCH_FILTER_POSITION)
    private int searchFilterPosition = 0;
    @Column(Col.DIMENSION_ATTRIBUTE)
    private boolean dimensionAttribute = false;

    // Lazy loaded attribute options
    private transient List<AttributeOption> options = null;

    // Lazy loaded attribute links
    private transient List<Attribute> linkedAttributes = null;

    // Lazy loaded attribute target object
    private transient AttributeTargetObject targetObject = null;

    // Attributes repository
    private final transient Attributes attributes;

    // Attribute options repository
    private final transient AttributeOptions attributeOptions;

    // Attribute target objects repository
    private final transient AttributeTargetObjects attributeTargetObjects;

    public DefaultAttribute() {
        this(i(Attributes.class), i(AttributeOptions.class), i(AttributeTargetObjects.class));
    }

    @Inject
    public DefaultAttribute(Attributes attributes, AttributeOptions attributeOptions, AttributeTargetObjects attributeTargetObjects) {
        this.attributes = attributes;
        this.attributeOptions = attributeOptions;
        this.attributeTargetObjects = attributeTargetObjects;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public Attribute setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getId2() {
        return id2;
    }

    @Override
    public Attribute setId2(Id id2) {
        this.id2 = id2;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Attribute setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public String getCode2() {
        return code2;
    }

    @Override
    public Attribute setCode2(String code2) {
        this.code2 = code2;
        return this;
    }

    @Override
    public Id getTargetObjectId() {
        return targetObjectId;
    }

    @Override
    public Attribute setTargetObjectId(Id targetObjectId) {
        this.targetObjectId = targetObjectId;
        return this;
    }

    @Override
    public AttributeTargetObject getTargetObject() {
        if (targetObjectId == null)
            return null;

        if (targetObject == null) {
            targetObject = attributeTargetObjects.findById(AttributeTargetObject.class, targetObjectId);
        }

        return targetObject;
    }

    @Override
    public AttributeType getType() {
        return type;
    }

    @Override
    public Attribute setType(AttributeType type) {
        this.type = type;
        return this;
    }

    @Override
    public List<Scope> getScopes() {
        return scopes;
    }

    @Override
    public Attribute setScopes(List<Scope> scopes) {
        this.scopes = scopes;
        return this;
    }

    @Override
    public Attribute addScope(Scope scope) {
        if (scopes == null)
            scopes = new ArrayList<>();

        if (!scopes.contains(scope))
            scopes.add(scope);

        return this;
    }

    @Override
    public boolean hasScope(Scope scope) {
        return scopes != null && scopes.contains(scope);
    }

    @Override
    public FrontendInput getFrontendInput() {
        return frontendInput;
    }

    @Override
    public Attribute setFrontendInput(FrontendInput frontendInput) {
        this.frontendInput = frontendInput;
        return this;
    }

    @Override
    public FrontendOutput getFrontendOutput() {
        return frontendOutput;
    }

    @Override
    public Attribute setFrontendOutput(FrontendOutput frontendOutput) {
        this.frontendOutput = frontendOutput;
        return this;
    }

    @Override
    public ContextObject<String> getFrontendLabel() {
        return frontendLabel;
    }

    @Override
    public Attribute setFrontendLabel(ContextObject<String> frontendLabel) {
        this.frontendLabel = frontendLabel;
        return this;
    }

    @Override
    public ContextObject<String> getFrontendFormat() {
        return frontendFormat;
    }

    @Override
    public Attribute setFrontendFormat(ContextObject<String> frontendFormat) {
        this.frontendFormat = frontendFormat;
        return this;
    }

    @Override
    public String getFrontendStyle() {
        return frontendStyle;
    }

    @Override
    public Attribute setFrontendStyle(String frontendStyle) {
        this.frontendStyle = frontendStyle;
        return this;
    }

    @Override
    public String getFrontendClass() {
        return frontendClass;
    }

    @Override
    public Attribute setFrontendClass(String frontendClass) {
        this.frontendClass = frontendClass;
        return this;
    }

    @Override
    public ContextObject<String> getBackendLabel() {
        return backendLabel;
    }

    @Override
    public Attribute setBackendLabel(ContextObject<String> backendLabel) {
        this.backendLabel = backendLabel;
        return this;
    }

    @Override
    public BackendType getBackendType() {
        return backendType;
    }

    @Override
    public Attribute setBackendType(BackendType backendType) {
        this.backendType = backendType;
        return this;
    }

    @Override
    public ContextObject<String> getBackendNote() {
        return backendNote;
    }

    @Override
    public Attribute setBackendNote(ContextObject<String> backendNote) {
        this.backendNote = backendNote;
        return this;
    }

    @Override
    public ContextObject<?> getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Attribute setDefaultValue(ContextObject<?> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public ContextObject<Double> getValidationMin() {
        return validationMin;
    }

    @Override
    public Attribute setValidationMin(ContextObject<Double> validationMin) {
        this.validationMin = validationMin;
        return this;
    }

    @Override
    public ContextObject<Double> getValidationMax() {
        return validationMax;
    }

    @Override
    public Attribute setValidationMax(ContextObject<Double> validationMax) {
        this.validationMax = validationMax;
        return this;
    }

    @Override
    public ContextObject<Integer> getValidationMinLength() {
        return validationMinLength;
    }

    @Override
    public Attribute setValidationMinLength(ContextObject<Integer> validationMinLength) {
        this.validationMinLength = validationMinLength;
        return this;
    }

    @Override
    public ContextObject<Integer> getValidationMaxLength() {
        return validationMaxLength;
    }

    @Override
    public Attribute setValidationMaxLength(ContextObject<Integer> validationMaxLength) {
        this.validationMaxLength = validationMaxLength;
        return this;
    }

    @Override
    public ContextObject<Boolean> getValidationFuture() {
        return validationFuture;
    }

    @Override
    public Attribute setValidationFuture(ContextObject<Boolean> validationFuture) {
        this.validationFuture = validationFuture;
        return this;
    }

    @Override
    public ContextObject<Boolean> getValidationPast() {
        return validationPast;
    }

    @Override
    public Attribute setValidationPast(ContextObject<Boolean> validationPast) {
        this.validationPast = validationPast;
        return this;
    }

    @Override
    public ContextObject<Boolean> getValidationAssertTrue() {
        return validationAssertTrue;
    }

    @Override
    public Attribute setValidationAssertTrue(ContextObject<Boolean> validationAssertTrue) {
        this.validationAssertTrue = validationAssertTrue;
        return this;
    }

    @Override
    public ContextObject<Boolean> getValidationAssertFalse() {
        return validationAssertFalse;
    }

    @Override
    public Attribute setValidationAssertFalse(ContextObject<Boolean> validationAssertFalse) {
        this.validationAssertFalse = validationAssertFalse;
        return this;
    }

    @Override
    public ContextObject<String> getValidationPattern() {
        return validationPattern;
    }

    @Override
    public Attribute setValidationPattern(ContextObject<String> validationPattern) {
        this.validationPattern = validationPattern;
        return this;
    }

    @Override
    public ContextObject<String> getValidationScript() {
        return validationScript;
    }

    @Override
    public Attribute setValidationScript(ContextObject<String> validationScript) {
        this.validationScript = validationScript;
        return this;
    }

    @Override
    public ContextObject<String> getValidationMessage() {
        return validationMessage;
    }

    @Override
    public Attribute setValidationMessage(ContextObject<String> validationMessage) {
        this.validationMessage = validationMessage;
        return this;
    }

    @Override
    public boolean isOptionAttribute() {
        return optionAttribute;
    }

    @Override
    public Attribute setOptionAttribute(boolean isOptionAttribute) {
        this.optionAttribute = isOptionAttribute;
        return this;
    }

    @Override
    public boolean isAllowMultipleValues() {
        return allowMultipleValues;
    }

    @Override
    public Attribute setAllowMultipleValues(boolean allowMultipleValues) {
        this.allowMultipleValues = allowMultipleValues;
        return this;
    }

    @Override
    public boolean isI18n() {
        return i18n;
    }

    @Override
    public Attribute setI18n(boolean i18n) {
        this.i18n = i18n;
        return this;
    }

    @Override
    public Set<ProductType> getProductTypes() {
        return productTypes;
    }

    @Override
    public Attribute setProductTypes(Set<ProductType> productTypes) {
        this.productTypes = productTypes;
        return this;
    }

    @Override
    public Attribute addProductTypes(ProductType... productTypes) {
        if (this.productTypes == null) {
            this.productTypes = new HashSet<ProductType>();
        }

        this.productTypes.addAll(Arrays.asList(productTypes));

        return this;
    }

    @Override
    public Boolean getIncludeInProductListQuery() {
        return includeInProductListQuery == null ? false : includeInProductListQuery;
    }

    @Override
    public Attribute setIncludeInProductListQuery(Boolean includeInProductListQuery) {
        this.includeInProductListQuery = includeInProductListQuery;
        return this;
    }

    @Override
    public Boolean getIncludeInProductListFilter() {
        return includeInProductListFilter == null ? false : includeInProductListFilter;
    }

    @Override
    public Attribute setIncludeInProductListFilter(Boolean includeInProductListFilter) {
        this.includeInProductListFilter = includeInProductListFilter;
        return this;
    }

    @Override
    public FilterType getProductListFilterType() {
        return productListFilterType;
    }

    @Override
    public Attribute setProductListFilterType(FilterType productListFilterType) {
        this.productListFilterType = productListFilterType;
        return this;
    }

    @Override
    public List<FilterIndexField> getProductListFilterIndexFields() {
        return productListFilterIndexFields;
    }

    @Override
    public Attribute setProductListFilterIndexFields(List<FilterIndexField> productListFilterIndexFields) {
        this.productListFilterIndexFields = productListFilterIndexFields;
        return this;
    }

    @Override
    public ContextObject<String> getProductListFilterKeyAlias() {
        return productListFilterKeyAlias;
    }

    @Override
    public Attribute setProductListFilterKeyAlias(ContextObject<String> productListFilterKeyAlias) {
        this.productListFilterKeyAlias = productListFilterKeyAlias;
        return this;
    }

    @Override
    public ContextObject<String> getProductListFilterFormatLabel() {
        return productListFilterFormatLabel;
    }

    @Override
    public Attribute setProductListFilterFormatLabel(ContextObject<String> productListFilterFormatLabel) {
        this.productListFilterFormatLabel = productListFilterFormatLabel;
        return this;
    }

    @Override
    public ContextObject<String> getProductListFilterFormatValue() {
        return productListFilterFormatValue;
    }

    @Override
    public Attribute setProductListFilterFormatValue(ContextObject<String> productListFilterFormatValue) {
        this.productListFilterFormatValue = productListFilterFormatValue;
        return this;
    }

    @Override
    public ContextObject<String> getProductListFilterParseValue() {
        return productListFilterParseValue;
    }

    @Override
    public Attribute setProductListFilterParseValue(ContextObject<String> productListFilterParseValue) {
        this.productListFilterParseValue = productListFilterParseValue;
        return this;
    }

    @Override
    public boolean isProductListFilterMulti() {
        return productListFilterMulti;
    }

    @Override
    public Attribute setProductListFilterMulti(boolean productListFilterMulti) {
        this.productListFilterMulti = productListFilterMulti;
        return this;
    }

    @Override
    public boolean isProductListFilterInheritFromParent() {
        return productListFilterInheritFromParent;
    }

    @Override
    public Attribute setProductListFilterInheritFromParent(boolean productListFilterInheritFromParent) {
        this.productListFilterInheritFromParent = productListFilterInheritFromParent;
        return this;
    }

    @Override
    public boolean isProductListFilterIncludeChildren() {
        return productListFilterIncludeChildren;
    }

    @Override
    public Attribute setProductListFilterIncludeChildren(boolean productListFilterIncludeChildren) {
        this.productListFilterIncludeChildren = productListFilterIncludeChildren;
        return this;
    }

    @Override
    public int getProductListFilterPosition() {
        return productListFilterPosition;
    }

    @Override
    public Attribute setProductListFilterPosition(int productListFilterPosition) {
        this.productListFilterPosition = productListFilterPosition;
        return this;
    }

    @Override
    public Boolean getIncludeInSearchFilter() {
        return includeInSearchFilter == null ? false : includeInSearchFilter;
    }

    @Override
    public Attribute setIncludeInSearchFilter(Boolean includeInSearchFilter) {
        this.includeInSearchFilter = includeInSearchFilter;
        return this;
    }

    @Override
    public FilterType getSearchFilterType() {
        return searchFilterType;
    }

    @Override
    public Attribute setSearchFilterType(FilterType searchFilterType) {
        this.searchFilterType = searchFilterType;
        return this;
    }

    @Override
    public List<FilterIndexField> getSearchFilterIndexFields() {
        return searchFilterIndexFields;
    }

    @Override
    public Attribute setSearchFilterIndexFields(List<FilterIndexField> searchFilterIndexFields) {
        this.searchFilterIndexFields = searchFilterIndexFields;
        return this;
    }

    @Override
    public ContextObject<String> getSearchFilterKeyAlias() {
        return searchFilterKeyAlias;
    }

    @Override
    public Attribute setSearchFilterKeyAlias(ContextObject<String> searchFilterKeyAlias) {
        this.searchFilterKeyAlias = searchFilterKeyAlias;
        return this;
    }

    @Override
    public ContextObject<String> getSearchFilterFormatLabel() {
        return searchFilterFormatLabel;
    }

    @Override
    public Attribute setSearchFilterFormatLabel(ContextObject<String> searchFilterFormatLabel) {
        this.searchFilterFormatLabel = searchFilterFormatLabel;
        return this;
    }

    @Override
    public ContextObject<String> getSearchFilterFormatValue() {
        return searchFilterFormatValue;
    }

    @Override
    public Attribute setSearchFilterFormatValue(ContextObject<String> searchFilterFormatValue) {
        this.searchFilterFormatValue = searchFilterFormatValue;
        return this;
    }

    @Override
    public ContextObject<String> getSearchFilterParseValue() {
        return searchFilterParseValue;
    }

    @Override
    public Attribute setSearchFilterParseValue(ContextObject<String> searchFilterParseValue) {
        this.searchFilterParseValue = searchFilterParseValue;
        return this;
    }

    @Override
    public boolean isSearchFilterMulti() {
        return searchFilterMulti;
    }

    @Override
    public Attribute setSearchFilterMulti(boolean searchFilterMulti) {
        this.searchFilterMulti = searchFilterMulti;
        return this;
    }

    @Override
    public int getSearchFilterPosition() {
        return searchFilterPosition;
    }

    @Override
    public Attribute setSearchFilterPosition(int searchFilterPosition) {
        this.searchFilterPosition = searchFilterPosition;
        return this;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public Attribute setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Attribute setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public boolean isDimensionAttribute() {
        return dimensionAttribute;
    }

    @Override
    public Attribute setDimensionAttribute(boolean dimensionAttribute) {
        this.dimensionAttribute = dimensionAttribute;
        return this;
    }

    @Override
    public InputType getInputType() {
        return inputType;
    }

    @Override
    public Attribute setInputType(InputType inputType) {
        this.inputType = inputType;
        return this;
    }

    @Override
    public boolean isSearchable() {
        return searchable;
    }

    @Override
    public Attribute setSearchable(boolean searchable) {
        this.searchable = searchable;
        return this;
    }

    @Override
    public boolean isIncludeInSearchIndex() {
        return includeInSearchIndex;
    }

    @Override
    public Attribute setIncludeInSearchIndex(boolean includeInSearchIndex) {
        this.includeInSearchIndex = includeInSearchIndex;
        return this;
    }

    @Override
    public boolean isShowInProductDetails() {
        return showInProductDetails;
    }

    @Override
    public Attribute setShowInProductDetails(boolean showInProductDetails) {
        this.showInProductDetails = showInProductDetails;
        return this;
    }

    @Override
    public List<Id> getLinkedAttributeIds() {
        return linkedAttributeIds;
    }

    @Override
    public void setLinkedAttributeIds(List<Id> linkedAttributeIds) {
        if (this.type != AttributeType.VIRTUAL && linkedAttributeIds != null && linkedAttributeIds.size() > 0)
            throw new IllegalArgumentException("Attribute links can only be set when the attribute type is 'VIRTUAL'");

        if ((this.optionAttribute || (options != null && options.size() > 0)) && linkedAttributeIds != null && linkedAttributeIds.size() > 0)
            throw new IllegalArgumentException("Attribute links cannot be set for option-attributes");

        this.linkedAttributeIds = linkedAttributeIds;

        // Reset the lazy loaded list of attributes.
        linkedAttributes = null;
    }

    @Override
    public Attribute addLinkedAttribute(Attribute attribute) {
        if (this.type != AttributeType.VIRTUAL)
            throw new IllegalArgumentException("Attribute links can only be set when the attribute type is 'VIRTUAL'");

        if (this.optionAttribute || (options != null && options.size() > 0))
            throw new IllegalArgumentException("Attribute links cannot be set for option-attributes");

        if (attribute == null || attribute.getId() == null)
            throw new NullPointerException("The attribute or its id cannot be null.");

        if (this.linkedAttributeIds == null)
            this.linkedAttributeIds = new ArrayList<>();

        this.linkedAttributeIds.add(attribute.getId());

        // Reset the lazy loaded list of attributes.
        linkedAttributes = null;

        return this;
    }

    @Override
    public Attribute removeLinkedAttribute(Attribute attribute) {
        if (attribute == null || attribute.getId() == null)
            throw new NullPointerException("The attribute or its id cannot be null.");

        if (linkedAttributeIds != null && linkedAttributeIds.size() > 0) {
            linkedAttributeIds.remove(attribute.getId());
        }

        // Reset the lazy loaded list of attributes.
        linkedAttributes = null;

        return this;
    }

    @JsonIgnore
    @Override
    public List<Attribute> getLinkedAttributes() {
        if (linkedAttributeIds != null && linkedAttributeIds.size() > 0 && linkedAttributes == null) {
            linkedAttributes = attributes.findByIds(Attribute.class, linkedAttributeIds.toArray(new Id[linkedAttributeIds.size()]));
        }

        return linkedAttributes;
    }

    @Override
    public List<AttributeOption> getOptions() {
        return options;
    }

    @Override
    public Attribute setOptions(List<AttributeOption> options) {
        if (this.type == AttributeType.VIRTUAL && options != null && options.size() > 0)
            throw new IllegalArgumentException("Options cannot be set when the attribute type is 'VIRTUAL'");

        if (this.linkedAttributeIds != null && this.linkedAttributeIds.size() > 0 && options != null && options.size() > 0)
            throw new IllegalArgumentException("Options cannot be set when the linked attributes exist");

        this.options = options;

        return this;
    }

    @Override
    public Attribute addOption(AttributeOption option) {
        if (this.type == AttributeType.VIRTUAL)
            throw new IllegalArgumentException("Options cannot be set when the attribute type is 'VIRTUAL'");

        if (this.linkedAttributeIds != null && this.linkedAttributeIds.size() > 0)
            throw new IllegalArgumentException("Options cannot be set when the linked attributes exist");

        if (this.options == null)
            this.options = new ArrayList<>();

        this.options.add(option);

        return this;
    }

    @Override
    public Attribute addOptions(String... values) {
        if (this.type == AttributeType.VIRTUAL)
            throw new IllegalArgumentException("Options cannot be set when the attribute type is 'VIRTUAL'");

        if (this.linkedAttributeIds != null && this.linkedAttributeIds.size() > 0)
            throw new IllegalArgumentException("Options cannot be set when the linked attributes exist");

        return addOptionsWithGroupingTag(null, values);
    }

    @Override
    public Attribute addOptionsWithGroupingTag(String tag, String... values) {
        if (this.type == AttributeType.VIRTUAL)
            throw new IllegalArgumentException("Options cannot be set when the attribute type is 'VIRTUAL'");

        if (this.linkedAttributeIds != null && this.linkedAttributeIds.size() > 0)
            throw new IllegalArgumentException("Options cannot be set when the linked attributes exist");

        int lastPos = 0;

        if (options == null) {
            options = new ArrayList<>();
        } else {
            for (AttributeOption option : options) {
                if (option.getPosition() > lastPos) {
                    lastPos = option.getPosition();
                }
            }
        }

        if (values != null && values.length > 0) {
            for (String val : values) {
                AttributeOption option = app.getModel(AttributeOption.class).belongsTo(this).setLabel(new ContextObject<String>(val)).setPosition(++lastPos);

                if (tag != null && !"".equals(tag.trim())) {
                    option.addTag(tag);
                }

                this.options.add(option);
            }
        }

        return this;
    }

    @Override
    public Attribute addOptions(List<ContextObject<String>> values) {
        if (this.type == AttributeType.VIRTUAL)
            throw new IllegalArgumentException("Options cannot be set when the attribute type is 'VIRTUAL'");

        if (this.linkedAttributeIds != null && this.linkedAttributeIds.size() > 0)
            throw new IllegalArgumentException("Options cannot be set when the linked attributes exist");

        return addOptionsWithGroupingTag(null, values);
    }

    @Override
    public Attribute addOptionsWithGroupingTag(String tag, List<ContextObject<String>> values) {
        if (this.type == AttributeType.VIRTUAL)
            throw new IllegalArgumentException("Options cannot be set when the attribute type is 'VIRTUAL'");

        if (this.linkedAttributeIds != null && this.linkedAttributeIds.size() > 0)
            throw new IllegalArgumentException("Options cannot be set when the linked attributes exist");

        int lastPos = 0;

        if (options == null) {
            options = new ArrayList<>();
        } else {
            for (AttributeOption option : options) {
                if (option.getPosition() > lastPos) {
                    lastPos = option.getPosition();
                }
            }
        }

        if (values != null && values.size() > 0) {
            for (ContextObject<String> val : values) {
                AttributeOption option = app.getModel(AttributeOption.class).belongsTo(this).setLabel(val).setPosition(++lastPos);

                if (tag != null && !"".equals(tag.trim())) {
                    option.addTag(tag);
                }

                this.options.add(option);
            }
        }

        return this;
    }

    @Override
    public AttributeOption getOption(Id optionId) {
        if (this.options == null || optionId == null)
            return null;

        for (AttributeOption option : this.options) {
            if (option != null && optionId.equals(option.getId()))
                return option;
        }

        return null;
    }

    @Override
    public AttributeOption getOptionWithGlobalLabel(String globalLabel) {
        if (this.options == null)
            return null;

        for (AttributeOption option : this.options) {
            ContextObject<String> label = option.getLabel();

            if (option != null && label != null && label.globalValueExists(globalLabel))
                return option;
        }

        return null;
    }

    @Override
    public boolean hasOptionWithGlobalLabel(String globalLabel) {
        if (this.options == null)
            return false;

        for (AttributeOption option : this.options) {
            ContextObject<String> label = option.getLabel();

            if (option != null && label != null && label.globalValueExists(globalLabel))
                return true;
        }

        return false;
    }

    @Override
    public Attribute setSearchFilterOptions(boolean includeInSearchFilter, FilterType searchFilterInputType, boolean searchFilterMulti, int searchFilterPosition) {
        this.includeInSearchFilter = includeInSearchFilter;
        this.searchFilterPosition = searchFilterPosition;
        this.searchFilterType = searchFilterInputType;
        this.searchFilterMulti = searchFilterMulti;

        return this;
    }

    @Override
    public Attribute removeFromSearchFilter() {
        this.includeInSearchFilter = false;
        this.searchFilterType = null;
        this.searchFilterIndexFields = null;
        this.searchFilterMulti = false;
        this.searchFilterPosition = 0;

        return this;
    }

    @Override
    public Attribute refreshOptions() {
        this.options = attributeOptions.thatBelongTo(this);

        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Col.ID));
        this.id2 = id_(map.get(Col.ID2));
        this.code = str_(map.get(Col.CODE));
        this.code2 = str_(map.get(Col.CODE2));
        this.targetObjectId = id_(map.get(Col.TARGET_OBJECT_ID));
        this.type = enum_(AttributeType.class, map.get(Col.TYPE), AttributeType.DEFAULT);
        this.editable = bool_(map.get(Col.EDITABLE), false);
        this.enabled = bool_(map.get(Col.ENABLED), false);

        if (map.get(Col.SCOPES) != null)
            this.scopes = enumList_(Scope.class, map.get(Col.SCOPES));

        // Linked attributes for virtual attribute.
        this.linkedAttributeIds = idList_(map.get(Col.LINKED_ATTRIBUTE_IDS));

        // Admin panel settings
        this.frontendInput = enum_(FrontendInput.class, map.get(Col.FRONTEND_INPUT), FrontendInput.TEXT);
        this.frontendOutput = enum_(FrontendOutput.class, map.get(Col.FRONTEND_OUTPUT), FrontendOutput.TEXT);
        this.frontendLabel = ctxObj_(map.get(Col.FRONTEND_LABEL));

        if (map.get(Col.FRONTEND_FORMAT) != null)
            this.frontendFormat = ctxObj_(map.get(Col.FRONTEND_FORMAT));

        if (map.get(Col.FRONTEND_STYLE) != null)
            this.frontendStyle = str_(map.get(Col.FRONTEND_STYLE));

        if (map.get(Col.FRONTEND_CLASS) != null)
            this.frontendClass = str_(map.get(Col.FRONTEND_CLASS));

        this.backendLabel = ctxObj_(map.get(Col.BACKEND_LABEL));

        if (map.get(Col.BACKEND_TYPE) != null)
            this.backendType = enum_(BackendType.class, map.get(Col.BACKEND_TYPE));

        if (map.get(Col.BACKEND_NOTE) != null)
            this.backendNote = ctxObj_(map.get(Col.BACKEND_NOTE));

        if (map.get(Col.DEFAULT_VALUE) != null)
            this.defaultValue = ctxObj_(map.get(Col.DEFAULT_VALUE));

        if (map.get(Col.VALIDATION_MIN) != null)
            this.validationMin = ctxObj_(map.get(Col.VALIDATION_MIN));

        if (map.get(Col.VALIDATION_MAX) != null)
            this.validationMax = ctxObj_(map.get(Col.VALIDATION_MAX));

        if (map.get(Col.VALIDATION_MIN_LENGTH) != null)
            this.validationMinLength = ctxObj_(map.get(Col.VALIDATION_MIN_LENGTH));

        if (map.get(Col.VALIDATION_MAX_LENGTH) != null)
            this.validationMaxLength = ctxObj_(map.get(Col.VALIDATION_MAX_LENGTH));

        if (map.get(Col.VALIDATION_FUTURE) != null)
            this.validationFuture = ctxObj_(map.get(Col.VALIDATION_FUTURE));

        if (map.get(Col.VALIDATION_PAST) != null)
            this.validationPast = ctxObj_(map.get(Col.VALIDATION_PAST));

        if (map.get(Col.VALIDATION_ASSERT_TRUE) != null)
            this.validationAssertTrue = ctxObj_(map.get(Col.VALIDATION_ASSERT_TRUE));

        if (map.get(Col.VALIDATION_ASSERT_FALSE) != null)
            this.validationAssertFalse = ctxObj_(map.get(Col.VALIDATION_ASSERT_FALSE));

        if (map.get(Col.VALIDATION_PATTERN) != null)
            this.validationPattern = ctxObj_(map.get(Col.VALIDATION_PATTERN));

        if (map.get(Col.VALIDATION_SCRIPT) != null)
            this.validationScript = ctxObj_(map.get(Col.VALIDATION_SCRIPT));

        if (map.get(Col.VALIDATION_MESSAGE) != null)
            this.validationMessage = ctxObj_(map.get(Col.VALIDATION_MESSAGE));

        if (map.get(Col.INPUT_TYPE) != null)
            this.inputType = enum_(InputType.class, map.get(Col.INPUT_TYPE));

        this.searchable = bool_(map.get(Col.SEARCHABLE), false);
        this.includeInSearchIndex = bool_(map.get(Col.INCLUDE_IN_SEARCH_INDEX), false);
        this.showInProductDetails = bool_(map.get(Col.SHOW_IN_PRODUCT_DETAILS), false);
        this.allowMultipleValues = bool_(map.get(Col.IS_MULTIPLE_ALLOWED), false);
        this.optionAttribute = bool_(map.get(Col.IS_OPTION_ATTRIBUTE), false);
        this.i18n = bool_(map.get(Col.IS_I18N), false);

        // Product settings
        if (map.get(Col.PRODUCT_TYPES) != null)
            this.productTypes = enumSet_(ProductType.class, map.get(Col.PRODUCT_TYPES));

        // ProductList filter settings
        this.includeInProductListFilter = bool_(map.get(Col.INCLUDE_IN_PRODUCT_LIST_FILTER));
        this.productListFilterMulti = bool_(map.get(Col.PRODUCT_LIST_FILTER_MULTI), false);
        this.productListFilterInheritFromParent = bool_(map.get(Col.PRODUCT_LIST_FILTER_INHERIT_FROM_PARENT), false);
        this.productListFilterIncludeChildren = bool_(map.get(Col.PRODUCT_LIST_FILTER_INCLUDE_CHILDREN), false);
        this.productListFilterPosition = int_(map.get(Col.PRODUCT_LIST_FILTER_POSITION), 0);

        if (map.get(Col.PRODUCT_LIST_FILTER_KEY_ALIAS) != null)
            this.productListFilterKeyAlias = ctxObj_(map.get(Col.PRODUCT_LIST_FILTER_KEY_ALIAS));

        if (map.get(Col.PRODUCT_LIST_FILTER_FORMAT_LABEL) != null)
            this.productListFilterFormatLabel = ctxObj_(map.get(Col.PRODUCT_LIST_FILTER_FORMAT_LABEL));

        if (map.get(Col.PRODUCT_LIST_FILTER_FORMAT_VALUE) != null)
            this.productListFilterFormatValue = ctxObj_(map.get(Col.PRODUCT_LIST_FILTER_FORMAT_VALUE));

        if (map.get(Col.PRODUCT_LIST_FILTER_PARSE_VALUE) != null)
            this.productListFilterParseValue = ctxObj_(map.get(Col.PRODUCT_LIST_FILTER_PARSE_VALUE));

        if (map.get(Col.PRODUCT_LIST_FILTER_INDEX_FIELDS) != null)
            this.productListFilterIndexFields = enumList_(FilterIndexField.class, map.get(Col.PRODUCT_LIST_FILTER_INDEX_FIELDS));

        if (map.get(Col.PRODUCT_LIST_FILTER_TYPE) != null)
            this.productListFilterType = enum_(FilterType.class, map.get(Col.PRODUCT_LIST_FILTER_TYPE));

        this.includeInProductListQuery = bool_(map.get(Col.INCLUDE_IN_PRODUCT_LIST_QUERY));

        // Search filter settings
        this.includeInSearchFilter = bool_(map.get(Col.INCLUDE_IN_SEARCH_FILTER));
        this.searchFilterMulti = bool_(map.get(Col.SEARCH_FILTER_MULTI), false);
        this.dimensionAttribute = bool_(map.get(Col.DIMENSION_ATTRIBUTE), false);
        this.searchFilterPosition = int_(map.get(Col.SEARCH_FILTER_POSITION), 0);

        if (map.get(Col.SEARCH_FILTER_KEY_ALIAS) != null)
            this.searchFilterKeyAlias = ctxObj_(map.get(Col.SEARCH_FILTER_KEY_ALIAS));

        if (map.get(Col.SEARCH_FILTER_FORMAT_LABEL) != null)
            this.searchFilterFormatLabel = ctxObj_(map.get(Col.SEARCH_FILTER_FORMAT_LABEL));

        if (map.get(Col.SEARCH_FILTER_FORMAT_VALUE) != null)
            this.searchFilterFormatValue = ctxObj_(map.get(Col.SEARCH_FILTER_FORMAT_VALUE));

        if (map.get(Col.SEARCH_FILTER_PARSE_VALUE) != null)
            this.searchFilterParseValue = ctxObj_(map.get(Col.SEARCH_FILTER_PARSE_VALUE));

        if (map.get(Col.SEARCH_FILTER_INDEX_FIELDS) != null)
            enumList_(FilterIndexField.class, map.get(Col.SEARCH_FILTER_INDEX_FIELDS));

        if (map.get(Col.SEARCH_FILTER_TYPE) != null)
            this.searchFilterType = enum_(FilterType.class, map.get(Col.SEARCH_FILTER_TYPE));

        this.options = attributeOptions.thatBelongTo(this);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Col.ID, getId());

        if (getId2() != null)
            map.put(Col.ID2, getId2());

        map.put(Col.CODE, getCode());

        if (getCode2() != null)
            map.put(Col.CODE2, getCode2());

        map.put(Col.TARGET_OBJECT_ID, getTargetObjectId());
        map.put(Col.TYPE, getType() == null ? AttributeType.DEFAULT : getType().toId());
        map.put(Col.EDITABLE, isEditable());
        map.put(Col.ENABLED, isEnabled());

        if (getScopes() != null)
            map.put(Col.SCOPES, getScopes());

        // Linked attributes for virtual attribute.
        if (getLinkedAttributeIds() != null)
            map.put(Col.LINKED_ATTRIBUTE_IDS, getLinkedAttributeIds());

        // Admin panel settings
        if (getFrontendInput() != null) {
            FrontendInput frontendInput = getFrontendInput();

            map.put(Col.FRONTEND_INPUT, frontendInput.toId());

            if (frontendInput == FrontendInput.SELECT || frontendInput == FrontendInput.MULTISELECT || ((frontendInput == FrontendInput.COMBOBOX && isAllowMultipleValues()))) {
                map.put(Col.IS_OPTION_ATTRIBUTE, true);
            } else {
                map.put(Col.IS_OPTION_ATTRIBUTE, isOptionAttribute());
            }
        } else {
            map.put(Col.IS_OPTION_ATTRIBUTE, isOptionAttribute());
        }

        if (getFrontendOutput() != null) {
            map.put(Col.FRONTEND_OUTPUT, getFrontendOutput().toId());
        }

        if (getFrontendLabel() != null)
            map.put(Col.FRONTEND_LABEL, getFrontendLabel());

        if (getFrontendFormat() != null)
            map.put(Col.FRONTEND_FORMAT, getFrontendFormat());

        if (getFrontendStyle() != null)
            map.put(Col.FRONTEND_STYLE, getFrontendStyle());

        if (getFrontendClass() != null)
            map.put(Col.FRONTEND_CLASS, getFrontendClass());

        map.put(Col.BACKEND_LABEL, getBackendLabel());

        if (getBackendType() != null)
            map.put(Col.BACKEND_TYPE, getBackendType().toId());

        if (getBackendNote() != null)
            map.put(Col.BACKEND_NOTE, getBackendNote());

        if (getDefaultValue() != null)
            map.put(Col.DEFAULT_VALUE, getDefaultValue());

        if (getValidationMin() != null)
            map.put(Col.VALIDATION_MIN, getValidationMin());

        if (getValidationMax() != null)
            map.put(Col.VALIDATION_MAX, getValidationMax());

        if (getValidationMinLength() != null)
            map.put(Col.VALIDATION_MIN_LENGTH, getValidationMinLength());

        if (getValidationMaxLength() != null)
            map.put(Col.VALIDATION_MAX_LENGTH, getValidationMaxLength());

        if (getValidationFuture() != null)
            map.put(Col.VALIDATION_FUTURE, getValidationFuture());

        if (getValidationPast() != null)
            map.put(Col.VALIDATION_PAST, getValidationPast());

        if (getValidationAssertTrue() != null)
            map.put(Col.VALIDATION_ASSERT_TRUE, getValidationAssertTrue());

        if (getValidationAssertFalse() != null)
            map.put(Col.VALIDATION_ASSERT_FALSE, getValidationAssertFalse());

        if (getValidationPattern() != null)
            map.put(Col.VALIDATION_PATTERN, getValidationPattern());

        if (getValidationScript() != null)
            map.put(Col.VALIDATION_SCRIPT, getValidationScript());

        if (getValidationMessage() != null)
            map.put(Col.VALIDATION_MESSAGE, getValidationMessage());

        if (getInputType() != null)
            map.put(Col.INPUT_TYPE, getInputType().toId());

        map.put(Col.IS_MULTIPLE_ALLOWED, isAllowMultipleValues());
        map.put(Col.DIMENSION_ATTRIBUTE, isDimensionAttribute());
        map.put(Col.SEARCHABLE, isSearchable());
        map.put(Col.INCLUDE_IN_SEARCH_INDEX, isIncludeInSearchIndex());
        map.put(Col.SHOW_IN_PRODUCT_DETAILS, isShowInProductDetails());
        map.put(Col.IS_I18N, isI18n());

        // Product settings
        if (getProductTypes() != null)
            map.put(Col.PRODUCT_TYPES, getProductTypes());

        // ProductList filter settings
        if (getIncludeInProductListFilter() != null && getProductListFilterType() != null) {
            map.put(Col.INCLUDE_IN_PRODUCT_LIST_FILTER, getIncludeInProductListFilter());
            map.put(Col.PRODUCT_LIST_FILTER_TYPE, getProductListFilterType().toId());
            map.put(Col.PRODUCT_LIST_FILTER_INDEX_FIELDS, getProductListFilterIndexFields());
            map.put(Col.PRODUCT_LIST_FILTER_KEY_ALIAS, getProductListFilterKeyAlias());
            map.put(Col.PRODUCT_LIST_FILTER_FORMAT_LABEL, getProductListFilterFormatLabel());
            map.put(Col.PRODUCT_LIST_FILTER_FORMAT_VALUE, getProductListFilterFormatValue());
            map.put(Col.PRODUCT_LIST_FILTER_PARSE_VALUE, getProductListFilterParseValue());
            map.put(Col.PRODUCT_LIST_FILTER_MULTI, isProductListFilterMulti());
            map.put(Col.PRODUCT_LIST_FILTER_INHERIT_FROM_PARENT, isProductListFilterInheritFromParent());
            map.put(Col.PRODUCT_LIST_FILTER_INCLUDE_CHILDREN, isProductListFilterIncludeChildren());
            map.put(Col.PRODUCT_LIST_FILTER_POSITION, getProductListFilterPosition());
        }

        // Search filter settings
        if (getIncludeInSearchFilter() != null && getSearchFilterType() != null) {
            map.put(Col.INCLUDE_IN_SEARCH_FILTER, getIncludeInSearchFilter());
            map.put(Col.SEARCH_FILTER_TYPE, getSearchFilterType().toId());
            map.put(Col.SEARCH_FILTER_INDEX_FIELDS, getSearchFilterIndexFields());
            map.put(Col.SEARCH_FILTER_KEY_ALIAS, getSearchFilterKeyAlias());
            map.put(Col.SEARCH_FILTER_FORMAT_LABEL, getSearchFilterFormatLabel());
            map.put(Col.SEARCH_FILTER_FORMAT_VALUE, getSearchFilterFormatValue());
            map.put(Col.SEARCH_FILTER_PARSE_VALUE, getSearchFilterParseValue());
            map.put(Col.SEARCH_FILTER_MULTI, isSearchFilterMulti());
            map.put(Col.SEARCH_FILTER_POSITION, getSearchFilterPosition());
        }

        map.put(Col.INCLUDE_IN_PRODUCT_LIST_QUERY, getIncludeInProductListQuery());
        return map;
    }

    /**
     * Make sure that the options are re-fetched after deserialization.
     */
    protected void afterReadingObject() {
        System.out.println("After reading object!");
        this.options = attributeOptions.thatBelongTo(this);
    }

    @Override
    public String toString() {
        return "DefaultAttribute [id=" + id + ", id2=" + id2 + ", code=" + code + ", code2=" + code2 + ", targetObjectId=" + targetObjectId + ", scopes=" + scopes + ", editable=" + editable
            + ", enabled=" + enabled + ", searchable=" + searchable
            + ", includeInSearchIndex=" + includeInSearchIndex + ", showInProductDetails=" + showInProductDetails + ", frontendInput=" + frontendInput + ", frontendLabel=" + frontendLabel
            + ", backendLabel=" + backendLabel + ", inputType="
            + inputType + ", optionAttribute=" + optionAttribute + ", allowMultipleValues=" + allowMultipleValues + ", includeInNavigationFilter=" + includeInProductListFilter
            + ", includeInSearchFilter=" + includeInSearchFilter + ", options="
            + options + "]";
    }
}
