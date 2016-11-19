package com.geecommerce.core.system.helper;

import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.util.Strings;

@Helper
public class DefaultContextMessageHelper implements ContextMessageHelper {
    @Override
    public String toKey(String message) {
	String key = null;

	// We do not want an endlessly long key, so we turn it into an MD5 string if it has more than 128 characters.
	if (message.length() > 128) {
	    key = Strings.toMD5(message);
	} else {
	    key = message;
	}

	return key;
    }
}
