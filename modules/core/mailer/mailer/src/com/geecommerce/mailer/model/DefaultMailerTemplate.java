package com.geecommerce.mailer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model("mailer_templates")
public class DefaultMailerTemplate extends AbstractMultiContextModel implements MailerTemplate {
    private static final long serialVersionUID = 1L;

    private Id id = null;

    private String from = null;

    private String subject = null;

    private String bodyHtml = null;

    private String bodyText = null;

    private String key = null;

    private List<Id> attachments = null;

    private List<Id> inlineImages = null;

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public MailerTemplate setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public String getSubject() {
	return subject;
    }

    @Override
    public MailerTemplate setSubject(String subject) {
	this.subject = subject;
	return this;
    }

    @Override
    public String getKey() {
	return key;
    }

    @Override
    public MailerTemplate setKey(String key) {
	this.key = key;
	return this;
    }

    @Override
    public String getBodyHtml() {
	return bodyHtml;
    }

    @Override
    public MailerTemplate setBodyHtml(String bodyHtml) {
	this.bodyHtml = bodyHtml;
	return this;
    }

    @Override
    public String getBodyText() {
	return bodyText;
    }

    @Override
    public MailerTemplate setBodyText(String bodyText) {
	this.bodyText = bodyText;
	return this;
    }

    @Override
    public List<Id> getAttachments() {
	return attachments;
    }

    @Override
    public MailerTemplate setAttachments(List<Id> attachments) {
	this.attachments = attachments;
	return this;
    }

    @Override
    public MailerTemplate setAttachment(Id attachment) {
	if (attachments == null)
	    attachments = new ArrayList<>();
	attachments.add(attachment);
	return this;
    }

    @Override
    public List<Id> getInlineImages() {
	return inlineImages;
    }

    @Override
    public MailerTemplate setInlineImages(List<Id> inlineImages) {
	this.inlineImages = inlineImages;
	return this;
    }

    @Override
    public MailerTemplate setInlineImage(Id inlineImage) {
	if (inlineImages == null)
	    inlineImages = new ArrayList<>();
	inlineImages.add(inlineImage);
	return this;
    }

    @Override
    public String getFrom() {
	return from;
    }

    @Override
    public MailerTemplate setFrom(String from) {
	this.from = from;
	return this;
    }

    public static long getSerialVersionUID() {
	return serialVersionUID;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
	if (map == null)
	    return;

	super.fromMap(map);

	this.id = id_(map.get(Column.ID));
	this.from = str_(map.get(Column.FROM));
	this.subject = str_(map.get(Column.SUBJECT));
	this.bodyHtml = str_(map.get(Column.BODY_HTML));
	this.bodyText = str_(map.get(Column.BODY_TEXT));
	this.key = str_(map.get(Column.KEY));
	this.attachments = idList_(map.get(Column.ATTACHMENTS));
	this.inlineImages = idList_(map.get(Column.INLINE_IMAGES));
    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

	map.put(Column.ID, getId());
	map.put(Column.FROM, getFrom());
	map.put(Column.SUBJECT, getSubject());
	map.put(Column.BODY_HTML, getBodyHtml());
	map.put(Column.BODY_TEXT, getBodyText());
	map.put(Column.KEY, getKey());

	if (getAttachments() != null && getAttachments().size() > 0)
	    map.put(Column.ATTACHMENTS, getAttachments());
	if (getInlineImages() != null && getInlineImages().size() > 0)
	    map.put(Column.INLINE_IMAGES, getInlineImages());
	return map;
    }
}
