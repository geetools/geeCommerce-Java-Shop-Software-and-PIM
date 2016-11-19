package com.geecommerce.vacancy.model;

import java.util.List;

import com.google.inject.Inject;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.geecommerce.vacancy.service.VacancyService;

@Cacheable
@Model(collection = "vacancy_groups", fieldAccess = true)
public class DefaultVacancyGroup extends AbstractMultiContextModel implements VacancyGroup {
    private static final long serialVersionUID = 90399598657570579L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Column(Col.IMAGE_URL)
    private String imageUrl = null;

    @Column(Col.POSITION)
    private int position = 0;

    @Column(Col.ENABLED)
    private ContextObject<Boolean> enabled = null;

    private MediaAsset imageMediaAsset = null;

    @Column(Col.IMAGE_ID)
    private Id imageId = null;

    @Override
    public Id getId() {
	return id;
    }

    private final MediaAssetService mediaAssetService;
    private final VacancyService vacancyService;

    @Inject
    public DefaultVacancyGroup(MediaAssetService mediaAssetService, VacancyService vacancyService) {
	this.mediaAssetService = mediaAssetService;
	this.vacancyService = vacancyService;
    }

    @Override
    public VacancyGroup setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public ContextObject<String> getLabel() {
	return label;
    }

    @Override
    public VacancyGroup setLabel(ContextObject<String> label) {
	this.label = label;
	return this;
    }

    @Override
    public String getImageUrl() {
	if (getImage() != null)
	    return getImage().getUrl();
	return imageUrl;
    }

    @Override
    public VacancyGroup setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
	return this;
    }

    @Override
    public int getPosition() {
	return position;
    }

    @Override
    public VacancyGroup setPosition(int position) {
	this.position = position;
	return this;
    }

    @Override
    public ContextObject<Boolean> getEnabled() {
	return enabled;
    }

    @Override
    public VacancyGroup setEnabled(ContextObject<Boolean> enabled) {
	this.enabled = enabled;
	return this;
    }

    public List<Vacancy> getVacancyList() {
	return vacancyService.getVacancies(this);
    }

    @Override
    public MediaAsset getImage() {
	if (imageMediaAsset == null && imageId != null)
	    imageMediaAsset = mediaAssetService.get(imageId);
	return imageMediaAsset;
    }

    @Override
    public VacancyGroup setImage(MediaAsset image) {
	this.imageMediaAsset = image;
	if (image != null)
	    this.imageId = image.getId();
	else
	    this.imageId = null;
	return this;
    }

    public Id getImageId() {
	return imageId;
    }

    public VacancyGroup setImageId(Id imageId) {
	this.imageId = imageId;
	return this;
    }

}
