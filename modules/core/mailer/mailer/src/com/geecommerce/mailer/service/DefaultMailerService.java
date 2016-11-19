package com.geecommerce.mailer.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.geecommerce.core.mail.SMTPMailer;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.template.Templates;
import com.geecommerce.core.type.Id;
import com.geecommerce.mailer.model.MailerTemplate;
import com.geecommerce.mailer.repository.MailerTemplates;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.model.MediaAssetFile;
import com.geecommerce.mediaassets.service.MediaAssetService;


import freemarker.template.TemplateException;

@Service
public class DefaultMailerService implements MailerService {
    private final MailerTemplates mailerTemplates;
    private final MediaAssetService mediaAssetService;

    @Inject
    public DefaultMailerService(MailerTemplates mailerTemplates, MediaAssetService mediaAssetService) {
	this.mailerTemplates = mailerTemplates;
	this.mediaAssetService = mediaAssetService;
    }

    
    @Override
    public MailerTemplate createMailerTemplate(MailerTemplate mailerTemplate) {
	if (mailerTemplate == null)
	    throw new NullPointerException("mailerTemplate cannot be null");
	return mailerTemplates.add(mailerTemplate);
    }

    
    @Override
    public void removeMailerTemplate(MailerTemplate mailerTemplate) {
	if (mailerTemplate == null || mailerTemplate.getId() == null)
	    return;
	mailerTemplates.remove(mailerTemplate);

    }

    
    @Override
    public void updateMailerTemplate(MailerTemplate mailerTemplate) {
	if (mailerTemplate == null || mailerTemplate.getId() == null)
	    return;
	mailerTemplates.update(mailerTemplate);
    }

    
    @Override
    public MailerTemplate getMailerTemplate(Id mailerTemplateId) {
	if (mailerTemplateId == null)
	    return null;
	return mailerTemplates.findById(MailerTemplate.class, mailerTemplateId);
    }

    @Override
    public void sendMail(String key, String to, Map<String, Object> params, String smtpConfigKey) {
	MailerTemplate mailerTemplate = getMailerTemplateByKey(key);
	if (mailerTemplate != null) {
	    try {
		String subject = Templates.render(mailerTemplate.getSubject(), params);

		String bodyHtml = null;
		if (mailerTemplate.getBodyHtml() != null)
		    bodyHtml = Templates.render(mailerTemplate.getBodyHtml(), params);

		String bodyText = null;
		if (mailerTemplate.getBodyText() != null)
		    bodyText = Templates.render(mailerTemplate.getBodyText(), params);

		List<Id> attachmentIds = mailerTemplate.getAttachments();
		List<Id> inlineImageIds = mailerTemplate.getInlineImages();
		// List<URL> files = new ArrayList<>();
		// List<URL> inlineImages = new ArrayList<>();

		Map<String, InputStream> attachments = new LinkedHashMap<>();
		Map<String, InputStream> inlineImages = new LinkedHashMap<>();

		if (attachmentIds != null && attachmentIds.size() > 0) {
		    for (Id attachmentId : attachmentIds) {
			MediaAsset attachment = mediaAssetService.get(attachmentId);

			if (attachment != null) {
			    MediaAssetFile file = mediaAssetService.get(attachment.getId()).getFile();
			    attachments.put(attachment.getName().getStr(), file.getContent());
			}
		    }
		}
		if (inlineImageIds != null && inlineImageIds.size() > 0) {
		    for (Id inlineImageId : inlineImageIds) {
			MediaAsset inlineImage = mediaAssetService.get(inlineImageId);
			if (inlineImage != null) {
			    // URL inlineImageFile = new URL(inlineImage.getUrl());
			    // inlineImages.add(inlineImageFile);

			    MediaAssetFile file = mediaAssetService.get(inlineImage.getId()).getFile();
			    inlineImages.put(inlineImage.getName().getStr(), file.getContent());
			}
		    }
		}
		SMTPMailer mailer = new SMTPMailer();
		mailer.sendStreams(subject, bodyHtml, bodyText, to, smtpConfigKey, attachments, inlineImages);
	    } catch (IOException e) {
		e.printStackTrace();
	    } catch (TemplateException e) {
		e.printStackTrace();
	    }
	}

    }

    @Override
    public MailerTemplate getMailerTemplateByKey(String key) {
	if (key == null || key.isEmpty())
	    return null;
	return mailerTemplates.thatBelongTo(key);
    }

    @Override
    public void sendMail(String key, String to, Map<String, Object> params) {
	sendMail(key, to, params, null);
    }

}
