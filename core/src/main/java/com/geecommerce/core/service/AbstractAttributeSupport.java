package com.geecommerce.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.geecommerce.core.App;
import com.geecommerce.core.Str;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.attribute.model.DefaultAttributeValue;
import com.geecommerce.core.system.attribute.repository.Attributes;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractAttributeSupport extends AbstractMultiContextModel implements AttributeSupport {
    private static final long serialVersionUID = 6493713269953753016L;

    @Inject
    protected App app;

    @Inject
    protected Attributes _attributes;

    @XmlElement(type = DefaultAttributeValue.class)
    @Column(AttributeSupportColumn.ATTRIBUTES)
    protected List<AttributeValue> attributes = new ArrayList<>();

    @Override
    public AttributeTargetObject targetObject() {
        Class<? extends AttributeSupport> modelInterface = Reflect.getModelInterface(this.getClass());

        if (modelInterface == null)
            throw new IllegalStateException("Unable to locate model interface for the object '"
                + this.getClass().getName()
                + "'. Therefore it is not possible to automatically create the attribute target object entry. Make sure that your model object has a matching interface, i.e. the object com.geecommerce.catalog.product.model.DefaultProduct must have the interface com.geecommerce.catalog.product.model.Product that extends the com.geecommerce.core.service.api.Model interface.");

        return app.service(AttributeService.class).getAttributeTargetObject(modelInterface, true);
    }

    @Override
    public Attribute getAttributeDefinition(Id attributeId) {
        return _attributes.findById(Attribute.class, attributeId);
    }

    @Override
    public Attribute getAttributeDefinition(String attributeCode) {
        return _attributes.havingCode(targetObject(), attributeCode);
    }

    @Override
    public List<AttributeValue> getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(List<AttributeValue> attributes) {
        this.attributes.clear();
        this.attributes.addAll(attributes);
    }

    @Override
    public AttributeSupport addAttribute(AttributeValue attribute) {
        this.attributes.add(attribute);
        return this;
    }

    @Override
    public AttributeSupport addAttribute(String attributeCode, ContextObject<?> value) {
        if (value == null || value.size() == 0)
            return this;

        this.attributes
            .add(app.model(AttributeValue.class).forAttribute(targetObject(), attributeCode).setValue(value));
        return this;
    }

    @Override
    public AttributeSupport addAttribute(String attributeCode, Object value) {
        this.attributes
            .add(app.model(AttributeValue.class).forAttribute(targetObject(), attributeCode).setSimpleValue(value));
        return this;
    }

    @Override
    public AttributeSupport addAttribute(String attributeCode, String language, String value) {
        this.attributes.add(
            app.model(AttributeValue.class).forAttribute(targetObject(), attributeCode).setValue(language, value));
        return this;
    }

    @Override
    public AttributeSupport addAttribute(String attributeCode, Object value, Store store) {
        this.attributes.add(app.model(AttributeValue.class).forAttribute(targetObject(), attributeCode)
            .setStoreValue(value, store));
        return this;
    }

    @Override
    public AttributeSupport addAttribute(String attributeCode, Id optionId) {
        this.attributes
            .add(app.model(AttributeValue.class).forAttribute(targetObject(), attributeCode).addOptionId(optionId));
        return this;
    }

    @Override
    public AttributeSupport addAttribute(String attributeCode, List<Id> optionIds) {
        this.attributes.add(
            app.model(AttributeValue.class).forAttribute(targetObject(), attributeCode).setOptionIds(optionIds));
        return this;
    }

    @Override
    public AttributeSupport addXOptionAttribute(String attributeCode, ContextObject<List<Id>> xOptionIds) {
        this.attributes.add(
            app.model(AttributeValue.class).forAttribute(targetObject(), attributeCode).setXOptionIds(xOptionIds));
        return this;
    }

    @Override
    public AttributeSupport addAttributeUsingCode2(String attributeCode2, Object value) {
        this.attributes.add(app.model(AttributeValue.class).forAttributeWithCode2(targetObject(), attributeCode2)
            .setSimpleValue(value));
        return this;
    }

    @Override
    public AttributeSupport addAttributeUsingCode2(String attributeCode2, Id optionId) {
        this.attributes.add(app.model(AttributeValue.class).forAttributeWithCode2(targetObject(), attributeCode2)
            .addOptionId(optionId));
        return this;
    }

    @Override
    public AttributeSupport setAttribute(String attributeCode, Object value) {
        if (hasAttribute(attributeCode)) {
            AttributeValue attrValue = getAttribute(attributeCode);

            if (value instanceof ContextObject) {
                attrValue.setValue((ContextObject<?>) value);
            } else {
                attrValue.setSimpleValue(value);
            }
        } else {
            addAttribute(attributeCode, value);
        }

        return this;
    }

    @Override
    public AttributeSupport setAttribute(String attributeCode, String language, String value) {
        if (hasAttribute(attributeCode)) {
            AttributeValue attrValue = getAttribute(attributeCode);
            ContextObject<String> ctxObj = attrValue.getValue();

            if (ctxObj != null) {
                ctxObj.addOrUpdate(language, value);
            } else {
                attrValue.setValue(new ContextObject<>(language, value));
            }

        } else {
            addAttribute(attributeCode, language, value);
        }

        return this;
    }

    @Override
    public AttributeSupport setAttribute(String attributeCode, Object value, Store store) {
        if (hasAttribute(attributeCode)) {
            AttributeValue attrValue = getAttribute(attributeCode);

            if (value instanceof ContextObject) {
                attrValue.setValue((ContextObject<?>) value);
            } else {
                attrValue.setStoreValue(value, store);
            }
        } else {
            addAttribute(attributeCode, value, store);
        }

        return this;
    }

    @Override
    public AttributeSupport setAttribute(String attributeCode, Id optionId) {
        if (hasAttribute(attributeCode)) {
            AttributeValue attrValue = getAttribute(attributeCode);
            attrValue.setOptionId(optionId);
        } else {
            addAttribute(attributeCode, optionId);
        }

        return this;
    }

    @Override
    public AttributeSupport setAttribute(String attributeCode, List<Id> optionIds) {
        if (hasAttribute(attributeCode)) {
            AttributeValue attrValue = getAttribute(attributeCode);
            attrValue.setOptionIds(optionIds);
        } else {
            addAttribute(attributeCode, optionIds);
        }

        return this;
    }

    @Override
    public AttributeSupport putAttributes(Map<String, ContextObject<?>> attributesMap) {
        if (attributesMap == null || attributesMap.size() == 0)
            return this;

        Set<String> attributeCodes = attributesMap.keySet();

        for (String code : attributeCodes) {
            ContextObject<?> newValue = attributesMap.get(code);

            if (hasAttribute(code)) {
                AttributeValue attrValue = getAttribute(code);
                attrValue.setValue(newValue);
            } else {
                addAttribute(code, newValue);
            }
        }

        return this;
    }

    @Override
    public AttributeSupport setOptionAttributes(Map<String, List<Id>> attributesMap) {
        if (attributesMap == null || attributesMap.size() == 0)
            return this;

        Set<String> attributeCodes = attributesMap.keySet();

        for (String code : attributeCodes) {
            List<Id> newOptionIds = attributesMap.get(code);

            if (hasAttribute(code)) {
                AttributeValue attrValue = getAttribute(code);
                List<Id> currentOptionIds = attrValue.getOptionIds();

                // Options have been reset.
                if ((newOptionIds == null || newOptionIds.size() == 0) && currentOptionIds != null
                    && !currentOptionIds.isEmpty()) {
                    removeAttribute(code);
                } else {
                    attrValue.setOptionIds(newOptionIds);
                }
            } else if (newOptionIds != null && !newOptionIds.isEmpty()) {
                addAttribute(code, newOptionIds);
            }
        }

        return this;
    }

    @Override
    public AttributeSupport setXOptionAttributes(Map<String, ContextObject<List<Id>>> attributesMap) {
        if (attributesMap == null || attributesMap.size() == 0)
            return this;

        Set<String> attributeCodes = attributesMap.keySet();

        for (String code : attributeCodes) {
            ContextObject<List<Id>> newXOptionIds = attributesMap.get(code);

            if (hasAttribute(code)) {
                AttributeValue attrValue = getAttribute(code);
                attrValue.setXOptionIds(newXOptionIds);
            } else {
                addXOptionAttribute(code, newXOptionIds);
            }
        }

        return this;
    }

    @Override
    public AttributeSupport addXOptionAttribute(String attributeCode, Id optionId, Store store) {
        this.attributes.add(app.model(AttributeValue.class).forAttribute(targetObject(), attributeCode)
            .addXOptionId(optionId, store));
        return this;
    }

    @Override
    public AttributeSupport addXOptionAttribute(String attributeCode, List<Id> optionIds, Store store) {
        this.attributes.add(app.model(AttributeValue.class).forAttribute(targetObject(), attributeCode)
            .addXOptionIds(optionIds, store));
        return this;
    }

    @Override
    public AttributeSupport setXOptionAttribute(String attributeCode, Id optionId, Store store) {
        if (hasAttribute(attributeCode)) {
            AttributeValue attrValue = getAttribute(attributeCode);
            attrValue.setXOptionId(optionId, store);
        } else {
            addXOptionAttribute(attributeCode, optionId, store);
        }

        return this;
    }

    @Override
    public AttributeSupport setXOptionAttribute(String attributeCode, List<Id> optionIds, Store store) {
        if (hasAttribute(attributeCode)) {
            AttributeValue attrValue = getAttribute(attributeCode);
            attrValue.setXOptionIds(optionIds, store);
        } else {
            addXOptionAttribute(attributeCode, optionIds, store);
        }

        return this;
    }

    @Override
    public AttributeSupport setOptOuts(Map<String, ContextObject<Boolean>> optOutMap) {
        if (optOutMap == null || optOutMap.size() == 0)
            return this;

        Set<String> attributeCodes = optOutMap.keySet();

        for (String code : attributeCodes) {
            ContextObject<Boolean> optOut = optOutMap.get(code);

            setOptOut(code, optOut);
        }

        return this;
    }

    @Override
    public AttributeSupport setOptOut(String attributeCode, ContextObject<Boolean> optOut) {
        boolean isOptOut = false;

        if (optOut != null)
            isOptOut = optOut.getBoolean();

        if (hasAttribute(attributeCode)) {
            if (!isOptOut && optOut.size() == 1 && isAttributeEmpty(attributeCode)) {
                removeAttribute(attributeCode);
            } else {
                AttributeValue attrValue = getAttribute(attributeCode);
                attrValue.setOptOut(optOut);
            }
        } else {
            if (isOptOut)
                this.attributes.add(
                    app.model(AttributeValue.class).forAttribute(targetObject(), attributeCode).setOptOut(optOut));
        }

        return this;
    }

    @Override
    public boolean hasAttribute(Id attributeId) {
        return getAttribute(attributeId) != null;
    }

    @Override
    public boolean hasAttribute(String attributeCode) {
        return getAttribute(attributeCode) != null;
    }

    @Override
    public boolean hasAttributeValue(String attributeCode, String language) {
        AttributeValue av = getAttribute(attributeCode);

        if (av == null)
            return false;

        return !Str.isEmpty(av.getString(language));
    }

    @Override
    public boolean isAttributeOptedOut(Id attributeId) {
        AttributeValue av = getAttribute(attributeId);

        if (av == null)
            return false;

        return av.isOptOut();
    }

    @Override
    public boolean isAttributeOptedOut(String attributeCode) {
        AttributeValue av = getAttribute(attributeCode);

        if (av == null)
            return false;

        return av.isOptOut();
    }

    @Override
    public boolean isAttributeEmpty(Id attributeId) {
        return isAttributeEmpty(attributeId, null);
    }

    @Override
    public boolean isAttributeEmpty(Id attributeId, Store store) {
        AttributeValue av = getAttribute(attributeId);

        if (av == null)
            return true;

        // If attribute options exist, there is no need to do any further
        // checking because
        // obviously this attribute has a value.
        if (av.getOptionIds() != null && av.getOptionIds().size() > 0)
            return false;

        if (av.getXOptionIds() != null && av.getXOptionIds().size() > 0 && store == null)
            return false;

        ContextObject<?> val = av.getValue();

        // Check the contents of the ContextObject.
        if (val != null && !val.isEmpty()) {
            Attribute attr = av.getAttribute();

            // No need to check for language value as it is not an i18n
            // attribute.
            if (!attr.isI18n())
                return false;

            String lang = null;

            if (store != null)
                lang = store.getDefaultLanguage();

            if (lang == null)
                lang = app.getDefaultLanguage();

            // No language to check.
            if (lang == null)
                return false;

            Object i18nVal = val.getValueFor(lang);

            if (i18nVal != null && i18nVal instanceof String && !"".equals(((String) i18nVal).trim())) {
                return false;
            } else if (i18nVal != null && !(i18nVal instanceof String)) {
                return false;
            } else {
                System.out.println(attr.getCode() + " HAS NO VALUE FOR LANG::: " + lang + " !!! ");
            }
        }

        ContextObject<List<Id>> xOptions = av.getXOptionIds();

        if (store != null && xOptions != null) {
            if (!xOptions.isEmpty()) {
                List<Id> xOptionIds = xOptions.getValueForStore(store.getId());
                if (xOptionIds != null && !xOptionIds.isEmpty())
                    return false;
            }
        }

        // No value and no option found.
        return true;
    }

    @Override
    public boolean isAttributeEmpty(String attributeCode) {
        AttributeValue av = getAttribute(attributeCode);
        return av == null ? true : isAttributeEmpty(av.getAttributeId(), null);
    }

    @Override
    public boolean isAttributeEmpty(String attributeCode, Store store) {
        AttributeValue av = getAttribute(attributeCode);
        return av == null ? true : isAttributeEmpty(av.getAttributeId(), store);
    }

    @Override
    public AttributeValue attr(Id attributeId) {
        return getAttribute(attributeId);
    }

    @Override
    public AttributeValue getAttribute(Id attributeId) {
        AttributeValue foundAttribute = null;

        for (AttributeValue attrValue : attributes) {
            if (attributeId.equals(attrValue.getAttributeId()) && attrValue.attributeExists()) {
                foundAttribute = attrValue;
                break;
            }
        }

        return foundAttribute;
    }

    @Override
    public AttributeValue attr(String attributeCode) {
        return getAttribute(attributeCode);
    }

    @Override
    public AttributeValue getAttribute(String attributeCode) {
        return getAttribute(attributeCode, false, null);
    }

    @Override
    public AttributeValue attr(String attributeCode, boolean allowParentLookup) {
        return getAttribute(attributeCode, allowParentLookup);
    }

    @Override
    public AttributeValue getAttribute(String attributeCode, boolean allowParentLookup) {
        return getAttribute(attributeCode, allowParentLookup, null);
    }

    @Override
    public AttributeValue attr(String attributeCode, boolean allowParentLookup, ChildSupport.Lookup allowChildLookup) {
        return getAttribute(attributeCode, allowParentLookup, allowChildLookup);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public AttributeValue getAttribute(String attributeCode, boolean allowParentLookup,
        ChildSupport.Lookup allowChildLookup) {
        // long start = System.currentTimeMillis();

        AttributeValue foundAttribute = null;

        // System.out.print(attributeCode + "___ SELF:");

        for (AttributeValue av : attributes) {
            if (av.getAttribute() != null && attributeCode.equals(av.getAttribute().getCode())) {
                foundAttribute = av;
                break;
            }
        }

        // System.out.print(System.currentTimeMillis()-start);

        // Try parent if allowed and the current object does not have a value.
        if (foundAttribute == null && allowParentLookup) {
            // System.out.print(", P:");

            AttributeSupport parent = (AttributeSupport) ((ParentSupport) this).getParent();

            if (parent != null)
                foundAttribute = parent.getAttribute(attributeCode);

            // System.out.print(System.currentTimeMillis()-start);
        }

        // If there is still no value and child-lookup is allowed, try that too.
        if (foundAttribute == null && allowChildLookup != null && allowChildLookup != ChildSupport.Lookup.NONE
            && this instanceof ChildSupport) {
            List<AttributeSupport> children = ((ChildSupport) this).getChildren();

            if (children != null && children.size() > 0) {
                if (ChildSupport.Lookup.FIRST == allowChildLookup) {
                    // System.out.print(", C:");

                    AttributeSupport child = children.get(0);
                    foundAttribute = child.getAttribute(attributeCode);

                    // System.out.print(System.currentTimeMillis()-start);
                } else if (ChildSupport.Lookup.ANY == allowChildLookup) {
                    for (AttributeSupport child : children) {
                        // System.out.print(", C:");

                        foundAttribute = child.getAttribute(attributeCode);

                        // System.out.print(System.currentTimeMillis()-start);

                        if (foundAttribute != null)
                            break;
                    }
                }
            }
        }

        // if(allowChildLookup != ChildSupport.Lookup.NONE)
        // System.out.println("... TOTAL: " +
        // (System.currentTimeMillis()-start));

        return foundAttribute;
    }

    @Override
    public AttributeValue getAttribute(Id attributeId, boolean allowParentLookup) {
        return getAttribute(attributeId, allowParentLookup, null);
    }

    @Override
    public AttributeValue attr(Id attributeId, boolean allowParentLookup, ChildSupport.Lookup allowChildLookup) {
        return getAttribute(attributeId, allowParentLookup, allowChildLookup);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public AttributeValue getAttribute(Id attributeId, boolean allowParentLookup,
        ChildSupport.Lookup allowChildLookup) {
        // long start = System.currentTimeMillis();

        AttributeValue foundAttribute = null;

        // System.out.print(attributeCode + "___ SELF:");

        for (AttributeValue av : attributes) {
            if (av.getAttribute() != null && attributeId.equals(av.getAttribute().getId())) {
                foundAttribute = av;
                break;
            }
        }

        // System.out.print(System.currentTimeMillis()-start);

        // Try parent if allowed and the current object does not have a value.
        if (foundAttribute == null && allowParentLookup) {
            // System.out.print(", P:");

            AttributeSupport parent = (AttributeSupport) ((ParentSupport) this).getParent();

            if (parent != null)
                foundAttribute = parent.getAttribute(attributeId);

            // System.out.print(System.currentTimeMillis()-start);
        }

        // If there is still no value and child-lookup is allowed, try that too.
        if (foundAttribute == null && allowChildLookup != null && allowChildLookup != ChildSupport.Lookup.NONE
            && this instanceof ChildSupport) {
            List<AttributeSupport> children = ((ChildSupport) this).getChildren();

            if (children != null && children.size() > 0) {
                if (ChildSupport.Lookup.FIRST == allowChildLookup) {
                    // System.out.print(", C:");

                    AttributeSupport child = children.get(0);
                    foundAttribute = child.getAttribute(attributeId);

                    // System.out.print(System.currentTimeMillis()-start);
                } else if (ChildSupport.Lookup.ANY == allowChildLookup) {
                    for (AttributeSupport child : children) {
                        // System.out.print(", C:");

                        foundAttribute = child.getAttribute(attributeId);

                        // System.out.print(System.currentTimeMillis()-start);

                        if (foundAttribute != null)
                            break;
                    }
                }
            }
        }

        // if(allowChildLookup != ChildSupport.Lookup.NONE)
        // System.out.println("... TOTAL: " +
        // (System.currentTimeMillis()-start));

        return foundAttribute;
    }

    @Override
    public boolean attributeValueEquals(String attributeCode, Object value) {
        return attributeValueEquals(attributeCode, value, false);
    }

    @Override
    public boolean attributeValueEquals(String attributeCode, Object value, boolean allowParentLookup) {
        return attributeValueEquals(attributeCode, value, allowParentLookup, null);
    }

    @Override
    public boolean attributeValueEquals(String attributeCode, Object value, boolean allowParentLookup,
        ChildSupport.Lookup allowChildLookup) {
        AttributeValue attrVal = getAttribute(attributeCode, allowParentLookup, allowChildLookup);

        if ((attrVal == null || attrVal.getVal() == null) && value == null)
            return true;

        if (attrVal != null && attrVal.getVal() != null) {
            return value.equals(attrVal.getVal());
        }

        return false;
    }

    @Override
    public AttributeSupport removeAttribute(Id attributeId) {
        int removeIndex = -1;

        for (int i = 0; i < attributes.size(); i++) {
            if (attributeId.equals(attributes.get(i).getAttributeId())) {
                removeIndex = i;
                break;
            }
        }

        if (removeIndex != -1)
            attributes.remove(removeIndex);

        return this;
    }

    @Override
    public AttributeSupport removeAttribute(String attributeCode) {
        int removeIndex = -1;

        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).getAttribute() != null
                && attributeCode.equals(attributes.get(i).getAttribute().getCode())) {
                removeIndex = i;
                break;
            }
        }

        if (removeIndex != -1)
            attributes.remove(removeIndex);

        return this;
    }

    @Override
    public boolean hasAttributeWithOption(Id attributeId, Id attributeOptionId) {
        return getAttributeHavingOption(attributeId, attributeOptionId) != null;
    }

    @Override
    public boolean hasAttributeWithOption(String attributeCode, Id attributeOptionId) {
        return getAttributeHavingOption(attributeCode, attributeOptionId) != null;
    }

    @Override
    public AttributeValue getAttributeHavingOption(Id attributeId, Id attributeOptionId) {
        AttributeValue attribute = getAttribute(attributeId);

        if (attribute != null && attribute.getOptionIds().contains(attributeOptionId)) {
            return attribute;
        } else {
            return null;
        }
    }

    @Override
    public AttributeValue getAttributeHavingOption(String attributeCode, Id attributeOptionId) {
        AttributeValue attribute = getAttribute(attributeCode);

        if (attribute != null && attribute.getOptionIds().contains(attributeOptionId)) {
            return attribute;
        } else {
            return null;
        }
    }

    @Override
    public List<AttributeValue> getAttributesHavingProperty(String key) {
        List<AttributeValue> attributeValues = new ArrayList<>();

        for (AttributeValue attrValue : attributes) {
            if (attrValue.hasProperty(key) && attrValue.attributeExists()) {
                attributeValues.add(attrValue);
            }
        }

        return attributeValues;
    }

    @Override
    public List<AttributeValue> getAttributesHavingPrefix(String attributeCodePrefix) {
        List<AttributeValue> attributeValues = new ArrayList<>();

        if (Str.isEmpty(attributeCodePrefix))
            return attributeValues;

        for (AttributeValue attrValue : attributes) {
            Attribute attr = attrValue.getAttribute();

            if (attr != null && attr.getCode().startsWith(attributeCodePrefix)) {
                attributeValues.add(attrValue);
            }
        }

        return attributeValues;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);

        if (map == null || map.size() == 0)
            return;

        List<Map> attributesList = (List<Map>) map.get(AttributeSupportColumn.ATTRIBUTES);

        if (attributesList != null && !attributesList.isEmpty()) {
            attributes.clear();

            for (Map m : attributesList) {
                AttributeValue attribute = app.model(AttributeValue.class);
                attribute.fromMap(m);

                attributes.add(attribute);
            }
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        ArrayList<Map<String, Object>> attributes = new ArrayList<>();

        for (AttributeValue attrVal : getAttributes()) {
            attributes.add(attrVal.toMap());
        }

        map.put(AttributeSupportColumn.ATTRIBUTES, attributes);

        return map;
    }

    protected List<AttributeValue> copyOfAttributes() {
        List<AttributeValue> copyOfAttributes = new ArrayList<>();

        for (AttributeValue attributeValue : attributes) {
            copyOfAttributes.add(attributeValue.copy());
        }

        return copyOfAttributes;
    }
}
