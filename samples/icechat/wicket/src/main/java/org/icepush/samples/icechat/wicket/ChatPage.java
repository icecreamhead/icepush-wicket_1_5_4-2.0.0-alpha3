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

package org.icepush.samples.icechat.wicket;

import javax.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.icepush.samples.icechat.cdi.facade.ChatManagerFacadeBean;
import org.icepush.samples.icechat.cdi.model.CurrentChatSessionHolderBean;
import org.icepush.samples.icechat.controller.ILoginController;

/**
 * 
 */
public final class ChatPage extends AppBasePage {

	// Session Scoped Beans
	@Inject
	ILoginController loginController;
	CompoundPropertyModel compoundLoginController;

	@Inject
	ChatManagerFacadeBean chatManagerFacadeBean;
	CompoundPropertyModel compoundChatManagerFacadeBean;

	@Inject
	ChatManagerViewControllerSessionBean chatManagerVC;

	CurrentChatSessionHolderBean currentChatSessionHolderBean = new CurrentChatSessionHolderBean();

	public ChatPage() {
		super();
		// Initialize Model
		chatManagerVC.setChatService(chatService);
		chatManagerVC.setLoginController(loginController);
		chatManagerVC.setCurrentChatSessionHolder(currentChatSessionHolderBean);
		chatManagerFacadeBean.setChatService(chatService);

		compoundLoginController = new CompoundPropertyModel(loginController);
		compoundChatManagerFacadeBean = new CompoundPropertyModel(
				chatManagerFacadeBean);

		// Add Components to page
		Form chatSession = new Form("chatSession", compoundLoginController);
		chatSession.add(new Label("userName"));
		chatSession.add(new AjaxButton("logout") {
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				loginController.logout();
				setResponsePage(LoginPage.class);
			}
		});
		add(chatSession);

		add(new ChatRoomsPanel("CHAT_ROOMS", compoundChatManagerFacadeBean));

		add(new ChatPanel("chatPanel"));

	}

}
