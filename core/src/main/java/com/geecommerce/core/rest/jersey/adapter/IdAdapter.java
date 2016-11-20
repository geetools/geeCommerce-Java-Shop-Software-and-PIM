package com.geecommerce.core.rest.jersey.adapter;

import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.type.Id;

@Profile
public class IdAdapter extends XmlAdapter<IdAdapter.MappableId, Id> {
    public static class MappableId {
        @XmlValue
        public String id;
    }

    @Override
    public IdAdapter.MappableId marshal(Id id) throws Exception {
        IdAdapter.MappableId idBean = new IdAdapter.MappableId();

        if (id != null) {
            idBean.id = id.str();
            return idBean;
        } else {
            return null;
        }
    }

    @Override
    public Id unmarshal(IdAdapter.MappableId idBean) throws Exception {
        Id id = null;

        if (idBean != null) {
            id = Id.valueOf(idBean.id);
        }

        return id;
    }
}
