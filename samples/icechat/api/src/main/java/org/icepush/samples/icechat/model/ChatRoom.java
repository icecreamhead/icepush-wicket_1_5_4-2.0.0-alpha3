/*
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
import java.util.List;

/**
 *
 */
public class ChatRoom implements Serializable{

    private static final long serialVersionUID = -6076794430884852218L;

	private String name;

    private Collection<UserChatSession> userChatSessions;

    private Date created;

    private List<Message> messages = new ArrayList<Message>();
    
    private long id;

    public ChatRoom(){
        created = new Date();
        userChatSessions = new ArrayList<UserChatSession>();
        messages = new ArrayList<Message>();
    }
    
    public ChatRoom(long id){
    	super();
    	this.id = id;        
    }

    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the participants
     */
    public Collection<UserChatSession> getUserChatSessions() {
        return userChatSessions;
    }

    /**
     * @param participants the participants to set
     */
    public void setUserChatSessions(Collection<UserChatSession> userChatSessions) {
        this.userChatSessions = userChatSessions;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return the messages
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * @param messages the messages to set
     */
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    
    @Deprecated
    public boolean hasUserSession(String name){
    	for( UserChatSession userSession : userChatSessions ){
    		if( userSession.getUser().getName().equals(name))
    			return true;
    	}
    	return false;
    }
    public boolean isUserInRoom(User user){
    	for(UserChatSession session: userChatSessions){
    		if(session.getUser().equals(user)){
    			return true;
    		}
    	}
    	return false;
    }
    
    
    @Override
    public String toString(){
    	return "<div><button onclick=\"openChatRoom('" + this.name + "');\" style=\"width:100%;\">" + this.name + "</button></div>";
    }


}
