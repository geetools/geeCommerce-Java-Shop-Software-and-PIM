package com.geecommerce.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Num {
    private static final Pattern isNumericPattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final Pattern isWholeNumberPattern = Pattern.compile("-?\\d+");
    private static final Pattern isDecimalNumberPattern = Pattern.compile("-?\\d+\\.d+");

    public static boolean isNumeric(String str) {
	Matcher m = isNumericPattern.matcher(str);
	return m.matches();
    }

    public static boolean isWholeNumber(String str) {
	Matcher m = isWholeNumberPattern.matcher(str);
	return m.matches();
    }

    public static boolean isLong(String str) {
	Matcher m = isWholeNumberPattern.matcher(str);
	return m.matches() && str.length() > 9;
    }

    public static boolean isInteger(String str) {
	Matcher m = isWholeNumberPattern.matcher(str);
	return m.matches() && str.length() < 10;
    }

    public static boolean isDouble(String str) {
	Matcher m = isDecimalNumberPattern.matcher(str);
	return m.matches();
    }
}
