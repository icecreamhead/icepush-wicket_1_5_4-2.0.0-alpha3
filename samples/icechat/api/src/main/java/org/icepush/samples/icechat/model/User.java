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

package org.icepush.samples.icechat.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 *
 */
public class User implements Serializable{

    private String name;

    private Collection<UserChatSession> chatSessions = new ArrayList<UserChatSession>();

    private String sessionToken;
    private Date lastTouch = new Date();
    private long id;
    
    
    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User(){
        chatSessions = new ArrayList<UserChatSession>();
    }

    /**
     * @return the userName
     */
    public String getName() {
        return name;
    }

    /**
     * @param userName the userName to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param messages the messages to set
     */
    public void setMessages(Collection<Message> messages) {
        this.setMessages(messages);
    }

    /**
     * @return the chatRooms
     */
    public Collection<UserChatSession> getChatSessions() {
        return chatSessions;
    }

    /**
     * @param chatRooms the chatRooms to set
     */
    public void setChatRooms(Collection<ChatRoom> chatRooms) {
        this.setChatRooms(chatRooms);
    }

    public String toString(){
    	return name;
    }

    
    public void touchUser(){
    	this.lastTouch = new Date();
    }

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public Date getLastTouch() {
		return lastTouch;
	}
	
	public UserChatSession getChatSessionByRoom(ChatRoom room){
		for( UserChatSession session : chatSessions ){
			if( session.getRoom() == room)
				return session;
		}
		return null;
	}
    
}
