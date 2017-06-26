package com.geecommerce.catalog.product.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.system.query.model.QueryNode;
import org.apache.logging.log4j.util.Strings;

import com.geecommerce.catalog.product.repository.ProductListFilterRules;
import com.geecommerce.core.service.AbstractAttributeSupport;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.PageSupport;
import com.geecommerce.core.service.TargetSupport;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.ContextObjects;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.owlike.genson.annotation.JsonIgnore;

@Cacheable
@Model(collection = "product_lists", history = true)
public class DefaultProductList extends AbstractAttributeSupport
    implements ProductList, AttributeSupport, TargetSupport, PageSupport {
    private static final long serialVersionUID = -6927276841061370646L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.ID2)
    private Id id2 = null;

    @Column(Col.KEY)
    private String key = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Column(Col.QUERY)
    private String query = null;

    @Column(Col.FILTER_RULE_ID)
    private Id filterRuleId = null;

    @Column(Col.ENABLED)
    private boolean enabled = true;

    @Column(Col.SALE)
    private boolean sale = false;

    @Column(Col.SPECIAL)
    private boolean special = false;

    @Column(Col.QUERY_NODE)
    public QueryNode queryNode;

    @Column(Col.FILTER_ATTRIBUTES)
    public List<Id> filterAttributeIds = new ArrayList<>();

    public List<Attribute> filterAttributes;

    // Loaded on demand
    private ContextObject<String> uri = null;
    private ProductListFilterRule filterRule = null;

    // Repositories
    private final ProductListFilterRules filterRules;
    private final UrlRewrites urlRewrites;

    private final AttributeService attributeService;

    @Inject
    public DefaultProductList(ProductListFilterRules filterRules, UrlRewrites urlRewrites,
        AttributeService attributeService) {
        this.filterRules = filterRules;
        this.urlRewrites = urlRewrites;
        this.attributeService = attributeService;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public ProductList setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getId2() {
        return id2;
    }

    @Override
    public ProductList setId2(Id id2) {
        this.id2 = id2;
        return this;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public ProductList setKey(String key) {
        this.key = key;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public ProductList setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public QueryNode getQueryNode() {
        return queryNode;
    }

    @Override
    public ProductList setQueryNode(QueryNode productListQueryNode) {
        this.queryNode = productListQueryNode;
        return this;
    }

    @Override
    public ProductList setQuery(String query) {
        this.query = query;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public ProductList setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public boolean isSale() {
        return sale;
    }

    @Override
    public ProductList setSale(boolean sale) {
        this.sale = sale;
        return this;
    }

    @Override
    public boolean isSpecial() {
        return special;
    }

    @Override
    public ProductList setSpecial(boolean special) {
        this.special = special;
        return this;
    }

    @JsonIgnore
    @Override
    public ContextObject<String> getURI() {
        if (uri == null) {
            UrlRewrite urlRewrite = urlRewrites.forProductList(getId());

            if (urlRewrite != null)
                uri = urlRewrite.getRequestURI();

            if (uri == null)
                uri = new ContextObject<String>();

            if (!uri.hasGlobalEntry())
                uri.addOrUpdateGlobal("/catalog/product-list/view/" + getId());
        }

        return uri;
    }

    @Override
    public AttributeValue attr(String attributeCode) {
        return getAttribute(attributeCode);
    }

    @Override
    public boolean hasAttribute(String attributeCode) {
        return getAttribute(attributeCode) != null;
    }

    @Override
    public Id getFilterRuleId() {
        return this.filterRuleId;
    }

    @Override
    public ProductList setFilterRuleId(Id filterRuleId) {
        this.filterRuleId = filterRuleId;
        return this;
    }

    @Override
    public ProductListFilterRule getFilterRule() {
        if (filterRuleId != null && filterRule == null) {
            filterRule = filterRules.findById(ProductListFilterRule.class, filterRuleId);
        }

        return filterRule;
    }

    @Override
    public ProductList setFilterRule(ProductListFilterRule filterRule) {
        this.filterRule = filterRule;
        return this;
    }

    @Override
    @JsonIgnore
    public List<Attribute> getFilterAttributes() {
        if (filterAttributes == null && filterAttributeIds != null && filterAttributeIds.size() > 0) {
            filterAttributes = attributeService.getAttributes(filterAttributeIds.toArray(new Id[0]));
        }
        return filterAttributes;
    }

    @Override
    public List<Id> getFilterAttributeIds() {
        return filterAttributeIds;
    }

    @Override
    public ProductList setFilterAttributeIds(List<Id> ids) {
        this.filterAttributeIds = ids;
        return this;
    }

    // --------------------------------------------------------------------------------
    // Page support methods
    // --------------------------------------------------------------------------------

    @Override
    public ContextObject<String> getTitle() {
        AttributeValue metaTitle = getAttribute("meta_title");
        ContextObject<String> title = getLabel();

        if (metaTitle != null && Strings.isNotEmpty(ContextObjects.findCurrentLanguageOrGlobal(metaTitle.getValue()))) {
            return metaTitle.getValue();
        } else if (title != null && Strings.isNotEmpty(ContextObjects.findCurrentLanguageOrGlobal(title))) {
            return title;
        } else {
            return null;
        }
    }

    @Override
    public ContextObject<String> getMetaDescription() {
        AttributeValue metaDescription = getAttribute("meta_description");
        AttributeValue description = getAttribute("description");

        if (metaDescription != null
            && Strings.isNotEmpty(ContextObjects.findCurrentLanguageOrGlobal(metaDescription.getValue()))) {
            return metaDescription.getValue();
        } else if (description != null
            && Strings.isNotEmpty(ContextObjects.findCurrentLanguageOrGlobal(description.getValue()))) {
            return description.getValue();
        } else {
            return null;
        }
    }

    @Override
    public ContextObject<String> getMetaRobots() {
        AttributeValue metaRobots = getAttribute("meta_robots");

        if (metaRobots != null && metaRobots.getFirstAttributeOption() != null) {
            return metaRobots.getFirstAttributeOption().getLabel();
        } else if (getCanonicalURI() != null) {
            return ContextObjects.global("noindex,follow");
        } else {
            return null;
        }
    }

    @Override
    public ContextObject<String> getCanonicalURI() {
        UrlRewrite urlRewrite = urlRewrites.forProductList(getId());

        if (urlRewrite != null) {
            String rewriteURI = ContextObjects.findCurrentLanguageOrGlobal(urlRewrite.getRequestURI());
            String requestURI = app.getOriginalURI();

            if (rewriteURI != null && !requestURI.equals(rewriteURI)) {
                return urlRewrite.getRequestURI();
            }
        }

        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Col.ID));
        this.id2 = id_(map.get(Col.ID2));
        this.key = str_(map.get(Col.KEY));
        this.label = ctxObj_(map.get(Col.LABEL));
        this.query = str_(map.get(Col.QUERY));
        this.filterRuleId = id_(map.get(Col.FILTER_RULE_ID));
        this.enabled = map.get(Col.ENABLED) == null ? true : bool_(map.get(Col.ENABLED));
        this.sale = bool_(map.get(Col.SALE), false);
        this.filterAttributeIds = idList_(map.get(Col.FILTER_ATTRIBUTES));

        if (this.filterAttributeIds != null)
            while (this.filterAttributeIds.remove(null))
                ;

        Map<String, Object> queryNodeMap = map_(map.get(Col.QUERY_NODE));
        if (queryNodeMap != null && queryNodeMap.size() > 0) {
            this.queryNode = app.model(QueryNode.class);
            this.queryNode.fromMap(queryNodeMap);
        }

    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Col.ID, getId());

        if (getId2() != null)
            map.put(Col.ID2, getId2());

        map.put(Col.KEY, getKey());
        map.put(Col.LABEL, getLabel());

        if (getQuery() != null)
            map.put(Col.QUERY, getQuery());

        if (getFilterRuleId() != null)
            map.put(Col.FILTER_RULE_ID, getFilterRuleId());

        map.put(Col.ENABLED, isEnabled());

        map.put(Col.SALE, isSale());

        if (getQueryNode() != null) {
            map.put(Col.QUERY_NODE, getQueryNode().toMap());
        }

        return map;
    }
}
