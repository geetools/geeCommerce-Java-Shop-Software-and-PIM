package com.geecommerce.core;

import java.util.HashSet;
import java.util.Set;

public class True {
    public static final Set<String> i18nTrue = new HashSet<>();
    static {
        i18nTrue.add("true");
        i18nTrue.add("1");
        i18nTrue.add("yes");
        i18nTrue.add("y");
        i18nTrue.add("j");
        i18nTrue.add("po");
        i18nTrue.add("bai");
        i18nTrue.add("ды");
        i18nTrue.add("da");
        i18nTrue.add("да");
        i18nTrue.add("sí");
        i18nTrue.add("da");
        i18nTrue.add("ano");
        i18nTrue.add("ja");
        i18nTrue.add("jah");
        i18nTrue.add("kyllä");
        i18nTrue.add("oui");
        i18nTrue.add("si");
        i18nTrue.add("ja");
        i18nTrue.add("ναί");
        i18nTrue.add("igen");
        i18nTrue.add("já");
        i18nTrue.add("sì");
        i18nTrue.add("jā");
        i18nTrue.add("taip");
        i18nTrue.add("iva");
        i18nTrue.add("ja");
        i18nTrue.add("tak");
        i18nTrue.add("sim");
        i18nTrue.add("da");
        i18nTrue.add("áno");
        i18nTrue.add("ja");
        i18nTrue.add("так");
        i18nTrue.add("ie");
        i18nTrue.add("יאָ");
        i18nTrue.add("այո");
        i18nTrue.add("bəli");
        i18nTrue.add("হাঁ");
        i18nTrue.add("是的");
        i18nTrue.add("დიახ");
        i18nTrue.add("હા");
        i18nTrue.add("हाँ");
        i18nTrue.add("yog");
        i18nTrue.add("はい");
        i18nTrue.add("ಹೌದು");
        i18nTrue.add("иә");
        i18nTrue.add("បាត");
        i18nTrue.add("네");
        i18nTrue.add("ແມ່ນ​ແລ້ວ");
        i18nTrue.add("അതെ");
        i18nTrue.add("होय");
        i18nTrue.add("Хэрэв тийм бол");
        i18nTrue.add("ဟုတ်ကဲ့");
        i18nTrue.add("हो");
        i18nTrue.add("ඔව්");
        i18nTrue.add("ҳа");
        i18nTrue.add("ஆம்");
        i18nTrue.add("అవును");
        i18nTrue.add("ใช่");
        i18nTrue.add("جی ہاں");
        i18nTrue.add("ha");
        i18nTrue.add("vâng");
        i18nTrue.add("نعم");
        i18nTrue.add("כן");
        i18nTrue.add("بله");
        i18nTrue.add("evet");
        i18nTrue.add("inde");
        i18nTrue.add("a");
        i18nTrue.add("ee");
        i18nTrue.add("e");
        i18nTrue.add("haa");
        i18nTrue.add("ndiyo");
        i18nTrue.add("bẹẹni");
        i18nTrue.add("yebo");
        i18nTrue.add("oo");
        i18nTrue.add("iya nih");
        i18nTrue.add("ya");
        i18nTrue.add("eny");
        i18nTrue.add("ya");
        i18nTrue.add("ae");
    }

    public static boolean matches(String s) {
        return s != null && i18nTrue.contains(s.toLowerCase());
    }
}
