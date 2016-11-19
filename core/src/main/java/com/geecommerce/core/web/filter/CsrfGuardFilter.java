/**
 * The OWASP CSRFGuard Project, BSD License
 * Eric Sheridan (eric@infraredsecurity.com), Copyright (c) 2011 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *    3. Neither the name of OWASP nor the names of its contributors may be used
 *       to endorse or promote products derived from this software without specific
 *       prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.geecommerce.core.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.owasp.csrfguard.CsrfGuard;
import org.owasp.csrfguard.http.InterceptRedirectResponse;

import com.geecommerce.core.util.Requests;

public final class CsrfGuardFilter implements Filter {

    private static final String DEFAULT_REGEX_MEDIA_EXTENSIONS = ".+\\.(gif|jpg|jpeg|png|bmp|ico|rar|css|js|zip|gz|flv|swf|mp3|mp4|m4v|webm|weba|ogm|ogv|ogg|doc|docx|ppt|pptx|xls|xlsx|pdf|txt|csv)$";

    private static final String DEFAULT_REGEX_MEDIA_PATHS = "^\\/(static|skin|js|css|cache|c\\/media)\\/.+";

    private static final String IGNORE_URI = "/proxypay/";

    private FilterConfig filterConfig = null;

    @Override
    public void destroy() {
	filterConfig = null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
	/** only work with HttpServletRequest objects **/
	if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
	    HttpServletRequest httpRequest = (HttpServletRequest) request;
	    HttpServletResponse httpResp = (HttpServletResponse) response;
	    HttpSession session = httpRequest.getSession(false);

	    httpRequest.setCharacterEncoding("UTF-8");
	    httpResp.setCharacterEncoding("UTF-8");

	    if (session == null || isIgnoreURI(httpRequest.getRequestURI()) || Requests.isAjaxRequest(httpRequest) || Requests.isAPIRequest(httpRequest.getRequestURI()) || Requests.isCMSRequest(httpRequest.getRequestURI())
		    || isMediaRequest(httpRequest.getRequestURI()) || !Requests.isPostRequest(httpRequest)) {
		// If there is no session, no harm can be done
		filterChain.doFilter(httpRequest, httpResp);

		if (session != null) {
		    /** update tokens **/
		    CsrfGuard csrfGuard = CsrfGuard.getInstance();
		    csrfGuard.updateTokens(httpRequest);
		}

		return;
	    }

	    CsrfGuard csrfGuard = CsrfGuard.getInstance();
	    csrfGuard.getLogger().log(String.format("CsrfGuard analyzing request %s", httpRequest.getRequestURI()));

	    InterceptRedirectResponse httpResponse = new InterceptRedirectResponse(httpResp, httpRequest, csrfGuard);

	    // if(MultipartHttpServletRequest.isMultipartRequest(httpRequest)) {
	    // httpRequest = new MultipartHttpServletRequest(httpRequest);
	    // }

	    if (session.isNew() && csrfGuard.isUseNewTokenLandingPage()) {
		csrfGuard.writeLandingPage(httpRequest, httpResponse);
	    } else if (csrfGuard.isValidRequest(httpRequest, httpResponse)) {
		filterChain.doFilter(httpRequest, httpResponse);
	    } else {
		/** invalid request - nothing to do - actions already executed **/
	    }

	    /** update tokens **/
	    csrfGuard.updateTokens(httpRequest);

	} else {
	    filterConfig.getServletContext().log(String.format("[WARNING] CsrfGuard does not know how to work with requests of class %s ", request.getClass().getName()));

	    filterChain.doFilter(request, response);
	}
    }

    private boolean isIgnoreURI(String requestURI) {
	return requestURI.startsWith(IGNORE_URI);
    }

    @Override
    public void init(@SuppressWarnings("hiding") FilterConfig filterConfig) throws ServletException {
	this.filterConfig = filterConfig;
    }

    protected boolean isMediaRequest(String uri) {
	return uri.matches(DEFAULT_REGEX_MEDIA_EXTENSIONS) || uri.matches(DEFAULT_REGEX_MEDIA_PATHS);
    }
}
