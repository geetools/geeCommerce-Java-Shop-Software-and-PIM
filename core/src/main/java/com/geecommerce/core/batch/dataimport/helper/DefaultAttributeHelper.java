package com.geecommerce.core.batch.dataimport.helper;

import java.util.List;
import java.util.Set;

import com.geecommerce.core.App;
import com.geecommerce.core.Str;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@Helper
public class DefaultAttributeHelper implements AttributeHelper {
    protected final AttributeService attributeService;

    @Inject
    protected App app;

    @Inject
    public DefaultAttributeHelper(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Override
    public AttributeTargetObject targetObject(AttributeSupport model) {
        Class<? extends Model> modelInterface = Reflect.getModelInterface(model.getClass());

        if (AttributeSupport.class.isAssignableFrom(modelInterface)) {
            return attributeService.getAttributeTargetObject((Class<? extends AttributeSupport>) modelInterface);
        }

        return null;
    }

    @Override
    public AttributeTargetObject targetObject(Class<? extends AttributeSupport> modelClass) {
        Class<? extends Model> modelInterface = Reflect.getModelInterface(modelClass);

        if (AttributeSupport.class.isAssignableFrom(modelInterface)) {
            return attributeService.getAttributeTargetObject((Class<? extends AttributeSupport>) modelInterface);
        }

        return null;
    }

    @Override
    public boolean optionExists(AttributeTargetObject targetObject, String code, String value, String language) {
        if (targetObject == null || code == null || value == null)
            return false;

        boolean exists = false;

        Attribute attr = attributeService.getAttribute(targetObject.getId(), code);

        List<AttributeOption> options = attr.getOptions();

        for (AttributeOption attributeOption : options) {
            ContextObject<String> ctxLabel = attributeOption.getLabel();
            String label = ctxLabel.getStr(language);

            if (Str.isEmpty(label))
                continue;

            if (label.equalsIgnoreCase(value)) {
                exists = true;
                break;
            }
        }

        return exists;
    }

    @Override
    public Id optionId(AttributeTargetObject targetObject, String code, String value, String language) {
        if (targetObject == null || Str.isEmpty(code) || Str.isEmpty(value))
            return null;

        if (Str.isEmpty(language)) {
            List<String> availableLanguages = app.cpStrList_("core/i18n/available_languages");

            if (availableLanguages.size() == 1) {
                language = availableLanguages.get(0);
            }

            if (Str.isEmpty(language))
                return null;
        }

        Id optionId = null;

        Attribute attr = attributeService.getAttribute(targetObject.getId(), code);

        List<AttributeOption> options = attr.getOptions();

        for (AttributeOption attributeOption : options) {
            ContextObject<String> ctxLabel = attributeOption.getLabel();
            String label = ctxLabel.getStr(language);

            if (Str.isEmpty(label))
                continue;

            if (label.equalsIgnoreCase(value)) {
                optionId = attributeOption.getId();
                break;
            }
        }

        return optionId;
    }

    @Override
    public boolean optionsExistForOtherLanguages(AttributeTargetObject targetObject, String code, String notLanguage) {
        if (targetObject == null || code == null || notLanguage == null)
            throw new NullPointerException("targetObject=" + targetObject + ", code=" + code + ", notLanguage=" + notLanguage);

        Attribute attr = attributeService.getAttribute(targetObject.getId(), code);

        List<AttributeOption> options = attr.getOptions();

        for (AttributeOption attributeOption : options) {
            ContextObject<String> ctxLabel = attributeOption.getLabel();
            Set<String> languages = ctxLabel.specifiedLanguages();

            if (languages.size() > 1)
                return true;

            if (!languages.isEmpty()) {
                String lang = languages.iterator().next();
                if (!lang.equals(notLanguage))
                    return true;
            }
        }

        return false;
    }
}
