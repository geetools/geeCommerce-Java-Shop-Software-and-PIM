package com.geecommerce.mailer.model;

import java.util.List;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.Id;

public interface MailerTemplate extends MultiContextModel {
    public Id getId();

    public MailerTemplate setId(Id id);

    public String getFrom();

    public MailerTemplate setFrom(String from);

    public String getSubject();

    public MailerTemplate setSubject(String subject);

    public String getBodyHtml();

    public MailerTemplate setBodyHtml(String bodyHtml);

    public String getBodyText();

    public MailerTemplate setBodyText(String bodyText);

    public String getKey();

    public MailerTemplate setKey(String key);

    public List<Id> getAttachments();

    public MailerTemplate setAttachments(List<Id> attachments);

    public MailerTemplate setAttachment(Id attachment);

    public List<Id> getInlineImages();

    public MailerTemplate setInlineImages(List<Id> inlineImages);

    public MailerTemplate setInlineImage(Id inlineImage);

    static final class Column {
	public static final String ID = "_id";
	public static final String KEY = "key";
	public static final String FROM = "from";
	public static final String SUBJECT = "subject";
	public static final String BODY_HTML = "body_html";
	public static final String BODY_TEXT = "body_text";
	public static final String ATTACHMENTS = "attachments";
	public static final String INLINE_IMAGES = "inline_images";

    }
}
