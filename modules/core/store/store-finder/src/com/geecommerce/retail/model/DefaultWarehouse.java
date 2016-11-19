package com.geecommerce.retail.model;

import com.google.common.collect.Maps;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import static com.geecommerce.retail.model.Warehouse.Column.*;

import java.util.*;

@Model("zip_warehouse_mapping")
public class DefaultWarehouse extends AbstractModel implements Warehouse {

    private static final long serialVersionUID = 133923683264098006L;

    private Id id = null;
    private String plz = null;
    private String l1 = null;
    private String l2 = null;
    private String l3 = null;
    private String l4 = null;
    private String l5 = null;
    private String l6 = null;

    public Id getId() {
	return id;
    }

    public void setId(Id id) {
	this.id = id;
    }

    public String getPlz() {
	return plz;
    }

    public void setPlz(String plz) {
	this.plz = plz;
    }

    public String getL1() {
	return l1;
    }

    public void setL1(String l1) {
	this.l1 = l1;
    }

    public String getL2() {
	return l2;
    }

    public void setL2(String l2) {
	this.l2 = l2;
    }

    public String getL3() {
	return l3;
    }

    public void setL3(String l3) {
	this.l3 = l3;
    }

    public String getL4() {
	return l4;
    }

    public void setL4(String l4) {
	this.l4 = l4;
    }

    public String getL5() {
	return l5;
    }

    public void setL5(String l5) {
	this.l5 = l5;
    }

    public String getL6() {
	return l6;
    }

    public void setL6(String l6) {
	this.l6 = l6;
    }

    public Set<String> getNumbers() {
	Set<String> result = new HashSet<>();
	if (getL1() != null) {
	    result.add(prepareWarehouseNumber(getL1()));
	}
	if (getL2() != null) {
	    result.add(prepareWarehouseNumber(getL2()));
	}
	if (getL3() != null) {
	    result.add(prepareWarehouseNumber(getL3()));
	}
	if (getL4() != null) {
	    result.add(prepareWarehouseNumber(getL4()));
	}
	if (getL5() != null) {
	    result.add(prepareWarehouseNumber(getL5()));
	}
	if (getL6() != null) {
	    result.add(prepareWarehouseNumber(getL6()));
	}
	return result;
    }

    private String prepareWarehouseNumber(String data) {
	return data.replaceFirst("L", "").replaceFirst("N", "");
    }

    public void fromMap(Map<String, Object> map) {
	if (map != null) {
	    super.fromMap(map);

	    this.id = id_(map.get(ID));
	    this.plz = str_(map.get(PLZ));
	    this.l1 = str_(map.get(L1));
	    this.l2 = str_(map.get(L2));
	    this.l3 = str_(map.get(L3));
	    this.l4 = str_(map.get(L4));
	    this.l5 = str_(map.get(L5));
	    this.l6 = str_(map.get(L6));
	}
    }

    public Map<String, Object> toMap() {
	Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

	map.put(ID, getId());
	map.put(PLZ, getPlz());
	map.put(L1, getL1());
	map.put(L2, getL2());
	map.put(L3, getL3());
	map.put(L4, getL4());
	map.put(L5, getL5());
	map.put(L6, getL6());

	return map;
    }
}
