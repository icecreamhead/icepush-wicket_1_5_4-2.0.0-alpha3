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
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.icepush.samples.icechat.cdi.model.NewChatRoomBean;
import org.icepush.samples.icechat.model.ChatRoom;

/**
 *
 */
public final class ChatRoomsPanel extends PushPanel {

	@Inject
	ChatManagerViewControllerSessionBean chatManagerVC;

	NewChatRoomBean newChatRoomBean = new NewChatRoomBean();
	CompoundPropertyModel compoundNewChatRoomBean = new CompoundPropertyModel(
			newChatRoomBean);

	final ListView chatRoomsListView;
	Form chatRooms;

	public ChatRoomsPanel(String id, IModel compoundChatManagerFacadeBean) {
		super(id);
		chatManagerVC.setPushRequestContext(pushRequestContext);
		chatRooms = new Form("chatRoomsForm", compoundChatManagerFacadeBean);
		chatRooms.setOutputMarkupId(true);
		chatRooms.add(chatRoomsListView = new ListView("chatRooms") {
			public void populateItem(final ListItem listItem) {
				final ChatRoom chatRoom = (ChatRoom) listItem.getModelObject();
				listItem.add(new AjaxButton("name", new Model(chatRoom
						.getName())) {
					protected void onSubmit(AjaxRequestTarget target, Form form) {
						chatManagerVC.openChatSession(chatRoom.getName());
						setResponsePage(getPage());
					}
				});

			}
		});

		add(chatRooms);

		Form createNewChatRoom = new Form("createNewChatRoomForm",
				compoundNewChatRoomBean);
		final AjaxButton createNewRoomBtn = new AjaxButton("registerButton") {
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				chatManagerVC.setNewChatRoomBean(newChatRoomBean);
				chatManagerVC.createNewChatRoom();
				chatRoomsListView.modelChanged();
				target.addComponent(chatRooms);
				chatManagerVC.openChatSession(newChatRoomBean.getName());
				setResponsePage(getPage());
			}
		};
		createNewChatRoom.add(new RequiredTextField<String>("name") {
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.put("onkeypress",
						"if (event.keyCode == 13) { document.getElementById('"
								+ createNewRoomBtn.getMarkupId()
								+ "').click(); return false;}");
			}
		});
		createNewChatRoom.add(createNewRoomBtn);
		add(createNewChatRoom);
	}

	protected void pushCallback(AjaxRequestTarget target) {
		chatRoomsListView.modelChanged();
		target.addComponent(chatRooms);
	}

}
