package com.geecommerce.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.geecommerce.core.Char;
import com.geecommerce.core.Str;

public class BasicValidator {
    /**
     * None of the "standard" solutions for email validation seem to be optimal. The java InternetAddress class and the Apache commons validator
     * accept to many invalid entries. They are RFC compliant, but an address that ends with ",," makes no sense in most e-commerce stores. The regex
     * provided at http://howtodoinjava.com/2014/11/11/java-regex-validate-email-address/ seem to be the most complete and a not too restrictive
     * solution.
     */
    private static final Pattern emailPattern = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");

    public static boolean isValidEmail(String email) {
	if (Str.isEmpty(email))
	    return false;

	// No need to bother with regex if these simple checks fail.
	if (email.indexOf(Char.AT) == -1 || email.indexOf(Char.COLON) != -1 || email.indexOf(Char.COMMA) != -1 || email.indexOf(Char.SEMI_COLON) != -1 || email.indexOf(Char.SPACE) != -1)
	    return false;

	Matcher m = emailPattern.matcher(email);

	return m.matches();
    }
}
