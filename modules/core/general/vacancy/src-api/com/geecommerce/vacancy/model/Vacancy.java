package com.geecommerce.vacancy.model;

import com.geecommerce.core.service.AttributeGroupSupport;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;

public interface Vacancy extends AttributeGroupSupport {
    public Id getId();

    public Vacancy setId(Id id);

    public Vacancy belongsTo(VacancyGroup vacancyGroup);

    public Id getVacancyGroupId();

    void setVacancyGroupId(Id vacancyGroupId);

    public ContextObject<String> getLabel();

    public Vacancy setLabel(ContextObject<String> label);

    public ContextObject<String> getDescription();

    public Vacancy setDescription(ContextObject<String> description);

    public String getBranch();

    public Vacancy setBranch(String branch);

    public String getDocumentUrl();

    public Vacancy setDocumentUrl(String documentUrl);

    public MediaAsset getDocument();

    public Vacancy setDocument(MediaAsset document);

    public String getTag();

    public Vacancy setTag(String tag);

    public int getPosition();

    public Vacancy setPosition(int position);

    public ContextObject<Boolean> getEnabled();

    public Vacancy setEnabled(ContextObject<Boolean> enabled);

    public Id getDocumentId();

    public Vacancy setDocumentId(Id documentId);

    public boolean isShow();

    static final class Col {
        public static final String ID = "_id";
        public static final String GROUP_ID = "group_id";
        public static final String LABEL = "label";
        public static final String DESCRIPTION = "description";
        public static final String BRANCH = "branch";
        public static final String DOCUMENT_URL = "doc_url";
        public static final String TAG = "tag";
        public static final String POSITION = "pos";
        public static final String ENABLED = "enabled";
        public static final String DOCUMENT_ID = "doc_id";
    }
}
