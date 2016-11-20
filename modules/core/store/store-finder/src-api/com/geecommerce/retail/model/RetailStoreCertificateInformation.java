package com.geecommerce.retail.model;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.Id;

public interface RetailStoreCertificateInformation extends MultiContextModel {
    Id getId();

    RetailStoreCertificateInformation setId(Id id);

    String getText();

    void setText(String text);

    final class Col {
        public static final String ID = "_id";
        public static final String TEXT = "text";
    }

}
