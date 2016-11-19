package com.geecommerce.vacancy.model;

import com.google.inject.Inject;
import com.geecommerce.core.service.AbstractAttributeGroupSupport;
import com.geecommerce.core.service.AbstractAttributeSupport;
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
@Model(collection = "vacancies")
public class DefaultVacancy extends AbstractAttributeGroupSupport implements Vacancy {
    private static final long serialVersionUID = 90399598657570579L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.GROUP_ID)
    private Id vacancyGroupId = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Column(Col.DESCRIPTION)
    private ContextObject<String> description = null;

    @Column(Col.BRANCH)
    private String branch = null;

    @Column(Col.TAG)
    private String tag = null;

    @Column(Col.DOCUMENT_URL)
    private String documentUrl = null;

    @Column(Col.POSITION)
    private int position = 0;

    @Column(Col.ENABLED)
    private ContextObject<Boolean> enabled = null;

    private MediaAsset documentMediaAsset = null;

    @Column(Col.DOCUMENT_ID)
    private Id documentId = null;

    private final MediaAssetService mediaAssetService;

    @Inject
    public DefaultVacancy(MediaAssetService mediaAssetService) {
	this.mediaAssetService = mediaAssetService;
    }

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public Vacancy setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public Vacancy belongsTo(VacancyGroup group) {
	if (group == null || group.getId() == null)
	    throw new NullPointerException("The vacancy groupId cannot be null");

	this.vacancyGroupId = group.getId();
	return this;
    }

    @Override
    public Id getVacancyGroupId() {
	return vacancyGroupId;
    }

    public void setVacancyGroupId(Id vacancyGroupId) {
	this.vacancyGroupId = vacancyGroupId;
    }

    @Override
    public ContextObject<String> getLabel() {
	return label;
    }

    @Override
    public Vacancy setLabel(ContextObject<String> label) {
	this.label = label;
	return this;
    }

    @Override
    public ContextObject<String> getDescription() {
	return description;
    }

    @Override
    public Vacancy setDescription(ContextObject<String> description) {
	this.description = description;
	return this;
    }

    @Override
    public String getBranch() {
	return branch;
    }

    @Override
    public Vacancy setBranch(String branch) {
	this.branch = branch;
	return this;
    }

    @Override
    public String getDocumentUrl() {
	if (getDocument() != null)
	    return getDocument().getUrl();
	return documentUrl;
    }

    @Override
    public Vacancy setDocumentUrl(String documentUrl) {
	this.documentUrl = documentUrl;
	return this;
    }

    @Override
    public String getTag() {
	return tag;
    }

    @Override
    public Vacancy setTag(String tag) {
	this.tag = tag;
	return this;
    }

    @Override
    public int getPosition() {
	return position;
    }

    @Override
    public Vacancy setPosition(int position) {
	this.position = position;
	return this;
    }

    @Override
    public ContextObject<Boolean> getEnabled() {
	return enabled;
    }

    @Override
    public Vacancy setEnabled(ContextObject<Boolean> enabled) {
	this.enabled = enabled;
	return this;
    }

    @Override
    public MediaAsset getDocument() {
	if (documentMediaAsset == null && documentId != null)
	    documentMediaAsset = mediaAssetService.get(documentId);
	return documentMediaAsset;
    }

    @Override
    public Vacancy setDocument(MediaAsset document) {
	this.documentMediaAsset = document;
	if (document != null)
	    this.documentId = document.getId();
	else
	    this.documentId = null;
	return this;
    }

    public Id getDocumentId() {
	return documentId;
    }

    public Vacancy setDocumentId(Id documentId) {
	this.documentId = documentId;
	return this;
    }

    @Override
    public boolean isShow() {
	if (getEnabled() == null)
	    return false;
	if (getEnabled().getVal() != null && getEnabled().getVal())
	    return true;
	return false;
    }

}
