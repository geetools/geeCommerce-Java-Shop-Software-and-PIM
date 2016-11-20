package com.geecommerce.core.utils;

public class Filenames {
    public static String ensureSafeName(String filename) {
        return ensureSafeName(filename, false, false);
    }

    public static String ensureSafeName(String filename, boolean toLowerCase) {
        return ensureSafeName(filename, toLowerCase, false);
    }

    public static String ensureSafeName(String filename, boolean toLowerCase, boolean underscoreToHyphen) {
        if (filename == null) {
            return null;
        }

        String s = Regex.normalize(filename);
        s = s.replace(' ', '-');

        if (underscoreToHyphen)
            s = s.replace('_', '-');

        if (toLowerCase) {
            s = s.toLowerCase();
        }

        return Regex.replaceAll(s, "[^A-Za-z0-9\\-_\\.]", "");
    }

}
