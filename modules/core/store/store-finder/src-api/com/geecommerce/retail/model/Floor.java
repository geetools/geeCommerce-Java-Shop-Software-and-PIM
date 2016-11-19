package com.geecommerce.retail.model;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.Id;

import java.util.List;

public interface Floor extends MultiContextModel {
    Floor setId(Id id);

    String getName();

    void setName(String name);

    Integer getNumber();

    void setNumber(Integer number);

    String getDescription();

    void setDescription(String description);

    String getImageUri();

    void setImageUri(String imageUri);

    List<FloorItem> getFloorItems();

    void setFloorItems(List<FloorItem> floorItems);

    final class Col {
	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String NUMBER = "number";
	public static final String DESCRIPTION = "description";
	public static final String IMAGE_URI = "image_uri";
	public static final String FLOOR_ITEMS = "floor_items";
    }
}
