package com.geecommerce.core;

import java.util.HashSet;
import java.util.Set;

public class False {
    public static final Set<String> i18nFalse = new HashSet<>();
    static {
        i18nFalse.add("false");
        i18nFalse.add("0");
        i18nFalse.add("n");
        i18nFalse.add("no");
        i18nFalse.add("няма");
        i18nFalse.add("ne");
        i18nFalse.add("не");
        i18nFalse.add("ingen");
        i18nFalse.add("nee");
        i18nFalse.add("ei");
        i18nFalse.add("non");
        i18nFalse.add("ningunha");
        i18nFalse.add("nein");
        i18nFalse.add("κανένα");
        i18nFalse.add("nem");
        i18nFalse.add("ekki");
        i18nFalse.add("aon");
        i18nFalse.add("no");
        i18nFalse.add("nē");
        i18nFalse.add("ebda");
        i18nFalse.add("nei");
        i18nFalse.add("nie");
        i18nFalse.add("não");
        i18nFalse.add("nu");
        i18nFalse.add("нет");
        i18nFalse.add("ne");
        i18nFalse.add("nie");
        i18nFalse.add("nej");
        i18nFalse.add("немає");
        i18nFalse.add("dim");
        i18nFalse.add("קיין");
        i18nFalse.add("ոչ");
        i18nFalse.add("yox");
        i18nFalse.add("না");
        i18nFalse.add("没有");
        i18nFalse.add("沒有");
        i18nFalse.add("არ");
        i18nFalse.add("કોઈ");
        i18nFalse.add("नहीं");
        i18nFalse.add("tsis muaj");
        i18nFalse.add("いいえ");
        i18nFalse.add("ಯಾವುದೇ");
        i18nFalse.add("жоқ");
        i18nFalse.add("គ្មាន");
        i18nFalse.add("아니");
        i18nFalse.add("ບໍ່​ມີ");
        i18nFalse.add("ഇല്ല");
        i18nFalse.add("नाही");
        i18nFalse.add("ямар ч");
        i18nFalse.add("အဘယ်သူမျှမ");
        i18nFalse.add("कुनै");
        i18nFalse.add("නෑ");
        i18nFalse.add("нест");
        i18nFalse.add("இல்லை");
        i18nFalse.add("ఏ");
        i18nFalse.add("ไม่");
        i18nFalse.add("نہیں");
        i18nFalse.add("yo'q");
        i18nFalse.add("không");
        i18nFalse.add("لا");
        i18nFalse.add("לא");
        i18nFalse.add("هیچ");
        i18nFalse.add("hayır");
        i18nFalse.add("geen");
        i18nFalse.add("palibe");
        i18nFalse.add("babu");
        i18nFalse.add("ọ dịghị");
        i18nFalse.add("ha ho");
        i18nFalse.add("no");
        i18nFalse.add("hakuna");
    }

    public static boolean matches(String s) {
        return s != null && i18nFalse.contains(s.toLowerCase());
    }
}
