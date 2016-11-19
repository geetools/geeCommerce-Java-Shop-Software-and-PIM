package com.geecommerce.core.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

public class Arr {
    public static Object[] asObjectArray(Object val) {
	if (val == null)
	    return null;

	if (val instanceof Object[])
	    return (Object[]) val;

	int arrlength = Array.getLength(val);
	Object[] outputArray = new Object[arrlength];

	for (int i = 0; i < arrlength; ++i) {
	    outputArray[i] = Array.get(val, i);
	}

	return outputArray;
    }

    public static Collection<Object> asCollection(Object val) {
	if (val == null)
	    return null;

	Object[] outputArray = asObjectArray(val);

	if (outputArray == null)
	    return null;

	return Arrays.asList(outputArray);
    }
}
