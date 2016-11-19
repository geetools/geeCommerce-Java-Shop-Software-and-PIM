package com.geecommerce.core.customer.account;

import java.util.Map;

import com.geecommerce.core.Constant;
import com.geecommerce.core.utils.Filenames;

public abstract class AbstractLoginMethod {
    public abstract int getSortIndex();

    public abstract String getProvider();

    public abstract String getCode();

    public abstract String getLabel();

    public abstract LoginResponse processLogin(Map<String, Object> requestParameters);

    public String getFrontendFormPath() {
	StringBuilder sb = new StringBuilder();

	sb.append(Constant.LOGIN_METHOD_FRONTEND_FORMS_BASE_PATH).append("/").append(Filenames.ensureSafeName(getProvider(), true)).append("/").append(Filenames.ensureSafeName(getLabel(), true)).append("/")
		.append(Constant.LOGIN_METHOD_FRONTEND_FORMS_FILE_NAME);

	return sb.toString();
    }
}
