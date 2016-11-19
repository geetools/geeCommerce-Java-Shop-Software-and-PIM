package com.geecommerce.vacancy.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;

import java.util.List;

public interface VacancyGroup extends Model {
    public Id getId();

    public VacancyGroup setId(Id id);

    public ContextObject<String> getLabel();

    public VacancyGroup setLabel(ContextObject<String> label);

    public String getImageUrl();

    public VacancyGroup setImageUrl(String imageUrl);

    public int getPosition();

    public VacancyGroup setPosition(int position);

    public ContextObject<Boolean> getEnabled();

    public VacancyGroup setEnabled(ContextObject<Boolean> enabled);

    public List<Vacancy> getVacancyList();

    public MediaAsset getImage();

    public VacancyGroup setImage(MediaAsset image);

    public Id getImageId();

    public VacancyGroup setImageId(Id imageId);

    static final class Col {
	public static final String ID = "_id";
	public static final String LABEL = "label";
	public static final String IMAGE_URL = "image_url";
	public static final String POSITION = "pos";
	public static final String ENABLED = "enabled";
	public static final String IMAGE_ID = "img_id";
    }
}
