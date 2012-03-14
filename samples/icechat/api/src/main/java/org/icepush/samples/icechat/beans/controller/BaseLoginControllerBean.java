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

package org.icepush.samples.icechat.beans.controller;

import java.io.Serializable;

import org.icepush.PushContext;
import org.icepush.samples.icechat.IPushRequestContext;
import org.icepush.samples.icechat.PushConstants;
import org.icepush.samples.icechat.controller.ILoginController;
import org.icepush.samples.icechat.model.User;
import org.icepush.samples.icechat.service.IChatService;
import org.icepush.samples.icechat.service.exception.ConfigurationException;


public abstract class BaseLoginControllerBean implements Serializable, ILoginController{
	
	
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	protected String userName;
	
	protected transient IChatService chatService;
	
	protected transient IPushRequestContext pushRequestContext;

	private User currentUser;

	public IPushRequestContext getPushRequestContext() {
		return pushRequestContext;
	}

	public void setPushRequestContext(IPushRequestContext pushRequestContext) {
		this.pushRequestContext = pushRequestContext;
	}

	public void setChatService(IChatService chatService) {
		this.chatService = chatService;
	}

	public User getCurrentUser() {
		return currentUser;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void login(){
        login(userName);
        addGroupMember(PushConstants.CHAT_USERS.name(), 
				pushRequestContext.getCurrentPushId());
        push(PushConstants.CHAT_USERS.name());
    }
	
	public void login(String userName){
		if(chatService==null)
			throw new ConfigurationException(this.getClass(), "chatService", "null.  Please initialize before calling login()");
		currentUser = chatService.login(userName);
	}

    public void logout(){
    	currentUser = null;
    }
    
    public void addGroupMember(String name, String pushId){
    	PushContext pushContext = pushRequestContext.getPushContext();
    	pushContext.addGroupMember(name, pushId);
    }
    
    public void push(String name){
    	PushContext pushContext = pushRequestContext.getPushContext();
    	pushContext.push(name);
    }

}
