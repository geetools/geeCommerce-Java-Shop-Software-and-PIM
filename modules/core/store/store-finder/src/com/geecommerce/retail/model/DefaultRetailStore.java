package com.geecommerce.retail.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractAttributeSupport;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

@Cacheable
@Model("retail_stores")
public class DefaultRetailStore extends AbstractAttributeSupport implements RetailStore {
    private static final long serialVersionUID = 133923683264098006L;
    private Id id = null;
    private String id2 = null;
    private ContextObject<String> title = null;
    private final String NO_TITLE = "no title available";
    private String name = null;
    private String zip = null;
    private String city = null;
    private String country = null;
    private String telephone = null;
    private String fax = null;
    private String email = null;
    private Id mainImageId = null;
    private String mainImageUri = null;
    private boolean enabled = false;
    private String uri = null;
    private Integer sortIndex = null;
    private Double distance;
    private String street;
    private String building;
    private String workdaystart;
    private String workdayend;
    private String weekendstart;
    private String weekendend;
    private List<Floor> floors = new ArrayList<>();
    private ShoppingCenterInfo shoppingCenterInfo;

    private final UrlRewrites urlRewrites;

    @Inject
    public DefaultRetailStore(UrlRewrites urlRewrites) {
	this.urlRewrites = urlRewrites;
    }

    private List<String> addressLines = new ArrayList<>();

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public RetailStore setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public String getId2() {
	return id2;
    }

