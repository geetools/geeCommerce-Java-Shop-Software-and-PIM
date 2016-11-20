package com.geecommerce.core.rest.jersey.adapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

/**
 * XmlAdapter for converting XML to a ContectObject and vice-versa. The
 * following XML-examples are valid for deserialization (using a name value as
 * an example).
 * 
 * The most basic form simply creates a global context-object with the value
 * "Some name": <code>
 *   <name>Some name</name>
 * </code>
 * 
 * The next most basic form is for values where we only have 1 context-value
 * (e.g. for a specific language): <code>
 *   <name l="en">Some name in English</name>
 * </code>
 * 
 * Or if we wanted it to be specific to a particular merchant: <code>
 *   <name m="12345" l=
"en">Some name in English for a particular merchant</name>
 * </code>
 * 
 * If we need to have a different value for more than one context it would look
 * like this: <code>
 *   <name>
 *     <!-- Global value -->
 *     <value>Some name</value>
 *     <!-- In English -->
 *     <value l="en">Some name in English</value>
 *     <!-- Global value -->
 *     <value m="12345" l=
"en">Some name in English for a particular merchant</value>
 *   </name>
 * </code>
 */
@Profile
public class ContextObjectAdapter extends XmlAdapter<ContextObjectAdapter.MappableBean, ContextObject<?>> {
    /**
     * The "real" ContextObject consisting of a list of maps cannot be easily
     * mapped with JAXB. We are therefore using this class as an intermediary
     * step in the mapping process between XML and ContextObject.
     */
    public static class MappableBean {
        @XmlElement(name = "value")
        public List<Value> values = new ArrayList<Value>();

        @XmlMixed
        public List<String> value;

        @XmlAttribute(name = "m")
        public String merchantId;

        @XmlAttribute(name = "s")
        public String storeId;

        @XmlAttribute(name = "l")
        public String language;

        @XmlAttribute(name = "c")
        public String country;

        @XmlAttribute(name = "v")
        public String viewId;
    }

    public static class Value {
        @XmlValue
        public String value;

        @XmlAttribute(name = "m")
        public String merchantId;

        @XmlAttribute(name = "s")
        public String storeId;

        @XmlAttribute(name = "l")
        public String language;

        @XmlAttribute(name = "c")
        public String country;

        @XmlAttribute(name = "v")
        public String viewId;
    }

    @Override
    public ContextObjectAdapter.MappableBean marshal(ContextObject<?> ctxObj) throws Exception {
        ContextObjectAdapter.MappableBean bean = null;

        if (ctxObj != null && ctxObj.size() > 0) {
            bean = new ContextObjectAdapter.MappableBean();

            if (ctxObj != null && ctxObj.size() > 0) {
                for (Map<String, Object> ctxMap : ctxObj) {
                    Value v = new Value();
                    if (ctxMap.get(ContextObject.MERCHANT) != null)
                        v.merchantId = String.valueOf(ctxMap.get(ContextObject.MERCHANT));

                    if (ctxMap.get(ContextObject.STORE) != null)
                        v.storeId = String.valueOf(ctxMap.get(ContextObject.STORE));

                    if (ctxMap.get(ContextObject.LANGUAGE) != null)
                        v.language = String.valueOf(ctxMap.get(ContextObject.LANGUAGE));

                    if (ctxMap.get(ContextObject.COUNTRY) != null)
                        v.country = String.valueOf(ctxMap.get(ContextObject.COUNTRY));

                    if (ctxMap.get(ContextObject.VIEW) != null)
                        v.viewId = String.valueOf(ctxMap.get(ContextObject.VIEW));

                    if (ctxMap.get(ContextObject.VALUE) != null) {
                        v.value = String.valueOf(ctxMap.get(ContextObject.VALUE));
                        bean.values.add(v);
                    }
                }
            }
        }

        return bean;
    }

    @Override
    public ContextObject<?> unmarshal(ContextObjectAdapter.MappableBean ctxObjBean) throws Exception {
        ContextObject<?> ctxObj = new ContextObject<>();

        if (ctxObjBean != null) {
            if (ctxObjBean.values.size() > 0) {
                for (Value val : ctxObjBean.values) {
                    Map<String, Object> entry = new LinkedHashMap<>();

                    if (val.merchantId != null && !"".equals(val.merchantId.trim()))
                        entry.put(ContextObject.MERCHANT, Id.valueOf(val.merchantId.trim()));

                    if (val.storeId != null && !"".equals(val.storeId.trim()))
                        entry.put(ContextObject.STORE, Id.valueOf(val.storeId.trim()));

                    if (val.language != null && !"".equals(val.language.trim()))
                        entry.put(ContextObject.LANGUAGE, val.language.trim());

                    if (val.country != null && !"".equals(val.country.trim()))
                        entry.put(ContextObject.COUNTRY, val.country.trim());

                    if (val.viewId != null && !"".equals(val.viewId.trim()))
                        entry.put(ContextObject.VIEW, Id.valueOf(val.viewId.trim()));

                    if (val.value != null && !"".equals(val.value.trim())) {
                        entry.put(ContextObject.VALUE, val.value.trim());
                        ctxObj.add(entry);
                    }
                }
            } else if (ctxObjBean.value != null && ctxObjBean.value.size() > 0) {
                Map<String, Object> entry = new LinkedHashMap<>();

                String value = ctxObjBean.value.get(0);

                if (ctxObjBean.merchantId != null && !"".equals(ctxObjBean.merchantId.trim()))
                    entry.put(ContextObject.MERCHANT, Id.valueOf(ctxObjBean.merchantId.trim()));

                if (ctxObjBean.storeId != null && !"".equals(ctxObjBean.storeId.trim()))
                    entry.put(ContextObject.STORE, Id.valueOf(ctxObjBean.storeId.trim()));

                if (ctxObjBean.language != null && !"".equals(ctxObjBean.language.trim()))
                    entry.put(ContextObject.LANGUAGE, ctxObjBean.language.trim());

                if (ctxObjBean.country != null && !"".equals(ctxObjBean.country.trim()))
                    entry.put(ContextObject.COUNTRY, ctxObjBean.country.trim());

                if (ctxObjBean.viewId != null && !"".equals(ctxObjBean.viewId.trim()))
                    entry.put(ContextObject.VIEW, Id.valueOf(ctxObjBean.viewId.trim()));

                if (value != null && !"".equals(value.trim())) {
                    entry.put(ContextObject.VALUE, value.trim());
                    ctxObj.add(entry);
                }
            }
        }

        return ctxObj;
    }
}
