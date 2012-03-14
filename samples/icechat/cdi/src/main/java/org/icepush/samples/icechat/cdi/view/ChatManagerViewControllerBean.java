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

package org.icepush.samples.icechat.cdi.view;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.icepush.samples.icechat.IPushRequestContext;
import org.icepush.samples.icechat.controller.ILoginController;
import org.icepush.samples.icechat.controller.model.ICurrentChatSessionHolderBean;
import org.icepush.samples.icechat.controller.model.INewChatRoomBean;
import org.icepush.samples.icechat.controller.model.INewChatRoomMessageBean;
import org.icepush.samples.icechat.service.IChatService;
import org.icepush.samples.icechat.view.IChatManagerViewController;

@Named(value="chatManagerVC")
@RequestScoped
public class ChatManagerViewControllerBean extends
		org.icepush.samples.icechat.beans.view.BaseChatManagerViewControllerBean
		implements IChatManagerViewController {
	
	
	@Inject
	@Override
	public void setLoginController(ILoginController loginController) {
		super.setLoginController(loginController);
	}
	
	@Inject
	@Override
	public void setNewChatRoomBean(INewChatRoomBean newChatRoomBean) {
		super.setNewChatRoomBean(newChatRoomBean);
	}
	
	@Inject 
	@Override
	public void setCurrentChatSessionHolder(
			ICurrentChatSessionHolderBean currentChatSessionHolder) {
		super.setCurrentChatSessionHolder(currentChatSessionHolder);
	}
	
	@Inject
	@Override
	public void setNewChatRoomMessageBean(
			INewChatRoomMessageBean newChatRoomMessageBean) {
		super.setNewChatRoomMessageBean(newChatRoomMessageBean);
	}
	
	@Inject
	@Override
	public void setChatService(IChatService chatService){
		super.setChatService(chatService);
	}
	
	@Inject
	@Override
	public void setPushRequestContext(IPushRequestContext pushRequestContext) {
		super.setPushRequestContext(pushRequestContext);
	}
}
