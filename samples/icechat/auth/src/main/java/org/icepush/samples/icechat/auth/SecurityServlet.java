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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.icepush.PushContext;
import org.icepush.samples.icechat.model.User;
import org.icepush.samples.icechat.model.UserChatSession;
import org.icepush.samples.icechat.service.IChatService;

public class SecurityServlet extends HttpServlet {
	
	public static final String OP = "op";
	public static final String USER_NAME = "userName";
	public static final String LOGIN = "login";
	public static final String LOGOUT = "logout";
	static final String USER_KEY = "user";
	private String homePageURL;
	private static final String HOME_PAGE_URL_KEY = "homePageURL";
	public static final String RESOURCE_PARAM_KEY = "res";
	
	private static Logger LOG = Logger.getLogger(SecurityServlet.class.getName());
	
	private IChatService getChatService(){
		return (IChatService)this.getServletContext().getAttribute("chatService");
	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String op = req.getParameter(OP);
		
		if( LOGOUT.equals(op)){
			User user = (User)req.getSession().getAttribute(USER_KEY);
			if( user != null ){
				for( UserChatSession session : user.getChatSessions() ){
					session.getRoom().getUserChatSessions().remove(session);					
					PushContext pushContext = PushContext.getInstance(req.getSession().getServletContext());
					pushContext.push(session.getRoom().getName()+"_users");
					session = null;
				}
				req.setAttribute(USER_KEY, null);				
				LOG.info(user.getName() + " logged out ");
			}
			req.getSession().invalidate();
			return;
		}
		else if( LOGIN.equals(op) && req.getAttribute(USER_KEY) != null ){
			dispatch(homePageURL,req,resp);
			return;
		}
		else if( LOGIN.equals(op)){
			
			String userName = req.getParameter(USER_NAME);
			
			if( userName != null ){				
				
				HttpSession session = req.getSession(true);
				User user = null;
				
				
				if( LOGIN.equals(op) ){
					LOG.info("logging in " + userName);
					user = getChatService().login(userName);
				}
				session.setAttribute(USER_KEY,user);
				LOG.info("session: " + session.getId() + " user=" + user);
				resp.setStatus(200);
				return;
			}
			
		}
		resp.setStatus(401);
		
	}
	
	private void dispatch(String url, HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {		
		getServletConfig().getServletContext().getRequestDispatcher(url).forward(req,resp);
	}

	@Override
	public void init() throws ServletException {
		super.init();
		homePageURL = getServletConfig().getServletContext().getInitParameter(HOME_PAGE_URL_KEY);
	}
	

}
