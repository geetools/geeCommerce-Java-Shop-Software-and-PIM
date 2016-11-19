package com.geecommerce.retail.model;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.Id;

public interface ShoppingCenterInfo extends MultiContextModel {
    ShoppingCenterInfo setId(Id id);

    String getText();

    void setText(String text);

    String getWebSite();

    void setWebSite(String webSite);

    String getWebSiteLinkText();

    void setWebSiteLinkText(String webSiteLinkText);

    String getImageUri();

    void setImageUri(String imageUri);

    final class Col {
	public static final String ID = "_id";
	public static final String TEXT = "text";
	public static final String WEB_SITE = "web_site";
	public static final String WEB_SITE_LINK_TEXT = "web_site_link_text";
	public static final String IMAGE_URI = "image_uri";
    }
}
