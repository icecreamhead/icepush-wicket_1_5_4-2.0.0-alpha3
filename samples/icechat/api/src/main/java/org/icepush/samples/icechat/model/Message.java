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

import java.util.Date;

/**
 *
 */
public class Message implements Serializable{

    private Long id;

    private Date created;

    private String message;

    private UserChatSession userChatSession;

    private ChatRoom chatRoom;

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
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the user
     */
    public UserChatSession getUserChatSession() {
        return userChatSession;
    }

    /**
     * @param user the user to set
     */
    public void setUserChatSession(UserChatSession userChatSession) {
        this.userChatSession = userChatSession;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the chatRoom
     */
    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    /**
     * @param chatRoom the chatRoom to set
     */
    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

}
