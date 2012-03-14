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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.icepush.samples.icechat.cdi.model.NewChatRoomMessageBean;
import org.icepush.samples.icechat.model.Message;
import org.icepush.samples.icechat.model.UserChatSession;

/**
 *
 */
public final class ChatPanel extends PushPanel {

    private final ListView messagesListView;
    private final ListView usersListView;

    NewChatRoomMessageBean composingMessage = new NewChatRoomMessageBean();

    String messageInput = "";

    @Inject
    ChatManagerViewControllerSessionBean chatManagerVC;
    CompoundPropertyModel compoundChatManagerVC;

    Form chatRoomForm;

    public ChatPanel(String id) {
        super(id);
        compoundChatManagerVC = new CompoundPropertyModel(chatManagerVC);
        chatManagerVC.setPushRequestContext(pushRequestContext);

        chatRoomForm = new Form("chatRoomForm",compoundChatManagerVC);
        chatRoomForm.setOutputMarkupId(true);
        chatRoomForm.add(new Label("currentChatSessionHolder.session.room.name"));

        chatRoomForm.add(usersListView = new ListView("currentChatSessionHolder.session.room.userChatSessions"){
            public void populateItem(final ListItem listItem){
                final UserChatSession userChatSession = (UserChatSession)listItem.getModelObject();
                listItem.add(new Label("userName",userChatSession.getUser().getName()));

            }
        });

        chatRoomForm.add(messagesListView = new ListView("currentChatSessionHolder.session.room.messages"){
            public void populateItem(final ListItem listItem){
                final Message message = (Message)listItem.getModelObject();
                listItem.add(new Label("created",message.getCreated().toString()));
                listItem.add(new Label("userChatSession.user.name",message.getUserChatSession().getUser().getName()));
                listItem.add(new Label("message",message.getMessage()));

            }
        });
        final AjaxButton sendBtn = new AjaxButton("send") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                composingMessage.setMessage(messageInput);
                chatManagerVC.setNewChatRoomMessageBean(composingMessage);
                chatManagerVC.sendNewMessage();
                messageInput = "";
            }
        };
        chatRoomForm.add(new TextField("messageInput",new PropertyModel(this,"messageInput")){
        	protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.put("onkeypress",
						"if (event.keyCode == 13) { document.getElementById('"
								+ sendBtn.getMarkupId()
								+ "').click(); return false;}");
			}
        });
        chatRoomForm.add(sendBtn);

        add(chatRoomForm);
    }

    // PUSH CALLBACK
    protected void pushCallback(AjaxRequestTarget target) {
        usersListView.modelChanged();
        messagesListView.modelChanged();
        target.addComponent(chatRoomForm);
    }

    
    //These two methods will cause the component to render.
    @Override
     protected boolean callOnBeforeRenderIfNotVisible(){
        return true;
    }

    @Override
        protected void onBeforeRender () {
        super.onBeforeRender();
        if(chatManagerVC.getCurrentChatSessionHolder().getSession() !=null){
            usersListView.modelChanged();
            messagesListView.modelChanged();
            setVisible(true);
        }else{
            setVisible(false);
        }
    }
    
    /*This method will also cause the component to render, but is less efficient as this method is called many times in the lifecycle.
    @Override
        public boolean isVisible(){
        if(chatManagerVC.getCurrentChatSessionHolder().getSession() !=null){
            return true;
        }else{
            return false;
        }
     }*/
    
}
