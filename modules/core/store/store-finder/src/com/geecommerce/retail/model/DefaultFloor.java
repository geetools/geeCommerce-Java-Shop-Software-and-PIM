package com.geecommerce.retail.model;

import static com.geecommerce.retail.model.Floor.Col.DESCRIPTION;
import static com.geecommerce.retail.model.Floor.Col.FLOOR_ITEMS;
import static com.geecommerce.retail.model.Floor.Col.ID;
import static com.geecommerce.retail.model.Floor.Col.IMAGE_URI;
import static com.geecommerce.retail.model.Floor.Col.NAME;
import static com.geecommerce.retail.model.Floor.Col.NUMBER;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractAttributeSupport;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Model
public class DefaultFloor extends AbstractAttributeSupport implements Floor {
    // @Column(ID)
    private Id id = null;
    // @Column(NAME)
    private String name;
    // @Column(NUMBER)
    private Integer number;
    // @Column(DESCRIPTION)
    private String description;
    // @Column(RETAIL_STORE_NUMBER)
    // private String retailStoreNumber;
    // @Column(IMAGE_URI)
    private String imageUri;

    private List<FloorItem> floorItems = new LinkedList<>();

    public Id getId() {
        return id;
    }

    public Floor setId(Id id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public List<FloorItem> getFloorItems() {
        return floorItems;
    }

    public void setFloorItems(List<FloorItem> floorItems) {
        this.floorItems = floorItems;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map != null) {
            super.fromMap(map);

            this.id = id_(map.get(ID));
            this.name = str_(map.get(NAME));
            this.description = str_(map.get(DESCRIPTION));
            this.number = int_(map.get(NUMBER));
            this.imageUri = str_(map.get(IMAGE_URI));

            List<Map<String, Object>> floorItemsMap = list_(map.get(FLOOR_ITEMS));
            if (floorItemsMap != null) {
                floorItemsMap.forEach(entry -> {
                    FloorItem floorItem = app.getModel(FloorItem.class);
                    floorItem.fromMap(entry);
                    getFloorItems().add(floorItem);
                });
            }
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(ID, getId());
        map.put(NAME, getName());
        map.put(DESCRIPTION, getDescription());
        map.put(NUMBER, getNumber());
        map.put(IMAGE_URI, getImageUri());

        List<Map<String, Object>> floorItems = new LinkedList<>();
        getFloorItems().forEach(floorItem -> floorItems.add(floorItem.toMap()));
        map.put(FLOOR_ITEMS, floorItems);

        return map;
    }

}
