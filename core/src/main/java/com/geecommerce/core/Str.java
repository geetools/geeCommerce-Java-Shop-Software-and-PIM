package com.geecommerce.core;

import com.geecommerce.core.util.Strings;

public class Str {
    public static final String SPACE = String.valueOf(Char.SPACE);
    public static final String DOUBLE_SPACE = "  ";
    public static final String SLASH = String.valueOf(Char.SLASH);
    public static final String SLASH_2X = "//";
    public static final String SLASH_ESCAPED = "\\" + Char.SLASH;
    public static final String DOT = String.valueOf(Char.DOT);
    public static final String DOT_ESCAPED = "\\" + Char.DOT;
    public static final String AT = String.valueOf(Char.AT);
    public static final String CARET = String.valueOf(Char.CARET);
    public static final String COMMA = String.valueOf(Char.COMMA);
    public static final String COLON = String.valueOf(Char.COLON);
    public static final String SEMI_COLON = String.valueOf(Char.SEMI_COLON);
    public static final String ASTERIX = String.valueOf(Char.ASTERIX);
    public static final String EQUALS = String.valueOf(Char.EQUALS);
    public static final String PLUS = String.valueOf(Char.PLUS);
    public static final String MINUS = String.valueOf(Char.MINUS);
    public static final String MINUS_ESCAPED = "\\" + Char.MINUS;
    public static final String UNDERSCORE = String.valueOf(Char.UNDERSCORE);
    public static final String UNDERSCORE_2X = "__";
    public static final String UNDERSCORE_3X = "___";
    public static final String AMPERSAND = String.valueOf(Char.AMPERSAND);
    public static final String BACKSLASH = String.valueOf(Char.BACKSLASH);
    public static final String BACKSLASH_ESCAPED = "\\" + Char.BACKSLASH;
    public static final String SMALLER_THAN = String.valueOf(Char.SMALLER_THAN);
    public static final String LARGER_THAN = String.valueOf(Char.LARGER_THAN);
    public static final String EXCLAMATION_MARK = String.valueOf(Char.EXCLAMATION_MARK);
    public static final String QUESTION_MARK = String.valueOf(Char.QUESTION_MARK);
    public static final String HASH = String.valueOf(Char.HASH);
    public static final String PERCENT = String.valueOf(Char.PERCENT);
    public static final String EUR = String.valueOf(Char.EUR);
    public static final String GBP = String.valueOf(Char.GBP);
    public static final String DOLLAR = String.valueOf(Char.DOLLAR);
    public static final String DOLLAR_ESCAPED = "\\" + Char.DOLLAR;
    public static final String SINGLE_QUOTE = String.valueOf(Char.SINGLE_QUOTE);
    public static final String SINGLE_QUOTE_ESCAPED = "\\" + Char.SINGLE_QUOTE;
    public static final String DOUBLE_QUOTE = String.valueOf(Char.DOUBLE_QUOTE);
    public static final String DOUBLE_QUOTE_ESCAPED = "\\" + Char.DOUBLE_QUOTE;
    public static final String BRACKET_OPEN = String.valueOf(Char.BRACKET_OPEN);
    public static final String BRACKET_OPEN_ESCAPED = "\\" + Char.BRACKET_OPEN;
    public static final String BRACKET_CLOSE = String.valueOf(Char.BRACKET_CLOSE);
    public static final String BRACKET_CLOSE_ESCAPED = "\\" + Char.BRACKET_CLOSE;
    public static final String SQUARE_BRACKET_OPEN = String.valueOf(Char.SQUARE_BRACKET_OPEN);
    public static final String SQUARE_BRACKET_CLOSE = String.valueOf(Char.SQUARE_BRACKET_CLOSE);
    public static final String CURLY_BRACKET_OPEN = String.valueOf(Char.CURLY_BRACKET_OPEN);
    public static final String CURLY_BRACKET_CLOSE = String.valueOf(Char.CURLY_BRACKET_CLOSE);
    public static final String PIPE = String.valueOf(Char.PIPE);
    public static final String NEWLINE = String.valueOf(Char.NEWLINE);
    public static final String PROTOCOL_SUFFIX = "://";
    public static final String EMPTY = "";
    public static final String NUL = String.valueOf(Char.NUL);

    public static boolean isEmpty(String s) {
	if (s == null)
	    return true;

	return EMPTY.equals(s.trim());
    }

    public static boolean trimEquals(String s1, String s2) {
	if (s1 == s2)
	    return true;

	if (s1 == null && s2 == null)
	    return true;

	if ((s1 != null && s2 == null) || (s1 == null && s2 != null))
	    return false;

	return s1.trim().equals(s2.trim());
    }

    public static boolean trimEqualsIgnoreCase(String s1, String s2) {
	if (s1 == s2)
	    return true;

	if (s1 == null && s2 == null)
	    return true;

	if ((s1 != null && s2 == null) || (s1 == null && s2 != null))
	    return false;

	return s1.trim().equalsIgnoreCase(s2.trim());
    }

    public static boolean trimNormalizedEqualsIgnoreCase(String s1, String s2) {
	if (s1 == s2)
	    return true;

	if (s1 == null && s2 == null)
	    return true;

	if ((s1 != null && s2 == null) || (s1 == null && s2 != null))
	    return false;

	String t1 = Strings.transliterate(s1.trim().replace(Str.DOUBLE_SPACE, Str.SPACE));
	String t2 = Strings.transliterate(s2.trim().replace(Str.DOUBLE_SPACE, Str.SPACE));

	return t1.equalsIgnoreCase(t2);
    }
}
