package com.geecommerce.core.web.stripes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.util.Requests;

import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.FileUploadLimitExceededException;
import net.sourceforge.stripes.exception.DefaultExceptionHandler;

public class ExceptionHandler extends DefaultExceptionHandler {
    @Override
    protected Resolution handle(FileUploadLimitExceededException exception, HttpServletRequest request, HttpServletResponse response) throws FileUploadLimitExceededException {
	String referrer = Requests.getReferrerURI(request);

	return new RedirectResolution(referrer);
    }
}
