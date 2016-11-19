package com.geecommerce.guiwidgets.model;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.enums.SlideType;
import com.geecommerce.mediaassets.model.MediaAsset;

import java.util.Date;

public interface Slide extends Model {

    public Id getId();

    public Slide setId(Id id);

    public MediaAsset getMediaAsset();

    public Slide setMediaAsset(MediaAsset mediaAsset);

    public MediaAsset getLinkedMediaAsset();

    public Slide setLinkedMediaAsset(MediaAsset linkedMediaAsset);

    public String getSlideUri();

    public String getSlideUri(int height, int width);

    public String getLink();

    public Slide setLink(String link);

    public String getHtml();

    public Slide setHtml(String html);

    public String getMap();

    public Slide setMap(String map);

    public Integer getPosition();

    public Slide setPosition(Integer position);

    public SlideType getSlideType();

    public String getProductArticle();

    public Slide setProductArticle(String productArticle);

    public Product getProduct();

    public String getPricePosition();

    public Slide setPricePosition(String pricePosition);

    public Date getShowFrom();

    public Slide setShowFrom(Date showFrom);

    public Date getShowTo();

    public Slide setShowTo(Date showTo);

    static final class Col {
	public static final String ID = "_id";
	public static final String MEDIA_ASSET_ID = "media_asset_id";
	public static final String LINKED_MEDIA_ASSET_ID = "linked_media_asset_id";
	public static final String POSITION = "pos";
	public static final String LINK = "link";
	public static final String MAP = "map";
	public static final String HTML = "html";
	public static final String PRODUCT_ARTICLE = "art";
	public static final String PRICE_POSITION = "price_pos";
	public static final String SHOW_FROM = "show_from";
	public static final String SHOW_TO = "show_to";
    }
}
