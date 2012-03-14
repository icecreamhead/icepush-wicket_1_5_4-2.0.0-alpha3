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

package org.icepush.samples.icechat.beans.facade;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.icepush.samples.icechat.model.ChatRoom;
import org.icepush.samples.icechat.model.Message;
import org.icepush.samples.icechat.model.User;
import org.icepush.samples.icechat.service.IChatService;

public abstract class BaseChatManagerFacadeBean implements Serializable{
	
	private IChatService chatService;

	public void setChatService(IChatService chatService) {
		this.chatService = chatService;
	}

	public List<Message> getAllChatRoomMessages(String chatRoom) {
		return chatService.getAllChatRoomMessages(chatRoom);
	}

	public List<Message> getChatRoomMessagesFromIndex(String chatRoom, int index) {
		return chatService.getChatRoomMessagesFromIndex(chatRoom, index);
	}

	public List<ChatRoom> getChatRooms() {
		return chatService.getChatRooms();
	}

	public Collection<User> getOnlineUsers() {
		return chatService.getOnlineUsers();
	}
	
	
	
}
