@XmlJavaTypeAdapters({ @XmlJavaTypeAdapter(value = IdAdapter.class, type = Id.class),
    @XmlJavaTypeAdapter(value = ContextObjectAdapter.class, type = ContextObject.class),
    @XmlJavaTypeAdapter(value = UpdateAdapter.class, type = UpdateMap.class) })
package com.geecommerce;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import com.geecommerce.core.rest.jersey.adapter.ContextObjectAdapter;
import com.geecommerce.core.rest.jersey.adapter.IdAdapter;
import com.geecommerce.core.rest.jersey.adapter.UpdateAdapter;
import com.geecommerce.core.rest.pojo.Update.UpdateMap;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
