package com.geecommerce.customer.interceptor;

import com.geemvc.intercept.annotation.PreView;
import com.geemvc.view.bean.Result;

/**
 * Created by Michael on 14.07.2016.
 */
@PreView(on = { "/forgot-password", "/forgot-password-confirm" })
public class ForgottonPasswordInterceptor {
    public void intercept(Result result) {
    }
}
