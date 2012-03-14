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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.icepush.samples.icechat.controller.ILoginController;

/**
 *
 */
public final class LoginPage extends AppBasePage {

    @Inject
    ILoginController loginController;
    CompoundPropertyModel compoundLoginController = new CompoundPropertyModel(loginController);

    public LoginPage() {
        super ();

        loginController.setChatService(chatService);
        
        final Form loginForm = new Form("login",compoundLoginController);
        final FeedbackPanel feedbackPanel = new FeedbackPanel("loginMessages");
        feedbackPanel.setOutputMarkupId(true);

        final AjaxButton loginButton = new AjaxButton("loginButton") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                loginController.login(loginController.getUserName());
                setResponsePage(new ChatPage());
            };
        };
        loginForm.add(new RequiredTextField<String>("userName"){
        	protected void onComponentTag(ComponentTag tag){
                    super.onComponentTag(tag);                   
                    tag.put("onkeypress", "if (event.keyCode == 13) { document.getElementById('" + loginButton.getMarkupId() + "').click(); return false;}");
              } 
        });
        loginForm.add(loginButton);

        loginForm.add(feedbackPanel);

        add(loginForm);        
    }

}

