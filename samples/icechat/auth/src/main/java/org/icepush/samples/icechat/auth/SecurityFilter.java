/*
 * Version: MPL 1.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2010 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
*/

package org.icepush.samples.icechat.auth;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class SecurityFilter implements Filter {
	
	private String loginURL;
	private static final String LOGIN_URL_KEY = "loginURL";
	private FilterConfig filterConfig;
	
	private static Logger LOG = Logger.getLogger(SecurityFilter.class.getName());
	
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		
		if( httpRequest.getRequestURI().contains(loginURL)){
			chain.doFilter(request, response);
		}
		else if( httpRequest.getSession(false) == null || 
				httpRequest.getSession(true).getAttribute(SecurityServlet.USER_KEY) == null ){
			LOG.info("unauthorized request '" + httpRequest.getRequestURI() + "', directing to " + loginURL);
			filterConfig.getServletContext().getRequestDispatcher(loginURL).forward(request,response);
		}
		else{
			chain.doFilter(request, response);
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {		
		this.filterConfig = filterConfig;
		loginURL = filterConfig.getServletContext().getInitParameter(LOGIN_URL_KEY);
	}

}
