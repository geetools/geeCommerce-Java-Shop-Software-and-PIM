package com.geecommerce.retail.model;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model("retail_store_certificate_informations")
public class DefaultRetailStoreCertificateInformation extends AbstractMultiContextModel
    implements RetailStoreCertificateInformation {
    @Column(Col.ID)
    private Id id = null;
    @Column(Col.TEXT)
    private String text = null;

    public Id getId() {
        return id;
    }

    public RetailStoreCertificateInformation setId(Id id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
