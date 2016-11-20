package com.geecommerce.core.utils;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    private static final Map<String, Pattern> cache = new HashMap<>();

    /**
     * Replaces all found substrings in a text with a given replacement. This
     * method simply passes the regex handling onto javax.util.regex.Matcher,
     * but caches the compiled pattern for reuse to increase performance.
     * 
     * @param text
     * @param pattern
     * @param replacement
     * @return replaced text
     */
    public static String replaceAll(String text, String pattern, String replacement) {
        Pattern p = compile(pattern);
        Matcher m = p.matcher(text);
        return m.replaceAll(replacement);
    }

    /**
     * Replaces the first found substring in a text with a given replacement.
     * This method simply passes the regex handling onto
     * javax.util.regex.Matcher, but caches the compiled pattern for reuse to
     * increase performance.
     * 
     * @param text
     * @param pattern
     * @param replacement
     * @return replaced text
     */
    public static String replaceFirst(String text, String pattern, String replacement) {
        Pattern p = compile(pattern);
        Matcher m = p.matcher(text);
        return m.replaceFirst(replacement);
    }

    /**
     * Replaces the first found substring in a text with a given replacement.
     * This method simply passes the regex handling onto
     * javax.util.regex.Matcher, but caches the compiled pattern for reuse to
     * increase performance.
     * 
     * @param text
     * @param pattern
     * @param replacement
     * @return replaced text
     */
    public static String normalize(String text) {
        String s = text.replace("ä", "ae").replace("ö", "oe").replace("ü", "ue").replace("Ä", "Ae").replace("Ö", "Oe")
            .replace("Ü", "Ue").replace("ß", "ss").replace("@", "a").replace("&", "and");

        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern p = compile("\\p{InCombiningDiacriticalMarks}+");
        return p.matcher(s).replaceAll("");
    }

    /**
     * Check if the given pattern is found in a text using java.util.regex
     * classes. This method simply passes the regex handling onto
     * javax.util.regex.Matcher, but caches the compiled pattern for reuse to
     * increase performance.
     * 
     * @param text
     * @param pattern
     * @return boolean matches
     * @see java.util.regex.Matcher
     */
    public static boolean matches(String text, String pattern) {
        Pattern p = compile(pattern);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * Returns the number of cached regular expressions.
     * 
     * @return size
     */
    public static long size() {
        return cache().size();
    }

    /**
     * Clears the regex cache.
     */
    public static void clear() {
        cache().clear();
    }

    /**
     * Returns a string of debug output containing all of the cached regular
     * expressions.
     * 
     * @return debug string
     */
    public static String debug() {
        StringBuffer sb = new StringBuffer();
        sb.append("------------------------------------------------------\n");
        sb.append("Regex Keys\n");
        sb.append("------------------------------------------------------\n");

        Set<String> keys = cache().keySet();

        if (keys != null && keys.size() > 0) {
            for (Object k : keys) {
                sb.append(k.toString()).append("\n");
            }
        } else {
            sb.append("No regex keys available.\n");
        }

        return sb.toString();
    }

    /**
     * Compiles string pattern and caches Pattern object for reuse.
     * 
     * @param pattern
     *            String to compile
     * @return Pattern
     */
    private static Pattern compile(String pattern) {
        Map<String, Pattern> c = cache();
        Pattern p = c.get(pattern);

        if (p == null) {
            p = Pattern.compile(pattern);
            c.put(pattern, p);
        }

        return p;
    }

    /**
     * Returns cache instance where regular expressions are to be cached.
     * 
     * @return Cache
     */
    private static Map<String, Pattern> cache() {
        return cache;
    }
}
