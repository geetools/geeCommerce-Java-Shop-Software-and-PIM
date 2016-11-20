package com.geecommerce.core.system.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Cacheable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "currency")
@Model(collection = "_currencies", fieldAccess = true)
public class DefaultCurrency extends AbstractModel implements Currency {
    private static final long serialVersionUID = -914090522500917209L;

    @Column(GlobalColumn.ID)
    private Id id = null;

    @Column(Col.CODE)
    private String code = null;

    @Column(Col.NAME)
    private String name = null;

    @Column(Col.SYMBOL)
    private String symbol = null;

    @Column(Col.COUNTRY)
    private String country = null;

    @Inject
    public DefaultCurrency() {

    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return "DefaultCurrency [id=" + id + ", code=" + code + ", name=" + name + ", symbol=" + symbol + ", country="
            + country + "]";
    }

}
