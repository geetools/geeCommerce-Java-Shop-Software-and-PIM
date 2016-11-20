package com.geecommerce.core.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;

public enum I18nBoolean {
    YES(true), NO(false);

    private static final Map<I18nBoolean, Map<Locale, String>> TRANSLATIONS = new HashMap<>();
    private static final List<String> ALL_YES_TRANSLATIONS;
    private static final List<String> ALL_NO_TRANSLATIONS;

    static {
        final Map<Locale, String> YES_TRANSLATIONS = new HashMap<>();
        YES_TRANSLATIONS.put(Locale.CHINESE, "是的");
        YES_TRANSLATIONS.put(Locale.ENGLISH, "yes");
        YES_TRANSLATIONS.put(Locale.GERMAN, "ja");
        YES_TRANSLATIONS.put(Locale.FRENCH, "oui");
        YES_TRANSLATIONS.put(Locale.ITALIAN, "si");
        YES_TRANSLATIONS.put(Locale.JAPANESE, "はい");
        YES_TRANSLATIONS.put(Locale.KOREAN, "예");
        YES_TRANSLATIONS.put(new Locale("cs"), "ano");
        YES_TRANSLATIONS.put(new Locale("es"), "sí");
        YES_TRANSLATIONS.put(new Locale("pt"), "sim");
        YES_TRANSLATIONS.put(new Locale("ru"), "да");
        YES_TRANSLATIONS.put(new Locale("sk"), "áno");
        YES_TRANSLATIONS.put(new Locale("tr"), "evet");

        TRANSLATIONS.put(YES, YES_TRANSLATIONS);
        ALL_YES_TRANSLATIONS = new ArrayList<>(YES_TRANSLATIONS.values());

        final Map<Locale, String> NO_TRANSLATIONS = new HashMap<>();
        NO_TRANSLATIONS.put(Locale.CHINESE, "無");
        NO_TRANSLATIONS.put(Locale.ENGLISH, "no");
        NO_TRANSLATIONS.put(Locale.GERMAN, "nein");
        NO_TRANSLATIONS.put(Locale.FRENCH, "non");
        NO_TRANSLATIONS.put(Locale.ITALIAN, "no");
        NO_TRANSLATIONS.put(Locale.JAPANESE, "いいえ");
        NO_TRANSLATIONS.put(Locale.KOREAN, "없음");
        NO_TRANSLATIONS.put(new Locale("cs"), "ne");
        NO_TRANSLATIONS.put(new Locale("es"), "no");
        NO_TRANSLATIONS.put(new Locale("pt"), "não");
        NO_TRANSLATIONS.put(new Locale("ru"), "нет");
        NO_TRANSLATIONS.put(new Locale("sk"), "nie");
        NO_TRANSLATIONS.put(new Locale("tr"), "hayır");

        TRANSLATIONS.put(NO, NO_TRANSLATIONS);
        ALL_NO_TRANSLATIONS = new ArrayList<>(NO_TRANSLATIONS.values());
    }

    boolean bool = false;

    private I18nBoolean(boolean bool) {
        this.bool = bool;
    }

    public static final I18nBoolean valueOf(boolean bool) {
        for (I18nBoolean i18nBool : values()) {
            if (i18nBool.bool == bool)
                return i18nBool;
        }

        return null;
    }

    public static final I18nBoolean valueOf(int number) {
        return valueOf(number > 0);
    }

    public static final I18nBoolean i18nValueOf(String s) {
        if (s == null)
            return null;

        if ("true".equalsIgnoreCase(s.trim()) || "false".equalsIgnoreCase(s.trim())) {
            return valueOf(Boolean.valueOf(s));
        } else {
            if (ALL_YES_TRANSLATIONS.contains(s.toLowerCase().trim())) {
                return I18nBoolean.YES;
            } else if (ALL_NO_TRANSLATIONS.contains(s.toLowerCase().trim())) {
                return I18nBoolean.NO;
            }
        }

        return null;
    }

    public static final I18nBoolean fromObject(Object o) {
        if (o == null)
            return null;

        if (o instanceof Boolean) {
            return valueOf((Boolean) o);
        } else if (o instanceof Number) {
            return valueOf(((Number) o).intValue());
        } else if (o instanceof String) {
            return i18nValueOf((String) o);
        } else {
            return null;
        }
    }

    public String i18n(Locale locale) {
        return TRANSLATIONS.get(this).get(locale);
    }

    public String i18n(String language) {
        return i18n(new Locale(language));
    }

    public String i18n() {
        ApplicationContext appCtx = App.get().context();

        if (appCtx != null) {
            return i18n(appCtx.getLanguage());
        }

        return null;
    }

    public boolean booleanValue() {
        return bool;
    }
}