    @Override
    public RetailStore setId2(String id2) {
	this.id2 = id2;
	return this;
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public RetailStore setName(String name) {
	this.name = name;
	return this;
    }

    @Override
    public RetailStore setTitle(ContextObject<String> title) {
	this.title = title;
	return this;
    }

    @Override
    public String getTitle() {
	if (title != null)
	    return title.getString();
	return NO_TITLE;
    }

    @Override
    public ContextObject<String> getTitles() {
	return title;
    }

    @Override
    public RetailStore setZip(String zip) {
	this.zip = zip;
	return this;
    }

    @Override
    public String getZip() {
	return zip;
    }

    @Override
    public RetailStore setCity(String city) {
	this.city = city;
	return this;
    }

    @Override
    public String getCity() {
	return city;
    }

    @Override
    public RetailStore setCountry(String country) {
	this.country = country;
	return this;
    }

    @Override
    public String getCountry() {
	return country;
    }

    @Override
    public RetailStore setTelephone(String telephone) {
	this.telephone = telephone;
	return this;
    }

    @Override
    public String getTelephone() {
	return telephone;
    }

    @Override
    public RetailStore setFax(String fax) {
	this.fax = fax;
	return this;
    }

    @Override
    public String getFax() {
	return fax;
    }

    @Override
    public RetailStore setEmail(String email) {
	this.email = email;
	return this;
    }

    @Override
    public String getEmail() {
	return email;
    }

    @Override
    public RetailStore setAddressLines(List<String> addressLines) {
	this.addressLines = addressLines;
	return this;
    }

    @Override
    public List<String> getAddressLines() {
	return addressLines;
    }

    @Override
    public RetailStore setAddressLines(String... lines) {
	this.addressLines = Lists.newArrayList(lines);
	return this;
    }

    @Override
    public RetailStore addAddressLine(String addressLine) {
	this.addressLines.add(addressLine);
	return this;
    }

    @Override
    public RetailStore setMainImageId(Id mainImageId) {
	this.mainImageId = mainImageId;
	return this;
    }

    public Id getMainImageId() {
	return mainImageId;
    }

    public String getMainImageUri() {
	return mainImageUri;
    }

    public String setMainImageUri(String mainImageUri) {
	return mainImageUri;
    }

    @Override
    public boolean isEnabled() {
	return enabled;
    }

    @Override
    public RetailStore enable() {
	enabled = true;
	return this;

    }

    @Override
    public RetailStore disable() {
	enabled = false;
	return this;
    }

    @Override
    public String getUri() {
	if (uri == null) {
	    UrlRewrite urlRewrite = urlRewrites.forRetailStore(getId());

	    if (urlRewrite != null && urlRewrite.getRequestURI() != null && urlRewrite.getRequestURI().getClosestValue() != null)
		uri = urlRewrite.getRequestURI().getClosestValue();

	    if (uri == null) {
		uri = "/store/view/" + getId();
	    }
	}
	return uri;
    }

    @Override
    public RetailStore setUri(String uri) {
	this.uri = uri;
	return this;
    }

    @Override
    public Integer getSortIndex() {
	return sortIndex;
    }

    @Override
    public RetailStore setSortIndex(Integer sortIndex) {
	this.sortIndex = sortIndex;
	return this;
    }

    public Double getDistance() {// WORKAROUND
	return distance;
    }

    public void setDistance(Double distance) {
	this.distance = distance;
    };

    @Override
    public String getStreet() {
	return street;
    }

    @Override
    public void setStreet(String street) {
	this.street = street;
    }

    @Override
    public String getBuilding() {
	return building;
    }

    @Override
    public void setBuilding(String building) {
	this.building = building;
    }

    @Override
    public String getWorkdayStart() {
	return workdaystart;
    }

    @Override
    public void setWorkdayStart(String workdayStart) {
	this.workdaystart = workdayStart;
    }

    @Override
    public String getWorkdayEnd() {
	return workdayend;
    }

    @Override
    public void setWorkdayEnd(String workdayEnd) {
	this.workdayend = workdayEnd;
    }

    @Override
    public String getWeekendStart() {
	return weekendstart;
    }

    @Override
    public void setWeekendStart(String weekendStart) {
	this.weekendstart = weekendStart;
    }

    @Override
    public String getWeekendEnd() {
	return weekendend;
    }

    @Override
    public void setWeekendEnd(String weekendEnd) {
	this.weekendend = weekendEnd;
    }

    public List<Floor> getFloors() {
	return floors;
    }

    public void setFloors(List<Floor> floors) {
	this.floors = floors;
    }

    public ShoppingCenterInfo getShoppingCenterInfo() {
	return shoppingCenterInfo;
    }

    public void setShoppingCenterInfo(ShoppingCenterInfo shoppingCenterInfo) {
	this.shoppingCenterInfo = shoppingCenterInfo;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
	if (map == null)
	    return;

	super.fromMap(map);

	this.id = id_(map.get(Column.ID));
	this.id2 = str_(map.get(Column.ID2));
	this.name = str_(map.get(Column.NAME));
	this.title = ctxObj_(map.get(Column.TITLE));
	this.zip = str_(map.get(Column.ZIP));
	this.city = str_(map.get(Column.CITY));
	this.country = str_(map.get(Column.COUNTRY));
	this.telephone = str_(map.get(Column.TELEPHONE));
	this.fax = str_(map.get(Column.FAX));
	this.email = str_(map.get(Column.EMAIL));
	this.mainImageId = id_(map.get(Column.MAIN_IMAGE_ID));
	this.mainImageUri = str_(map.get(Column.MAIN_IMAGE_URI));
	this.enabled = bool_(map.get(Column.ENABLED));
	this.addressLines = list_(map.get(Column.ADDRESS_LINES));
	this.sortIndex = int_(map.get(Column.SORT_INDEX));
	this.street = str_(map.get(Column.STREET));
	this.building = str_(map.get(Column.BUILDING));
	this.workdaystart = str_(map.get(Column.WORKDAYSTART));
	this.workdayend = str_(map.get(Column.WORKDAYEND));
	this.weekendstart = str_(map.get(Column.WORKDAYSTART));
	this.weekendend = str_(map.get(Column.WEEKENDEND));

	List<Map<String, Object>> floorMap = list_(map.get(Column.FLOORS));
	if (floorMap != null) {
	    floorMap.forEach(entry -> {
		Floor floor = app.getModel(Floor.class);
		floor.fromMap(entry);
		getFloors().add(floor);
	    });
	}

	if (map.get(Column.SHOPPING_CENTER_INFO) != null) {
	    ShoppingCenterInfo shoppingCenterInfo = app.getModel(ShoppingCenterInfo.class);
	    shoppingCenterInfo.fromMap(map_(map.get(Column.SHOPPING_CENTER_INFO)));
	    setShoppingCenterInfo(shoppingCenterInfo);
	}
    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

	map.put(Column.ID, getId());
	map.put(Column.ID2, getId2());
	map.put(Column.NAME, getName());
	map.put(Column.TITLE, getTitles());
	map.put(Column.ZIP, getZip());
	map.put(Column.CITY, getCity());
	map.put(Column.COUNTRY, getCountry());
	map.put(Column.TELEPHONE, getTelephone());
	map.put(Column.FAX, getFax());
	map.put(Column.EMAIL, getEmail());
	map.put(Column.MAIN_IMAGE_ID, getMainImageId());
	map.put(Column.MAIN_IMAGE_URI, getMainImageUri());
	map.put(Column.ENABLED, isEnabled());
	map.put(Column.ADDRESS_LINES, getAddressLines());
	map.put(Column.SORT_INDEX, getSortIndex());
	map.put(Column.STREET, getStreet());
	map.put(Column.BUILDING, getBuilding());
	map.put(Column.WEEKENDSTART, getWorkdayStart());
	map.put(Column.WORKDAYEND, getWorkdayEnd());
	map.put(Column.WEEKENDSTART, getWeekendStart());
	map.put(Column.WEEKENDEND, getWeekendEnd());

	List<Map<String, Object>> floors = new LinkedList<>();
	getFloors().forEach(floor -> floors.add(floor.toMap()));
	map.put(Column.FLOORS, floors);

	map.put(Column.SHOPPING_CENTER_INFO, getShoppingCenterInfo());

	return map;
    }

    @Override
    public String toString() {
	return "DefaultRetailStore [id=" + id + ", id2=" + id2 + ", title=" + title + ", name=" + name + ", zip=" + zip + ", city=" + city + ", country=" + country + ", email=" + email + ", enabled=" + enabled + ", sortIndex=" + sortIndex + "]";
    }
}
