package com.geecommerce.core.util;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.BreakIterator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.nodes.Node;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.authentication.Passwords;
import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.ibm.icu.text.Transliterator;
import com.lyncode.jtwig.functions.util.HtmlUtils;

public class Strings {
    private static final char[] RANDOM_LCASE_LETTERS = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    private static final char[] RANDOM_NUMBERS = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };

    private static final List<char[]> randomCharArrays = new ArrayList<>();
    static {
        randomCharArrays.add(RANDOM_LCASE_LETTERS);
        randomCharArrays.add(RANDOM_NUMBERS);
    }

    private static final String COMMA = ",";
    private static final String COMMA_SPACE = ", ";
    private static final String DOT = ".";
    private static final String ENTITY_NBSP = "&nbsp;";
    private static final String NEWLINE_N = "\n";
    private static final String NEWLINE_R = "\r";
    private static final String EMPTY_STRING = "";
    private static final String SPACE = " ";
    private static final String SPACE_COMMA = " ,";
    private static final String SPACE_DOT = " .";
    private static final char X = 'x';

    private static final String DEFAULT_TRANSLITERATION_CODE = "Any-Latin; NFD; [:Nonspacing Mark:] remove; nfc";

    private static final Map<String, Transliterator> transliteratorCache = new HashMap<>();

    private static final Map<String, String> transliterateReplaceChars = new HashMap<>();

    private static final String ae = "ä";
    private static final String oe = "ö";
    private static final String ue = "ü";
    private static final String Ae = "Ä";
    private static final String Oe = "Ö";
    private static final String Ue = "Ü";
    private static final String ss = "ß";

    static {
        transliterateReplaceChars.put(ae, "ae");
        transliterateReplaceChars.put(oe, "oe");
        transliterateReplaceChars.put(ue, "ue");
        transliterateReplaceChars.put(Ae, "Ae");
        transliterateReplaceChars.put(Oe, "Oe");
        transliterateReplaceChars.put(Ue, "Ue");
        transliterateReplaceChars.put(ss, "ss");
        transliterateReplaceChars.put(Str.AT, "at");
        transliterateReplaceChars.put(Str.AMPERSAND, "and");
    }

    private static final char[] VOWELS = new char[] { 'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U' };

    private static final Pattern slugifyReplacePattern1 = Pattern.compile("[^\\p{ASCII}]");
    private static final Pattern slugifyReplacePattern2 = Pattern.compile("[^a-zA-Z0-9_\\-\\. ]");
    private static final Pattern slugifyReplacePattern3 = Pattern.compile("\\-+");
    private static final Pattern slugifyReplacePattern4 = Pattern.compile("\\s+");

    private static final long[] byteTable;
    static {
        byteTable = new long[256];
        long h = 0x544B2FBACAAF1684L;
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 31; j++) {
                h = (h >>> 7) ^ h;
                h = (h << 11) ^ h;
                h = (h >>> 10) ^ h;
            }
            byteTable[i] = h;
        }
    }

    private static final long HSTART = 0xBB40E64DA205B064L;
    private static final long HMULT = 7664345821815920749L;

    public static boolean isVowel(char character) {
        boolean isVowel = false;

        for (char vowel : VOWELS) {
            if (vowel == character) {
                isVowel = true;
                break;
            }
        }

        return isVowel;
    }

    public static String toMD5(String s) {
        HashFunction hf = Hashing.md5();
        HashCode hc = hf.newHasher().putString(s, Charsets.UTF_8).hash();

        return hc.toString();
    }

    public static String hash(String algorithm, String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(message.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String hash(String algorithm, String message, String key) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = key.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, algorithm);

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance(algorithm);
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(message.getBytes());

            // Convert raw bytes to Hex
            byte[] hexBytes = new Hex().encode(rawHmac);

            // Covert array of Hex bytes to a String
            return new String(hexBytes, "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long hash(CharSequence cs) {
        long h = HSTART;
        final long hmult = HMULT;
        final long[] ht = byteTable;
        final int len = cs.length();
        for (int i = 0; i < len; i++) {
            char ch = cs.charAt(i);
            h = (h * hmult) ^ ht[ch & 0xff];
            h = (h * hmult) ^ ht[(ch >>> 8) & 0xff];
        }
        return h;
    }

    public static String maskEmail(String email) {
        if (email == null || email.indexOf(Char.AT) == -1)
            return email;

        int atPos = email.indexOf(Char.AT);
        int lastDotPos = email.lastIndexOf(Char.DOT);

        if (lastDotPos == -1 || lastDotPos < atPos)
            lastDotPos = email.length() - 1;

        StringBuilder masked = new StringBuilder();

        if (atPos < 4) {
            for (int i = 0; i < atPos; i++) {
                masked.append(X);
            }

            masked.append(email.substring(atPos, lastDotPos + 1)).append(X);

            return masked.toString();
        } else {
            for (int i = 0; i < 3; i++) {
                masked.append(X);
            }

            masked.append(email.substring(3, atPos)).append(email.substring(atPos, lastDotPos + 1)).append(X);

            return masked.toString();
        }
    }

    public static String truncateNicely(String text, int maxLength, String append) {
        if (text != null && text.length() > maxLength) {
            BreakIterator bi = BreakIterator.getWordInstance();
            bi.setText(text);
            int first_before = bi.preceding(maxLength);

            if (first_before == 0) {
                String newText = new StringBuilder(text.substring(0, maxLength)).toString().trim();

                if (newText.endsWith(Str.COMMA) || newText.endsWith(Str.MINUS))
                    return new StringBuilder(newText.substring(0, newText.length() - 1)).append(Char.SPACE).append(append).toString();
                else
                    return new StringBuilder(newText).append(Char.SPACE).append(append).toString();
            } else {
                String newText = new StringBuilder(text.substring(0, first_before)).toString().trim();

                if (newText.endsWith(Str.COMMA) || newText.endsWith(Str.MINUS))
                    return new StringBuilder(newText.substring(0, newText.length() - 1)).append(Char.SPACE).append(append).toString();
                else
                    return new StringBuilder(newText).append(Char.SPACE).append(append).toString();
            }
        } else {
            return text;
        }
    }

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();

        sb.append("<ul>\n");
        sb.append("<li>moderná kuchyňa v prírodnom farebnom prevedení</li>\n");
        sb.append("<li>predná plocha MDF 22 mm silná , polymerová fólia</li>\n");
        sb.append("<li>povrch odolný proti poškrábaniu</li>\n");
        sb.append("<li>ľahká údržba</li>\n");
        sb.append("<li>široký výber skriniek</li>\n");
        sb.append("<li>možnosť kombinácie so sklom</li>\n");
        sb.append("<li>individuálne plánovanie na mieru</li>\n");
        sb.append("<li>možnosť kombinácie so spotrebičmi za zvýhodnené ceny</li>\n");
        sb.append("</ul>\n");

        // sb.append("moderná kuchyňa v prírodnom farebnom prevedení\n");
        // sb.append("predná plocha MDF 22 mm silná , polymerová fólia\n");
        // sb.append("povrch odolný proti poškrábaniu\n");
        // sb.append("ľahká údržba\n");
        // sb.append("široký výber skriniek\n");
        // sb.append("možnosť kombinácie so sklom\n");
        // sb.append("individuálne plánovanie na mieru\n");
        // sb.append("možnosť kombinácie so spotrebičmi za zvýhodnené ceny\n");

        System.out.println(truncateHtmlList(sb.toString(), 120, 3, "..."));
    }

    /**
     * Truncate HTML nicely, ensuring that it is still valid.
     * 
     * @param String
     *            html
     * @param int
     *            maxLength
     * @return String truncated text
     */
    public static String truncateHtmlList(String html, int maxLength, int maxRows, String append) {
        if (Str.isEmpty(html))
            return "";

        ByteArrayInputStream bais = null;

        try {
            // String charset = app.getSystemCharset();
            String charset = "UTF-8";

            // Attempt a charset safe way of parsing the string.
            bais = new ByteArrayInputStream(html.getBytes(charset));
            Document doc = Jsoup.parse(bais, charset, Str.EMPTY);
            doc.outputSettings().charset(charset);
            Cleaner cleaner = new Cleaner(new Whitelist().addTags("ul", "li"));
            Document clean = cleaner.clean(doc);
            clean.outputSettings().escapeMode(EscapeMode.xhtml);

            List<Node> nodes = clean.body().childNodes();

            int totalLen = 0;
            int numRows = 0;
            boolean isMaxReached = false;
            StringBuilder newText = new StringBuilder();

            for (Node node : nodes) {
                if (!isMaxReached && "ul".equals(node.nodeName())) {
                    newText.append("<ul>\n");

                    List<Node> childNodes = node.childNodes();
                    for (Node childNode : childNodes) {
                        if ("li".equals(childNode.nodeName())) {
                            if (childNode.childNodeSize() > 0 && !Str.isEmpty(childNode.childNode(0).toString())) {
                                String s = childNode.childNode(0).toString().trim();
                                int len = s.length();

                                if ((totalLen + len) <= maxLength && (numRows < maxRows || maxRows == 0)) {
                                    totalLen += len;
                                    numRows++;
                                    newText.append("<li>").append(s).append("</li>\n");
                                } else if ((maxLength - totalLen) >= 10 && len >= 10 && (numRows < maxRows || maxRows == 0)) {
                                    String truncated = truncateNicely(s, maxLength - totalLen, append);

                                    if ((truncated.length() - 4) >= 10)
                                        newText.append("<li>").append(truncated).append("</li>\n");

                                    isMaxReached = true;
                                } else {
                                    isMaxReached = true;
                                }
                            }
                        }

                        if (isMaxReached)
                            break;
                    }

                    newText.append("</ul>\n");
                } else if (!isMaxReached && node.childNodeSize() > 0) {
                    if (!Str.isEmpty(node.childNode(0).toString())) {
                        String s = node.childNode(0).toString().trim();
                        int len = s.length();

                        if ((totalLen + len) <= maxLength && (numRows < maxRows || maxRows == 0)) {
                            totalLen += len;
                            numRows++;
                            newText.append("<").append(node.nodeName()).append(">").append(s).append("</").append(node.nodeName()).append(">\n");
                        } else if ((maxLength - totalLen) >= 10 && (numRows < maxRows || maxRows == 0)) {
                            newText.append("<").append(node.nodeName()).append(">").append(truncateNicely(s, maxLength - totalLen, append)).append("</").append(node.nodeName()).append(">\n");
                            isMaxReached = true;
                        } else {
                            isMaxReached = true;
                        }
                    }
                } else if (!isMaxReached && "#text".equals(node.nodeName())) {
                    if (!Str.isEmpty(node.toString())) {
                        String s = node.toString().trim();
                        int len = s.length();

                        if ((totalLen + len) <= maxLength && (numRows < maxRows || maxRows == 0)) {
                            totalLen += len;
                            numRows++;
                            newText.append(s).append("\n");
                        } else if ((maxLength - totalLen) >= 10 && (numRows < maxRows || maxRows == 0)) {
                            newText.append(truncateNicely(s, maxLength - totalLen, append)).append("\n");
                            isMaxReached = true;
                        } else {
                            isMaxReached = true;
                        }
                    }
                }
            }

            return newText.toString();
        } catch (Throwable t) {
        } finally {
            IOUtils.closeQuietly(bais);
        }

        return "";
    }

    public static String stripTags(String html) {
        return HtmlUtils.stripTags(html);
    }

    public static String stripNewlines(String text) {
        text = text.trim();
        text = text.replace(ENTITY_NBSP, EMPTY_STRING);
        text = text.replace(NEWLINE_R, EMPTY_STRING);
        return text.replace(NEWLINE_N, EMPTY_STRING);
    }

    public static String replaceNewlines(String text, String replaceNewlineWith) {
        text = text.trim();
        text = text.replace(ENTITY_NBSP, EMPTY_STRING);
        text = text.replace(NEWLINE_R, EMPTY_STRING);

        if (replaceNewlineWith.trim().equals(COMMA)) {
            text = text.replace(NEWLINE_N, new StringBuilder(replaceNewlineWith).append(SPACE).toString());
            text = text.replace(SPACE_COMMA, COMMA);
        } else if (replaceNewlineWith.trim().equals(DOT)) {
            text = text.replace(NEWLINE_N, new StringBuilder(replaceNewlineWith).append(SPACE).toString());
            text = text.replace(SPACE_DOT, DOT);
        } else {
            text = text.replace(NEWLINE_N, replaceNewlineWith);
        }

        return text;
    }

    public static String toCsvString(Collection<?> items) {
        if (items == null)
            return null;

        StringBuilder sb = new StringBuilder();

        int i = 0;

        for (Object o : items) {
            if (i > 0) {
                sb.append(COMMA_SPACE);
            }

            sb.append(o);

            i++;
        }

        return sb.toString();
    }

    public static String slugify(String text) {
        return slugify(text, null);
    }

    public static String slugify2(String text) {
        return slugify2(text, null);
    }

    public static String slugify(String text, String transliterationCode) {
        if (Str.isEmpty(text))
            return text;

        if (transliterationCode == null)
            transliterationCode = DEFAULT_TRANSLITERATION_CODE;

        String transliteratedText = text.replace(ae, transliterateReplaceChars.get(ae)).replace(oe, transliterateReplaceChars.get(oe)).replace(ue, transliterateReplaceChars.get(ue))
                .replace(Ae, transliterateReplaceChars.get(Ae))
                .replace(Oe, transliterateReplaceChars.get(Oe)).replace(Ue, transliterateReplaceChars.get(Ue)).replace(ss, transliterateReplaceChars.get(ss))
                .replace(Str.AT, transliterateReplaceChars.get(Str.AT))
                .replace(Str.AMPERSAND, transliterateReplaceChars.get(Str.AMPERSAND));

        Transliterator t = transliteratorCache.get(transliterationCode);

        if (t == null) {
            t = Transliterator.getInstance(transliterationCode);
            transliteratorCache.put(transliterationCode, t);
        }

        transliteratedText = t.transform(transliteratedText);

        transliteratedText = Normalizer.normalize(transliteratedText, Normalizer.Form.NFD).replace(Char.UNDERSCORE, Char.MINUS).replace(Char.SPACE, Char.MINUS).replace(Char.COMMA, Char.MINUS)
                .replace(Char.DOT, Char.MINUS);

        Matcher m1 = slugifyReplacePattern1.matcher(transliteratedText);
        transliteratedText = m1.replaceAll(Str.EMPTY);

        Matcher m2 = slugifyReplacePattern2.matcher(transliteratedText);
        transliteratedText = m2.replaceAll(Str.EMPTY);

        Matcher m3 = slugifyReplacePattern3.matcher(transliteratedText);
        transliteratedText = m3.replaceAll(Str.MINUS);

        Matcher m4 = slugifyReplacePattern4.matcher(transliteratedText);
        transliteratedText = m4.replaceAll(Str.SPACE);

        return transliteratedText.toLowerCase();
    }

    public static String slugify2(String text, String transliterationCode) {
        if (Str.isEmpty(text))
            return text;

        if (transliterationCode == null) {
            transliterationCode = DEFAULT_TRANSLITERATION_CODE;
        }

        String transliteratedText = text.replace(ae, transliterateReplaceChars.get(ae)).replace(oe, transliterateReplaceChars.get(oe)).replace(ue, transliterateReplaceChars.get(ue))
                .replace(Ae, transliterateReplaceChars.get(Ae))
                .replace(Oe, transliterateReplaceChars.get(Oe)).replace(Ue, transliterateReplaceChars.get(Ue)).replace(ss, transliterateReplaceChars.get(ss))
                .replace(Str.AT, transliterateReplaceChars.get(Str.AT))
                .replace(Str.AMPERSAND, transliterateReplaceChars.get(Str.AMPERSAND));

        Transliterator t = transliteratorCache.get(transliterationCode);

        if (t == null) {
            t = Transliterator.getInstance(transliterationCode);
            transliteratorCache.put(transliterationCode, t);
        }

        transliteratedText = t.transform(transliteratedText);

        transliteratedText = Normalizer.normalize(transliteratedText, Normalizer.Form.NFD).replace(Char.UNDERSCORE, Char.MINUS).replace(Char.SPACE, Char.UNDERSCORE).replace(Char.COMMA, Char.MINUS)
                .replace(Char.DOT, Char.MINUS);

        Matcher m1 = slugifyReplacePattern1.matcher(transliteratedText);
        transliteratedText = m1.replaceAll(Str.EMPTY);

        Matcher m2 = slugifyReplacePattern2.matcher(transliteratedText);
        transliteratedText = m2.replaceAll(Str.MINUS);

        Matcher m3 = slugifyReplacePattern3.matcher(transliteratedText);
        transliteratedText = m3.replaceAll(Str.MINUS);

        Matcher m4 = slugifyReplacePattern4.matcher(transliteratedText);
        transliteratedText = m4.replaceAll(Str.SPACE);

        return transliteratedText.toLowerCase();
    }

    public static String transliterate(String text) {
        return transliterate(text, null);
    }

    public static String transliterate(String text, String transliterationCode) {
        if (Str.isEmpty(text))
            return text;

        if (transliterationCode == null) {
            transliterationCode = DEFAULT_TRANSLITERATION_CODE;
        }

        String transliteratedText = text.replace(ae, transliterateReplaceChars.get(ae)).replace(oe, transliterateReplaceChars.get(oe)).replace(ue, transliterateReplaceChars.get(ue))
                .replace(Ae, transliterateReplaceChars.get(Ae))
                .replace(Oe, transliterateReplaceChars.get(Oe)).replace(Ue, transliterateReplaceChars.get(Ue)).replace(ss, transliterateReplaceChars.get(ss))
                .replace(Str.AT, transliterateReplaceChars.get(Str.AT))
                .replace(Str.AMPERSAND, transliterateReplaceChars.get(Str.AMPERSAND));

        Transliterator t = transliteratorCache.get(transliterationCode);

        if (t == null) {
            t = Transliterator.getInstance(transliterationCode);
            transliteratorCache.put(transliterationCode, t);
        }

        transliteratedText = t.transform(transliteratedText);

        return Normalizer.normalize(transliteratedText, Normalizer.Form.NFD);
    }

    public static String random(int length) {
        StringBuilder randomPassword = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int idx = i < randomCharArrays.size() ? i : (int) (Math.random() * randomCharArrays.size());
            char[] chars = randomCharArrays.get(idx);

            int idx2 = (int) (Math.random() * chars.length);
            randomPassword.append(chars[idx2]);
        }

        return randomPassword.toString();
    }
}
