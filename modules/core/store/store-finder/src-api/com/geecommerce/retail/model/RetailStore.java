package com.geecommerce.retail.model;

import java.util.List;

import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface RetailStore extends AttributeSupport {
    public Id getId();

    public RetailStore setId(Id id);

    public String getId2();

    public RetailStore setId2(String id2);

    public String getName();

    public RetailStore setName(String name);

    public RetailStore setTitle(ContextObject<String> title);

    public String getTitle();

    public ContextObject<String> getTitles();

    public RetailStore setZip(String zip);

    public String getZip();

    public RetailStore setCity(String city);

    public String getCity();

    public RetailStore setCountry(String country);

    public String getCountry();

    public RetailStore setTelephone(String telephone);

    public String getTelephone();

    public RetailStore setFax(String fax);

    public String getFax();

    public RetailStore setEmail(String email);

    public String getEmail();

    public RetailStore setAddressLines(List<String> addressLines);

    public List<String> getAddressLines();

    public RetailStore addAddressLine(String addressLine);

    public RetailStore setMainImageId(Id mainImageId);

    public Id getMainImageId();

    String getMainImageUri();

    String setMainImageUri(String mainImageUri);

    public boolean isEnabled();

    public RetailStore enable();

    public RetailStore disable();

    public String getUri();

    public RetailStore setUri(String uri);

    public Integer getSortIndex();

    public RetailStore setSortIndex(Integer sortIndex);

    Double getDistance();

    void setDistance(Double distance);

    String getStreet();

    void setStreet(String street);

    String getBuilding();

    void setBuilding(String buiding);

    String getWorkdayStart();

    void setWorkdayStart(String workdayStart);

    String getWorkdayEnd();

    void setWorkdayEnd(String workdayEnd);

    String getWeekendStart();

    void setWeekendStart(String weekendStart);

    String getWeekendEnd();

    void setWeekendEnd(String weekendEnd);

    List<Floor> getFloors();

    void setFloors(List<Floor> floors);

    ShoppingCenterInfo getShoppingCenterInfo();

    void setShoppingCenterInfo(ShoppingCenterInfo shoppingCenterInfo);

    final class Column {
        public static final String ID = "_id";
        public static final String ID2 = "id2";
        public static final String NAME = "name";
        public static final String TITLE = "title";
        public static final String ADDRESS_LINES = "address_lines";
        public static final String ZIP = "zip";
        public static final String CITY = "city";
        public static final String COUNTRY = "country";
        public static final String TELEPHONE = "telephone";
        public static final String FAX = "fax";
        public static final String EMAIL = "email";
        public static final String MAIN_IMAGE_ID = "main_image_id";
        public static final String MAIN_IMAGE_URI = "main_image_uri";
        public static final String SORT_INDEX = "sort_index";
        public static final String ENABLED = "enabled";
        public static final String STREET = "street";
        public static final String BUILDING = "building";
        public static final String WORKDAYSTART = "workdaystart";
        public static final String WORKDAYEND = "workdayend";
        public static final String WEEKENDSTART = "weekendstart";
        public static final String WEEKENDEND = "weekendend";
        public static final String NUMBER = "number";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String FLOORS = "floors";
        public static final String SHOPPING_CENTER_INFO = "shopping_center_info";
    }
}
