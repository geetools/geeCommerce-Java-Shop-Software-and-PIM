package com.geecommerce.core.system.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

import java.util.Map;

@Cacheable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "country")
@Model(collection = "_countries", fieldAccess = true)
public class DefaultCountry extends AbstractModel implements Country {
    private static final long serialVersionUID = -1666220405724395473L;

    @Column(GlobalColumn.ID)
    private Id id = null;

    @Column(Col.CODE)
    private String code = null;

    @Column(Col.CODE3)
    private String code3 = null;

    @Column(Col.NAME)
    private ContextObject<String> name = null;

    private Double latitude = null;
    private Double longitude = null;

    @Column(Col.CURRENCY)
    private String currency = null;

    @Column(Col.TIMEZONE)
    private String timezone = null;

    @Column(Col.PHONE_CODE)
    private String phoneCode = null;

    @Inject
    public DefaultCountry() {
    }

    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        try {
            this.latitude = double_(map.get(Col.LATITUDE));
            this.longitude = double_(map.get(Col.LONGITUDE));
        } catch (NumberFormatException exc) {
            exc.printStackTrace();
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Col.LATITUDE, getLatitude());
        map.put(Col.LONGITUDE, getLongitude());
        return map;
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
    public String getCode3() {
        return code3;
    }

    @Override
    public ContextObject<String> getName() {
        return name;
    }

    @Override
    public Double getLatitude() {
        return latitude;
    }

    @Override
    public Double getLongitude() {
        return longitude;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public String getTimezone() {
        return timezone;
    }

    @Override
    public String getPhoneCode() {
        return phoneCode;
    }

    @Override
    public String toString() {
        return "DefaultCountry [id=" + id + ", code=" + code + ", code3=" + code3 + ", name=" + name + ", latitude=" + latitude + ", longitude=" + longitude + ", currency=" + currency + ", timezone="
                + timezone + ", phoneCode=" + phoneCode + "]";
    }
}
