package com.geecommerce.core.rest.jersey.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.service.annotation.Profile;

/**
 * XmlAdapter for converting XML to an Update object and vice-versa. The
 * following XML-example is valid for deserialization:
 * 
 * Update the forename and surname of a user: <code>
 *   <update>
 *     <fields>
 *       <forename>Some name</forename>
 *       <surname>Some name</surname>
 *     </fields>
 *   </update>
 * </code>
 */
@Profile
public class UpdateAdapter extends XmlAdapter<UpdateAdapter.MappableBean, Update.UpdateMap> {
    public static class MappableBean {
        @XmlElement(name = "field")
        public List<Field> fields = new ArrayList<Field>();

        @Override
        public String toString() {
            return "MappableBean [fields=" + fields + "]";
        }

    }

    public static class Field {
        @XmlAttribute(name = "name")
        public String name;

        @XmlValue
        public String value;

        @Override
        public String toString() {
            return "Field [name=" + name + ", value=" + value + "]";
        }
    }

    @Override
    public UpdateAdapter.MappableBean marshal(Update.UpdateMap updateMap) throws Exception {
        UpdateAdapter.MappableBean bean = new UpdateAdapter.MappableBean();

        if (updateMap != null && updateMap.size() > 0) {
            Set<String> keys = updateMap.keySet();

            for (String key : keys) {
                Field f = new Field();
                f.name = key;
                f.value = String.valueOf(updateMap.get(key));

                if (f.name != null && f.value != null) {
                    bean.fields.add(f);
                }
            }
        }

        return bean;
    }

    @Override
    public Update.UpdateMap unmarshal(UpdateAdapter.MappableBean updateBean) throws Exception {
        Map<String, Object> fields = new HashMap<>();

        if (updateBean != null) {
            if (updateBean.fields.size() > 0) {
                for (Field field : updateBean.fields) {
                    if (field.name != null && field.value != null) {
                        fields.put(field.name, field.value);
                    }
                }
            }
        }

        return new Update.UpdateMap(fields);
    }
}
