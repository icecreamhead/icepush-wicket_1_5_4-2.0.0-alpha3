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

package org.icepush.samples.icechat.beans.view;

import java.io.Serializable;

import org.icepush.samples.icechat.beans.controller.BaseChatRoomControllerBean;
import org.icepush.samples.icechat.controller.ILoginController;
import org.icepush.samples.icechat.controller.model.ICurrentChatSessionHolderBean;
import org.icepush.samples.icechat.controller.model.INewChatRoomBean;
import org.icepush.samples.icechat.controller.model.INewChatRoomMessageBean;
import org.icepush.samples.icechat.model.UserChatSession;
import org.icepush.samples.icechat.service.exception.UnauthorizedException;
import org.icepush.samples.icechat.view.IChatManagerViewController;

public abstract class BaseChatManagerViewControllerBean extends BaseChatRoomControllerBean 
	implements Serializable, IChatManagerViewController{
	
	private ILoginController loginController;
	
	private INewChatRoomBean newChatRoomBean;
	
	protected ICurrentChatSessionHolderBean currentChatSessionHolder;
	
	private INewChatRoomMessageBean newChatRoomMessageBean;
	
	public void setLoginController(ILoginController loginController) {
		this.loginController = loginController;
	}

	public void setNewChatRoomBean(INewChatRoomBean newChatRoomBean) {
		this.newChatRoomBean = newChatRoomBean;
	}

	public void setCurrentChatSessionHolder(
			ICurrentChatSessionHolderBean currentChatSessionHolder) {
		this.currentChatSessionHolder = currentChatSessionHolder;
	}

	public void setNewChatRoomMessageBean(
			INewChatRoomMessageBean newChatRoomMessageBean) {
		this.newChatRoomMessageBean = newChatRoomMessageBean;
	}

	public void createNewChatRoom(){
		createNewChatRoom(newChatRoomBean.getName());
	}
	
	public void openChatSession(String chatRoom){
		UserChatSession session = openChatSession(chatRoom, loginController.getCurrentUser());
		if( session != null )
			currentChatSessionHolder.setSession(session);
	}
	
	public void sendNewMessage(){
		if( currentChatSessionHolder.getSession() != null ){
			sendNewMessage(currentChatSessionHolder.getSession().getRoom().getName(), 
						newChatRoomMessageBean.getMessage(),
						loginController.getCurrentUser());
			newChatRoomMessageBean.setMessage(null);
			
		}
		
	}
	

}
