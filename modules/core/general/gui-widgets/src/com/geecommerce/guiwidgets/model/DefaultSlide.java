package com.geecommerce.guiwidgets.model;

import java.util.Date;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.enums.SlideType;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

@Model
public class DefaultSlide extends AbstractModel implements Slide {

    private Id id = null;
    private MediaAsset mediaAsset = null;
    private MediaAsset linkedMediaAsset = null;
    @Column(Col.MEDIA_ASSET_ID)
    private Id mediaAssetId = null;
    @Column(Col.LINKED_MEDIA_ASSET_ID)
    private Id linkedMediaAssetId = null;
    @Column(Col.LINK)
    private String link = null;
    @Column(Col.MAP)
    private String map = null;
    @Column(Col.HTML)
    private String html = null;
    @Column(Col.PRODUCT_ARTICLE)
    private String productArticle = null;
    @Column(Col.PRICE_POSITION)
    private String pricePosition = null;
    @Column(Col.POSITION)
    private Integer position = null;
    @Column(Col.SHOW_FROM)
    private Date showFrom = null;
    @Column(Col.SHOW_TO)
    private Date showTo = null;

    private Product product = null;

    private final MediaAssetService mediaAssetService;
    private final Products products;

    @Inject
    public DefaultSlide(MediaAssetService mediaAssetService, Products products) {
        this.mediaAssetService = mediaAssetService;
        this.products = products;
    }

    @Override
    public Slide setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public MediaAsset getMediaAsset() {
        if (mediaAsset == null && mediaAssetId != null)
            mediaAsset = mediaAssetService.get(mediaAssetId);
        return mediaAsset;
    }

    @Override
    public Slide setMediaAsset(MediaAsset mediaAsset) {
        this.mediaAsset = mediaAsset;
        this.mediaAssetId = mediaAsset.getId();
        return this;
    }

    @Override
    public MediaAsset getLinkedMediaAsset() {
        if (linkedMediaAsset == null && linkedMediaAssetId != null)
            linkedMediaAsset = mediaAssetService.get(linkedMediaAssetId);
        return linkedMediaAsset;
    }

    @Override
    public Slide setLinkedMediaAsset(MediaAsset linkedMediaAsset) {
        this.linkedMediaAsset = linkedMediaAsset;
        this.linkedMediaAssetId = linkedMediaAsset.getId();
        return this;
    }

    @Override
    public String getSlideUri() {
        return getSlideUri(570, 300);
    }

    @Override
    public String getSlideUri(int width, int height) {
        MediaAsset mediaAsset = getMediaAsset();
        if (mediaAsset != null) {
            return mediaAsset.getUrl(width, height);
        }
        return null;
    }

    @Override
    public String getLink() {
        if (getLinkedMediaAsset() != null) {
            return getLinkedMediaAsset().getUrl();
        }
        return link;
    }

    @Override
    public Slide setLink(String link) {
        this.link = link;
        return this;
    }

    @Override
    public String getHtml() {
        return html;
    }

    @Override
    public Slide setHtml(String html) {
        this.html = html;
        return this;
    }

    @Override
    public String getMap() {
        return map;
    }

    @Override
    public Slide setMap(String map) {
        this.map = map;
        return this;
    }

    @Override
    public Integer getPosition() {
        return position;
    }

    @Override
    public Slide setPosition(Integer position) {
        this.position = position;
        return this;
    }

    @Override
    public SlideType getSlideType() {
        if (this.mediaAssetId != null && this.productArticle != null && !this.productArticle.isEmpty())
            return SlideType.PRODUCT;

        if (this.mediaAssetId != null && this.link != null && !this.link.isEmpty())
            return SlideType.IMAGE_LINK;

        if (this.mediaAssetId != null)
            return SlideType.IMAGE;

        if (this.map != null && !this.map.isEmpty())
            return SlideType.MAP;

        if (this.html != null && !this.html.isEmpty())
            return SlideType.HTML;

        return null;
    }

    @Override
    public String getProductArticle() {
        return productArticle;
    }

    @Override
    public Slide setProductArticle(String productArticle) {
        this.productArticle = productArticle;
        this.product = null;
        return this;
    }

    @Override
    public Product getProduct() {
        if (product == null && productArticle != null) {
            product = products.havingArticleNumber(productArticle);
        }
        return product;
    }

    @Override
    public String getPricePosition() {
        return pricePosition;
    }

    @Override
    public Slide setPricePosition(String pricePosition) {
        this.pricePosition = pricePosition;
        return this;
    }

    @Override
    public Date getShowFrom() {
        return showFrom;
    }

    @Override
    public Slide setShowFrom(Date showFrom) {
        this.showFrom = showFrom;
        return this;
    }

    @Override
    public Date getShowTo() {
        return showTo;
    }

    @Override
    public Slide setShowTo(Date showTo) {
        this.showTo = showTo;
        return this;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Col.ID));
        this.mediaAssetId = id_(map.get(Col.MEDIA_ASSET_ID));
        this.linkedMediaAssetId = id_(map.get(Col.LINKED_MEDIA_ASSET_ID));
        this.link = str_(map.get(Col.LINK));
        this.html = str_(map.get(Col.HTML));
        this.map = str_(map.get(Col.MAP));
        this.position = int_(map.get(Col.POSITION));
        this.productArticle = str_(map.get(Col.PRODUCT_ARTICLE));
        this.pricePosition = str_(map.get(Col.PRICE_POSITION));
        this.showFrom = date_(map.get(Col.SHOW_FROM));
        this.showTo = date_(map.get(Col.SHOW_TO));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());
        map.put(Col.ID, getId());
        map.put(Col.MEDIA_ASSET_ID, mediaAssetId);
        map.put(Col.LINKED_MEDIA_ASSET_ID, linkedMediaAssetId);
        map.put(Col.LINK, getLink());
        map.put(Col.HTML, getHtml());
        map.put(Col.MAP, getMap());
        map.put(Col.POSITION, getPosition());
        map.put(Col.PRODUCT_ARTICLE, getProductArticle());
        map.put(Col.PRICE_POSITION, getPricePosition());
        map.put(Col.SHOW_FROM, getShowFrom());
        map.put(Col.SHOW_TO, getShowTo());
        return map;
    }
}
