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
import java.util.logging.Logger;

import org.icepush.PushContext;
import org.icepush.samples.icechat.IPushRequestContext;
import org.icepush.samples.icechat.controller.IChatRoomController;
import org.icepush.samples.icechat.model.ChatRoom;
import org.icepush.samples.icechat.model.User;
import org.icepush.samples.icechat.model.UserChatSession;
import org.icepush.samples.icechat.service.IChatService;
import org.icepush.samples.icechat.service.exception.UnauthorizedException;

public abstract class BaseChatRoomControllerBean implements Serializable, IChatRoomController{
	
	private static final long serialVersionUID = 3046754615536057774L;

	protected IChatService chatService;
	
	protected IPushRequestContext pushRequestContext;
	
	private static Logger log = Logger.getLogger(BaseChatRoomControllerBean.class.getName());
	
	public void setPushRequestContext(IPushRequestContext pushRequestContext) {
		this.pushRequestContext = pushRequestContext;
	}
    
    public IPushRequestContext getPushRequestContext() {
        return pushRequestContext;
    }

	public void createNewChatRoom(String chatRoomName){
        chatService.createNewChatRoom(chatRoomName);
		PushContext pushContext = this.getPushRequestContext().getPushContext();
        pushContext.addGroupMember(chatRoomName,pushRequestContext.getCurrentPushId());
        pushContext.push("CHAT_ROOMS");
	}
	
	public UserChatSession openChatSession(String chatRoomName, User user){
		UserChatSession session = null;
		for( ChatRoom room : chatService.getChatRooms() ){
			if( room.getName().equals(chatRoomName)){
				session = chatService.loginToChatRoom(chatRoomName, user);
				PushContext pushContext = this.getPushRequestContext().getPushContext();
		        pushContext.addGroupMember(chatRoomName,pushRequestContext.getCurrentPushId());
		        pushContext.push(chatRoomName);
			}
		}		
		return session;
	}
	
	public void sendNewMessage(String chatRoomName, String newMessage, User user){
		chatService.sendNewMessage(chatRoomName, user, newMessage);
		PushContext pushContext = this.getPushRequestContext().getPushContext();
        pushContext.push(chatRoomName);
	}
	
	public void setChatService(IChatService chatService){
            this.chatService = chatService;
	}
	
	public IChatService getChatService(){
        return chatService;
}


}
